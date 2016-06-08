package com.teddoll.fitness.intervaltrainer.tracking;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.service.IntervalService;
import com.teddoll.fitness.intervaltrainer.util.UnitsUtil;

import java.util.Arrays;

import timber.log.Timber;

public class TrackingActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    /**
     * Messenger for communicating with service.
     */
    private Messenger mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    private boolean mIsBound;

    private Messenger mMessenger;
    private IncomingHandler mHandler;

    private TextView mTime;
    private TextView mDistance;

    private boolean mStarted;
    private View mStartView;
    private View mStopView;

    private int mSetId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Timber.d("onCreate");
        mSetId = getIntent().getIntExtra("setId", -1);
        if (mSetId < 0) {
            finish();
            return;
        }

        mTime = (TextView) findViewById(R.id.time);
        mDistance = (TextView) findViewById(R.id.distance);

        mStartView = findViewById(R.id.start);
        mStartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionCheck();
            }
        });


        mStopView = findViewById(R.id.stop);
        mStopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateState(false);
                Intent startServiceIntent = new Intent(TrackingActivity.this, IntervalService.class);
                startServiceIntent.setAction(IntervalService.QUIT_SESSION);
                startService(startServiceIntent);
            }
        });

        mDistance.setText("");
        mTime.setText(R.string.get_started);
        mStarted = true;
        updateState(false);

    }

    private void permissionCheck() {
        Timber.d("permissionCheck");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        } else {
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Timber.d("onRequestPermissionsResult " + requestCode + " " + Arrays.deepToString(permissions) + " " + Arrays.toString(grantResults));
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start();
                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showRationale();
                } else {
                    permissionCheck();
                }
            }
        }
    }

    private void showRationale() {
        Timber.d("showRationale");
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.location_rationale))
                .setPositiveButton(getString(R.string.location_rationale_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    private void start() {
        updateState(true);
        Intent startServiceIntent = new Intent(TrackingActivity.this, IntervalService.class);
        startServiceIntent.putExtra(IntervalService.SET_ID_ARG, mSetId);
        startServiceIntent.setAction(IntervalService.START_SESSION);
        Timber.d("StartService");
        startService(startServiceIntent);
    }

    private void updateState(boolean started) {
        if (started != mStarted) {
            mStarted = started;

            if (mStarted) {
                mStartView.setVisibility(View.GONE);
                mStopView.setVisibility(View.VISIBLE);
            } else {
                mStartView.setVisibility(View.VISIBLE);
                mStopView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        Timber.d("onResume");
        super.onResume();
        mHandler = new IncomingHandler(this, mTime, mDistance);
        mMessenger = new Messenger(mHandler);
        loadServiceConnection();
        doBindService();
    }

    @Override
    protected void onPause() {
        Timber.d("onPause");
        super.onPause();
        doUnbindService();
        mHandler.clear();
    }


    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {

        private Context mContext;
        private TextView mTimeTextView;
        private TextView mDistanceTextView;

        IncomingHandler(Context context, TextView time, TextView distance) {
            super();
            mContext = context;
            mTimeTextView = time;
            mDistanceTextView = distance;
        }

        public void clear() {
            mContext = null;
            mTimeTextView = null;
            mDistanceTextView = null;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mContext == null) return;
            switch (msg.what) {
                case IntervalService.MSG_UPDATE:
                    updateState(true);
                    int timeMilli = msg.arg1;
                    int minute = timeMilli / 60000;
                    int sec = timeMilli / 1000;
                    mTimeTextView.setText(mContext.getString(R.string.time_format, minute, sec));
                    mDistanceTextView.setText(mContext.getString(R.string.distance_format,
                            UnitsUtil.metersToMiles((float) msg.arg2)));
                    break;
                case IntervalService.MSG_COMPLETE:
                    updateState(false);
                    mTimeTextView.setText(R.string.session_complete);
                    mDistanceTextView.setText(mContext.getString(R.string.distance_format,
                            UnitsUtil.metersToMiles((float) msg.arg2)));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection;

    private void loadServiceConnection() {
        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                Timber.d("onServiceConnected");
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  We are communicating with our
                // service through an IDL interface, so get a client-side
                // representation of that from the raw service object.
                mService = new Messenger(service);

                // We want to monitor the service for as long as we are
                // connected to it.
                try {
                    Message msg = Message.obtain(null,
                            IntervalService.MSG_REGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);

                } catch (RemoteException e) {
                    // In this case the service has crashed before we could even
                    // do anything with it; we can count on soon being
                    // disconnected (and then reconnected if it can be restarted)
                    // so there is no need to do anything here.
                }

            }

            public void onServiceDisconnected(ComponentName className) {
                Timber.d("onServiceDisconnected");
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                mService = null;


            }
        };
    }


    void doBindService() {
        Timber.d("doBindService");
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(TrackingActivity.this,
                IntervalService.class), mConnection, 0);
        mIsBound = true;
    }

    void doUnbindService() {
        Timber.d("doUnbindService");
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            IntervalService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

}
