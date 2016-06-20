package com.teddoll.fitness.intervaltrainer.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.prefs.AppPrefs;
import com.teddoll.fitness.intervaltrainer.set.SetSelectionActivity;

import timber.log.Timber;

public class LandingActivity extends AppCompatActivity implements SessionSelectionListener {

    private SessionDetailFragment mSessionDetailFragment;
    private SessionListFragment mSessionListFragment;
    private AppPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Timber.d("onCreate");
        mPrefs = AppPrefs.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingActivity.this, SetSelectionActivity.class));
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        int selected = 0;
        boolean showHighlight = false;
        if(findViewById(R.id.detail_container) != null) {
            showHighlight = true;
            mSessionDetailFragment = SessionDetailFragment.newInstance(-1);
            fm.beginTransaction().replace(R.id.detail_container,
                    mSessionDetailFragment, "DETAIL").commit();
        }
        if(savedInstanceState != null) {
            selected = savedInstanceState.getInt("selected", 0);
        }
        mSessionListFragment = SessionListFragment.newInstance(selected, showHighlight);
        fm.beginTransaction().replace(R.id.list_container,
                mSessionListFragment, "LIST").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        long last = mPrefs.getLastTipTime();
        long now = System.currentTimeMillis();
        if(now - last > AppPrefs.TIP_DELTA_DISPLAY_TIME) {
            String tip = mPrefs.getTip();
            mPrefs.setLastTipTime(now);
            new AlertDialog.Builder(this)
                    .setMessage(tip)
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected", mSessionListFragment.getSelected());
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

    @Override
    public void onSessionSelected(long sessionId) {
        Timber.d("onSessionSelected");
        if (mSessionDetailFragment != null) {
            mSessionDetailFragment.onSessionIdChange(sessionId);
        } else {
            Intent i = new Intent(this, SessionDetailActivity.class);
            i.putExtra("sessionId", sessionId);
            startActivity(i);
        }
    }

    @Override
    public void onSessionReady(long sessionId) {
        Timber.d("onSessionReady");
        if (mSessionDetailFragment != null) {
            mSessionDetailFragment.onSessionIdChange(sessionId);
        }
    }
}
