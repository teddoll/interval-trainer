package com.teddoll.fitness.intervaltrainer.set;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
import com.teddoll.fitness.intervaltrainer.edit.EditActivity;
import com.teddoll.fitness.intervaltrainer.tracking.TrackingActivity;

import timber.log.Timber;

/**
 * Created by teddydoll on 5/29/16.
 */
public class SetSelectionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_selection);
        Timber.tag("LandingActivity");
        Timber.d("Activity Created");
        mListView = (ListView) findViewById(R.id.list);
        mListView.setEmptyView(findViewById(R.id.empty));
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SetSelectionActivity.this, EditActivity.class));
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                IntervalContract.SetEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListView.setAdapter(new SetAdapter(this, data, new SetAdapter.SetAdapterListener() {
            @Override
            public void onEditClick(int id) {
                Intent intent = new Intent(SetSelectionActivity.this, EditActivity.class);
                intent.putExtra(EditActivity.EDIT_ID_ARG, id);
                startActivity(intent);
            }

            @Override
            public void onItemClick(int id) {
                Intent intent = new Intent(SetSelectionActivity.this, TrackingActivity.class);
                intent.putExtra("setId", id);
                startActivity(intent);
            }
        }));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static class SetAdapter extends CursorAdapter {

        public interface SetAdapterListener {
            void onEditClick(int id);

            void onItemClick(int id);
        }

        private SetAdapterListener mListener;

        public SetAdapter(Context context, Cursor cursor, SetAdapterListener listener) {
            super(context, cursor, 0);
            mListener = listener;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_set, viewGroup, false);
            ViewHolder vh = new ViewHolder();
            vh.label = (TextView) view.findViewById(R.id.label);
            vh.time = (TextView) view.findViewById(R.id.time);
            vh.edit = (ImageButton) view.findViewById(R.id.edit);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder) view.getTag();
            vh.label.setText(
                    cursor.getString(
                            cursor.getColumnIndex(
                                    IntervalContract.SetEntry.LABEL)));
            vh.time.setText(context.getString(R.string.minute_string_format,
                    cursor.getString(
                            cursor.getColumnIndex(
                                    IntervalContract.SetEntry.TOTAL_TIME))));
            final int id = cursor.getInt(
                    cursor.getColumnIndex(
                            IntervalContract.SetEntry._ID));
            vh.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onEditClick(id);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(id);
                }
            });

        }

        class ViewHolder {
            TextView label;
            TextView time;
            ImageButton edit;

        }
    }


}
