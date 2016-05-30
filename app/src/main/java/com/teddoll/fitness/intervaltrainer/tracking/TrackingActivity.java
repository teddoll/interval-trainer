package com.teddoll.fitness.intervaltrainer.tracking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.service.IntervalService;
import com.teddoll.fitness.intervaltrainer.util.UnitsUtil;

public class TrackingActivity extends AppCompatActivity {



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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int setId = getIntent().getIntExtra("setId", -1);
        if(setId < 0) {
            finish();
            return;
        }

        mTime = (TextView) findViewById(R.id.time);
        mDistance = (TextView) findViewById(R.id.distance);

        View mStartView = findViewById(R.id.start);
        mStartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startServiceIntent = new Intent(TrackingActivity.this, IntervalService.class);
                startServiceIntent.putExtra(IntervalService.SET_ID_ARG, setId);
                startServiceIntent.setAction(IntervalService.START_SESSION);
                startService(startServiceIntent);
            }
        });

        View mStopView = findViewById(R.id.stop);
        mStopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startServiceIntent = new Intent(TrackingActivity.this, IntervalService.class);
                startServiceIntent.setAction(IntervalService.QUIT_SESSION);
                startService(startServiceIntent);
            }
        });

        mDistance.setText("");
        mTime.setText(R.string.get_started);

    }







    @Override
    protected void onResume() {
        super.onResume();
        mHandler = new IncomingHandler(this, mTime, mDistance);
        mMessenger = new Messenger(mHandler);
        doBindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnbindService();
        mHandler.clear();
    }




    /**
     * Handler of incoming messages from service.
     */
    static class IncomingHandler extends Handler {

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
            switch (msg.what) {
                case IntervalService.MSG_UPDATE:
                    int timeMilli = msg.arg1;
                    int minute = timeMilli / 60000;
                    int sec = timeMilli / 1000;
                    mTimeTextView.setText(mContext.getString(R.string.time_format, minute, sec));
                    mDistanceTextView.setText(mContext.getString(R.string.distance_format,
                            UnitsUtil.metersToMiles((float) msg.arg2)));
                    break;
                case IntervalService.MSG_COMPLETE:
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
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
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
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;


        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(TrackingActivity.this,
                IntervalService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
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
