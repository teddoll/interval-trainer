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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.teddoll.fitness.intervaltrainer.BuildConfig;
import com.teddoll.fitness.intervaltrainer.session.LandingActivity;
import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
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

    private List<Long> mSet;
    private int mCurrentSetSplitIndex;

    private int mSessionId = -1;

    private SessionTracker mVelocityTracker;

    private ArrayList<Messenger> mClients;


    final Messenger mMessenger = new Messenger(new IncomingHandler());


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
    private CountDownTimer mCountDownTimer;


    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
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
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.tag("IntervalService");

        mClients = new ArrayList<>();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.d("Received onStartCommand ");
        if (intent == null || intent.getAction() == null) return START_STICKY;
        if (intent.getAction().equals(START_SESSION)) {
            Timber.d("Received START_SESSION ");
            if (mIsStarted) return START_STICKY;
            mIsStarted = true;
            int setId = intent.getIntExtra(SET_ID_ARG, -1);
            if (setId < 0) {
                //TODO send Error broadcast.
                stopSelf();
                return START_STICKY;
            }
            mSet = getSetFromId(setId);
            if (mSet == null || mSet.size() == 0) {
                //TODO send Error broadcast.
                stopSelf();
                return START_STICKY;
            }
            mCurrentSetSplitIndex = 0;
            localStartForeground();
            startTracking();
        }  else if (intent.getAction().equals(QUIT_SESSION)) {
            Timber.d("Received QUIT_SESSION");
            mIsStarted = false;
            if (mCountDownTimer != null) mCountDownTimer.cancel();
            end();
            stopForeground(true);
            stopSelf();

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private void localStartForeground() {
        Intent notificationIntent = new Intent(this, LandingActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Session Started")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(111,
                notification);
    }

    private void startTracking() {
        Timber.d("startTracking()");
        new Thread(new Runnable() {
            @Override
            public void run() {


                mGoogleApiClient = new GoogleApiClient.Builder(IntervalService.this)
                        .addApi(LocationServices.API)
                        .build();

                mGoogleApiClient.blockingConnect();
                new Handler(IntervalService.this.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                LocationRequest locationRequest = new LocationRequest();
                                locationRequest.setInterval(5000);
                                locationRequest.setFastestInterval(3000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                mLocationsBuffer = new ArrayList<>(10);


                                try

                                {
                                    Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                                    storeInitialIntervalSession(currentLocation);
                                    mRunningLocationString = DBStringParseUtil.serializeLocation(currentLocation);
                                    mVelocityTracker = new SessionTracker();
                                    mVelocityTracker.start(currentLocation);
                                    time();


                                    LocationServices.FusedLocationApi.requestLocationUpdates(
                                            mGoogleApiClient, locationRequest, IntervalService.this);

                                } catch (SecurityException e) {
                                    //TODO send error broadcast.
                                    stopSelf();
                                }
                            }

                        });
            }


        }).start();

    }

    private void time() {
        Timber.d("time()");

        mCountDownTimer = new CountDownTimer(mSet.get(mCurrentSetSplitIndex) * 60 * 1000, 500) {

            public void onTick(long millisUntilFinished) {
                for (int i = mClients.size() - 1; i >= 0; i--) {
                    try {
                        Bundle b = new Bundle();
                        Message m = Message.obtain(null,
                                MSG_UPDATE, (int) millisUntilFinished, (int) (mVelocityTracker.getTotalDistance()));
                        m.setData(b);
                        mClients.get(i).send(m);
                    } catch (RemoteException e) {
                        mClients.remove(i);
                    }
                }
            }

            public void onFinish() {
                if (mCurrentSetSplitIndex + 1 < mSet.size() - 1) {
                    mCurrentSetSplitIndex++;
                    //TODO test order of calls.
                    sendUpdateNotification();
                    trackSplitLocation();
                    time();
                } else {
                    end();
                }
            }
        };
        mCountDownTimer.start();

    }

    private void sendUpdateNotification() {
//Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Session Interval Hit")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setVibrate(new long[]{750, 500, 750, 500, 750, 500, 750, 500});


//Display notification
        notificationManager.notify(111, mBuilder.build());
    }

    private void sendFinalNotification() {
//Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Session Complete")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setVibrate(new long[]{750, 500, 750, 500, 750, 500, 750, 500});


//Display notification
        notificationManager.notify(222, builder.build());
    }

    private void end() {
        Timber.d("end()");
        sendFinalNotification();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        trackSplitLocation();
        flushLocationBuffer();
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                mClients.get(i).send(Message.obtain(null,
                        MSG_COMPLETE));
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
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

    private List<Long> getSetFromId(int setId) {
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


    private void storeInitialIntervalSession(Location location) {
        Date date = new Date();
        ContentValues values = new ContentValues();
        String serialLocation = DBStringParseUtil.serializeLocation(location);
        values.put(IntervalContract.SessionEntry.START_TIME,
                DBStringParseUtil.serializeDate(date));
        values.put(IntervalContract.SessionEntry.POLY_LINE_DATA,
                serialLocation);
        Uri uri = getContentResolver().insert(IntervalContract.SessionEntry.CONTENT_URI, values);
        if (uri != null) {
            mSessionId = Integer.parseInt(uri.getPathSegments().get(1));
        } else {
            //TODO error handle.
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        appendToLocationBuffer(location);
    }

    private synchronized void appendToLocationBuffer(Location location) {
        mVelocityTracker.update(location);
        mLocationsBuffer.add(location);
        if (mLocationsBuffer.size() >= 10) {
            flushLocationBuffer();
        }

    }

    private void flushLocationBuffer() {
        mRunningLocationString += "|" + DBStringParseUtil.serializeLocations(mLocationsBuffer);
        ContentValues vals = new ContentValues();
        vals.put(IntervalContract.SessionEntry.POLY_LINE_DATA, mRunningLocationString);
        getContentResolver().update(IntervalContract.SessionEntry.CONTENT_URI, vals,
                IntervalContract.SessionEntry._ID + "=?", new String[]{String.valueOf(mSessionId)});
        mLocationsBuffer.clear();
    }

}
