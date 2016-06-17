package com.teddoll.fitness.intervaltrainer.session;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
import com.teddoll.fitness.intervaltrainer.util.DBStringParseUtil;
import com.teddoll.fitness.intervaltrainer.util.UnitsUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by teddydoll on 5/29/16.
 */
public class SessionListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int SESSION_LOADER = 0;

    private ListView mListView;
    private SessionSelectionListener mListener;
    private int mSelected;
    private boolean mShouldHighlight;


    public static SessionListFragment newInstance(int selected, boolean shouldHighlight) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        args.putInt("selected", selected);
        args.putBoolean("shouldHighlight", shouldHighlight);
        fragment.setArguments(args);
        return fragment;
    }

    public SessionListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelected = getArguments().getInt("selected", 0);
        mShouldHighlight = getArguments().getBoolean("shouldHighlight", false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SESSION_LOADER, null, this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_list, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setEmptyView(view.findViewById(R.id.empty));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelected = i;
                ((SessionAdapter) mListView.getAdapter()).setSelected(mSelected);
                Cursor cur = (Cursor) adapterView.getAdapter().getItem(i);
                cur.moveToPosition(i);
                int id = cur.getInt(cur.getColumnIndex(IntervalContract.SessionEntry._ID));
                if (mListener != null) {
                    mListener.onSessionSelected(id);
                }

            }
        });

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SessionSelectionListener) context;
        } catch (ClassCastException e) {
            Timber.e(context.getClass().getName()
                    + " must implement SessionSelectionListener", e);
            throw e;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("onCreateLoader");
        return new CursorLoader(getContext(),
                IntervalContract.SessionEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("onLoadFinished");
        if (data != null && data.getCount() > 0) {
            if(mSelected < 0 || mSelected >= data.getCount() ) {
                mSelected = 0;
            }
            data.moveToPosition(mSelected);
            mListener.onSessionReady(data.getLong(data.getColumnIndex(IntervalContract.SessionEntry._ID)));
            SessionAdapter adapter = new SessionAdapter(getContext(), data, mShouldHighlight);
            adapter.setSelected(mSelected);
            mListView.setAdapter(adapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("onLoaderReset");
        mListView.setAdapter(null);
    }

    public int getSelected() {
        return mSelected;
    }


    static class SessionAdapter extends CursorAdapter {

        public static final String DATE_FORMAT = "MMM dd";

        private SimpleDateFormat mDateFormat;
        private int mSelected;
        private boolean mShouldSelect;

        public SessionAdapter(@NonNull Context context, @NonNull Cursor cursor, boolean shouldSelect) {
            super(context, cursor, 0);
            mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            mSelected = 0;
            mShouldSelect = shouldSelect;
        }

        public void setSelected(int i) {
            mSelected = i;
            notifyDataSetChanged();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_session, viewGroup, false);
            ViewHolder vh = new ViewHolder();
            vh.date = (TextView) view.findViewById(R.id.date);
            vh.time = (TextView) view.findViewById(R.id.time);
            vh.distance = (TextView) view.findViewById(R.id.distance);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int position = cursor.getPosition();
            view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            if (position == mSelected && mShouldSelect) {
                view.setBackgroundColor(context.getResources().getColor(R.color.list_selected));
            }
            ViewHolder vh = (ViewHolder) view.getTag();
            try {
                Date start = DBStringParseUtil.deserializeDate(
                        cursor.getString(
                                cursor.getColumnIndex(IntervalContract.SessionEntry.START_TIME)));
                Date end = DBStringParseUtil.deserializeDate(
                        cursor.getString(
                                cursor.getColumnIndex(IntervalContract.SessionEntry.END_TIME)));
                float distanceInMeters = cursor.getFloat(
                        cursor.getColumnIndex(IntervalContract.SessionEntry.DISTANCE_TRAVELED));

                if (start != null) vh.date.setText(mDateFormat.format(start));
                if (start != null && end != null) vh.time.setText(context.getString(
                        R.string.minutes, (end.getTime() - start.getTime()) / 60000f));

                vh.distance.setText(context.getString(R.string.miles,
                        UnitsUtil.metersToMiles(distanceInMeters)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        class ViewHolder {
            TextView date;
            TextView time;
            TextView distance;
        }
    }


}
