package com.teddoll.fitness.intervaltrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.teddoll.fitness.intervaltrainer.service.IntervalService;

import timber.log.Timber;

public class LandingActivity extends AppCompatActivity {

    TextView mStatus;
    TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Timber.tag("LandingActivity");
        Timber.d("Activity Created");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent startServiceIntent = new Intent(LandingActivity.this, IntervalService.class);
                startServiceIntent.putExtra(IntervalService.SET_ID_ARG, 2);
                startServiceIntent.setAction(IntervalService.START_SESSION);
                startService(startServiceIntent);


            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startServiceIntent = new Intent(LandingActivity.this, IntervalService.class);
                startServiceIntent.setAction(IntervalService.QUIT_SESSION);
                startService(startServiceIntent);
            }
        });
        mStatus = (TextView) findViewById(R.id.status);
        mTime = (TextView) findViewById(R.id.time);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        ContentValues vals = new ContentValues();
//        List<Long> times = new ArrayList<>(3);
//        times.add(1l);
//        times.add(1l);
//        times.add(1l);
//        vals.put(IntervalContract.SetEntry.LABEL, "TEST");
//        vals.put(IntervalContract.SetEntry.TIMES, DBStringParseUtil.serializeSetTimes(times));
//        vals.put(IntervalContract.SetEntry.TOTAL_TIME, 3l);
//        Uri uri = getContentResolver().insert(IntervalContract.SetEntry.CONTENT_URI, vals);
//        Timber.d("@@@@@@ " +uri.toString());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
