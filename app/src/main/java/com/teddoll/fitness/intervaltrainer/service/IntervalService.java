package com.teddoll.fitness.intervaltrainer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
import com.teddoll.fitness.intervaltrainer.session.LandingActivity;
import com.teddoll.fitness.intervaltrainer.tracking.TrackingActivity;
import com.teddoll.fitness.intervaltrainer.util.DBStringParseUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class IntervalService extends Service implements LocationListener {

    public static final String SET_ID_ARG = "_set_id_";
    public static final String START_SESSION = "_start_session_";
    public static final String QUIT_SESSION = "_quit_session_";

    private boolean mIsStarted;

    private List<Location> mLocationsBuffer;
    private String mRunningLocationString;

    private List<Integer> mSet;
    private int mCurrentSetSplitIndex;

    private int mSessionId = -1;

    private SessionTracker mVelocityTracker;

    private ArrayList<Messenger> mClients;


    private Messenger mMessenger;


    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command from service to update the client on time.
     */
    public static final int MSG_UPDATE = 3;

    /**
     * Command from service to update the client on time.
     */
    public static final int MSG_COMPLETE = 4;

    /**
     * Command in service to handle new command.
     */
    public static final int MSG_HANDLE = 5;

    /**
     * Command from service if not started
     */
    public static final int MSG_READY = 6;


    private CountDownTimer mCountDownTimer;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private long mMillisUntilFinished;


    /**
     * Handler of incoming messages from clients.
     */
    private final class ServiceHandler extends Handler {


        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    if (!mIsStarted) {
                        Message m = Message.obtain(null,
                                MSG_READY);
                        try {
                            msg.replyTo.send(m);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            mClients.remove(msg.replyTo);
                        }
                    } else {
                        sendUpdate();
                    }
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_HANDLE:
                    handleCommand((Intent) msg.obj);
                default:
                    super.handleMessage(msg);
            }
        }
    }


    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;


    public IntervalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.d("onCreate");
        HandlerThread thread = new HandlerThread(getClass().getSimpleName());
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mMessenger = new Messenger(mServiceHandler);
        mClients = new ArrayList<>();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("Received onStartCommand " + startId);
        Message msg = mServiceHandler.obtainMessage(MSG_HANDLE);
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();
        if (mIsStarted) end();
    }


    private synchronized void handleCommand(Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        if (intent.getAction().equals(START_SESSION)) {
            Timber.d("Received START_SESSION mStarted = " + mIsStarted);
            if (mIsStarted) return;
            Timber.d("Starting session ");
            mIsStarted = true;
            int setId = intent.getIntExtra(SET_ID_ARG, -1);
            if (setId < 0) {
                //TODO send Error broadcast.
                stopSelf();
                return;
            }
            mSet = getSetFromId(setId);
            if (mSet == null || mSet.size() == 0) {
                //TODO send Error broadcast.
                stopSelf();
                return;
            }
            mCurrentSetSplitIndex = 0;
            localStartForeground();
            startTracking();
        } else if (intent.getAction().equals(QUIT_SESSION)) {
            Timber.d("Received QUIT_SESSION");
            mIsStarted = false;
            if (mCountDownTimer != null) mCountDownTimer.cancel();
            end();
            stopForeground(true);
            stopSelf();
        }
    }

    private void localStartForeground() {
        Intent notificationIntent = new Intent(this, TrackingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.notification_started))
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();
        startForeground(111,
                notification);
    }

    private void startTracking() {
        Timber.d("startTracking()");


        mGoogleApiClient = new GoogleApiClient.Builder(IntervalService.this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.blockingConnect();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationsBuffer = new ArrayList<>(10);


        try {
            mRunningLocationString = "";
            mVelocityTracker = new SessionTracker();
            mVelocityTracker.start();

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, IntervalService.this);
            storeInitialIntervalSession();
            time();
        } catch (SecurityException e) {
            end();
            stopSelf();
        }


    }

    private void time() {
        Timber.d("time()");
        long startTime = mSet.get(mCurrentSetSplitIndex) * 60 * 1000;
        mMillisUntilFinished = startTime;
        mCountDownTimer = new CountDownTimer(startTime, 500) {

            public void onTick(long millisUntilFinished) {
                mMillisUntilFinished = millisUntilFinished;
                sendUpdate();
            }

            public void onFinish() {
                trackSplitLocation();
                if (mCurrentSetSplitIndex + 1 < mSet.size()) {
                    sendUpdateNotification(mSet.get(mCurrentSetSplitIndex));
                    mCurrentSetSplitIndex++;
                    time();
                } else {
                    end();
                }
            }
        };
        mCountDownTimer.start();

    }

    private void sendUpdate() {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                Message m = Message.obtain(null,
                        MSG_UPDATE, (int) mMillisUntilFinished, (int) (mVelocityTracker.getTotalDistance()));
                mClients.get(i).send(m);
            } catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    private void sendUpdateNotification(int time) {
//Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, TrackingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

//Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_update, time))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSound(soundUri)
                .setVibrate(new long[]{750, 500, 750, 500, 750, 500, 750, 500});


//Display notification
        notificationManager.notify(111, mBuilder.build());
    }

    private void sendFinalNotification() {
//Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, LandingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

//Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_end))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setSound(soundUri)
                .setVibrate(new long[]{750, 500, 750, 500, 750, 500, 750, 500});


//Display notification
        notificationManager.notify(222, builder.build());
    }

    private void end() {
        Timber.d("end()");
        mCountDownTimer.cancel();
        mIsStarted = false;
        sendFinalNotification();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        flushLocationBuffer();
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                Message m = Message.obtain(null,
                        MSG_COMPLETE, 0, (int) (mVelocityTracker.getTotalDistance()));

                mClients.get(i).send(m);
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
        //Update Widgets.
        startService(new Intent(this, TipService.class));
        stopForeground(true);
        stopSelf();


    }

    private void trackSplitLocation() {
        Timber.d("trackSplitLocation()");
        ContentValues vals = new ContentValues();
        vals.put(IntervalContract.LocationEntry.LOCATION,
                DBStringParseUtil.serializeLocation(
                        mVelocityTracker.getLastLocation()));
        vals.put(IntervalContract.LocationEntry.AVERAGE_VELOCITY,
                mVelocityTracker.getCurrentAverageVelocity());
        vals.put(IntervalContract.LocationEntry.INTERVAL_SESSION_ID, mSessionId);
        vals.put(IntervalContract.LocationEntry.DISTANCE, mVelocityTracker.getTrackedDistanceAndUpdate());

        getContentResolver().insert(IntervalContract.LocationEntry.CONTENT_URI, vals);
    }

    private List<Integer> getSetFromId(int setId) {
        Cursor cursor = getContentResolver().query(
                IntervalContract.SetEntry.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(setId)).build(),
                null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String rawSet = cursor.getString(
                        cursor.getColumnIndex(IntervalContract.SetEntry.TIMES));
                return DBStringParseUtil.deserializeSetTimes(rawSet);
            }
            cursor.close();
        }
        return null;
    }


    private void storeInitialIntervalSession() {
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put(IntervalContract.SessionEntry.START_TIME,
                DBStringParseUtil.serializeDate(date));
        values.put(IntervalContract.SessionEntry.POLY_LINE_DATA,
                "");
        Uri uri = getContentResolver().insert(IntervalContract.SessionEntry.CONTENT_URI, values);
        if (uri != null) {
            mSessionId = Integer.parseInt(uri.getPathSegments().get(1));
        } else {
            end();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        appendToLocationBuffer(location);
    }

    private synchronized void appendToLocationBuffer(Location location) {
        Timber.d("Location accuracy: " + location.getAccuracy());
        if(location.getAccuracy() <= 10) {
            mVelocityTracker.update(location);
            mLocationsBuffer.add(location);
            if (mLocationsBuffer.size() >= 10) {
                flushLocationBuffer();
            }
        }

    }

    private void flushLocationBuffer() {
        if (!mRunningLocationString.isEmpty()) mRunningLocationString += "|";
        mRunningLocationString += DBStringParseUtil.serializeLocations(mLocationsBuffer);
        Date date = new Date();

        ContentValues vals = new ContentValues();
        vals.put(IntervalContract.SessionEntry.END_TIME,
                DBStringParseUtil.serializeDate(date));
        vals.put(IntervalContract.SessionEntry.POLY_LINE_DATA, mRunningLocationString);
        vals.put(IntervalContract.SessionEntry.DISTANCE_TRAVELED, mVelocityTracker.getTotalDistance());
        getContentResolver().update(IntervalContract.SessionEntry.CONTENT_URI, vals,
                IntervalContract.SessionEntry._ID + "=?", new String[]{String.valueOf(mSessionId)});
        mLocationsBuffer.clear();
    }

}
