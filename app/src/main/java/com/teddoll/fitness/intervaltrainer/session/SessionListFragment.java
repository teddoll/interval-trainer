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
        return new CursorLoader(getContext(),
                IntervalContract.SessionEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListView.setAdapter(new SessionAdapter(getContext(), data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListView.setAdapter(null);
    }


    class SessionAdapter extends CursorAdapter {

        public static final String DATE_FORMAT = "MMM dd";

        private SimpleDateFormat mDateFormat;
        public SessionAdapter(@NonNull Context context, @NonNull Cursor cursor) {
            super(context, cursor, 0);
            mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
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

                if(start != null) vh.date.setText(mDateFormat.format(start));
                if(start != null && end != null) vh.time.setText((end.getTime() - start.getTime())/ 60000 + " m"  );
                vh.distance.setText(String.format(Locale.US, "%.2f m",
                        UnitsUtil.metersToMiles(distanceInMeters)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    class ViewHolder {
        TextView date;
        TextView time;
        TextView distance;
    }

}
