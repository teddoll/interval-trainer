package com.teddoll.fitness.intervaltrainer.session;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
import com.teddoll.fitness.intervaltrainer.util.DBStringParseUtil;
import com.teddoll.fitness.intervaltrainer.util.UnitsUtil;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * Created by teddydoll on 6/10/16.
 */
public class SessionDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback {


    private static final int SESSION_LOADER = 0;
    private static final int LOCATION_LOADER = 1;

    private ListView mListView;
    private View mExpand;
    private View mCollapse;
    private GoogleMap mMap;
    private SessionSelectionListener mListener;
    private List<Location> mPLineLocations;
    private List<SessionInterval> mIntervalLocations;
    private View mContentView;
    private View mEmptyView;

    private long mSessionId;

    public static SessionDetailFragment newInstance(long SessionId) {
        Bundle args = new Bundle();
        args.putLong("sessionId", SessionId);
        SessionDetailFragment fragment = new SessionDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mSessionId = getArguments().getLong("sessionId", -1);
        } else {
            mSessionId = -1;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_detail, container, false);
        mContentView = view.findViewById(R.id.content);
        mEmptyView = view.findViewById(R.id.empty);
        if(mSessionId < 0) {
            hideContent();
        }
        mListView = (ListView) view.findViewById(R.id.list);
        mExpand = view.findViewById(R.id.expand_list);
        mCollapse = view.findViewById(R.id.collapse_list);
        view.findViewById(R.id.list_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListView.getVisibility() == View.GONE) {
                    showList();
                } else {
                    hideList();
                }
            }
        });
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(getContext());
        if (result == ConnectionResult.SUCCESS) {
            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.map_container, mapFragment);
            fragmentTransaction.commit();
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mSessionId >= 0) {
            LoaderManager lm = getLoaderManager();
            lm.initLoader(SESSION_LOADER, null, this);
            lm.initLoader(LOCATION_LOADER, null, this);
        }
    }

    private void hideList() {
        mListView.setVisibility(View.GONE);
        mExpand.setVisibility(View.VISIBLE);
        mCollapse.setVisibility(View.GONE);
    }

    private void showList() {
        mListView.setVisibility(View.VISIBLE);
        mExpand.setVisibility(View.GONE);
        mCollapse.setVisibility(View.VISIBLE);
    }

    private void hideContent() {
        mContentView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        mContentView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }

    public void onSessionIdChange(long sessionId) {
        Timber.d("onSessionIdChange: " + sessionId);
        showContent();
        mSessionId = sessionId;
        LoaderManager lm = getLoaderManager();
        if (lm.getLoader(SESSION_LOADER) != null) {
            Timber.d("SESSION_LOADER restart: " + sessionId);
            lm.restartLoader(SESSION_LOADER, null, this);
        } else {
            Timber.d("SESSION_LOADER init: " + sessionId);
            lm.initLoader(SESSION_LOADER, null, this);
        }
        if (lm.getLoader(LOCATION_LOADER) != null) {
            Timber.d("LOCATION_LOADER restart: " + sessionId);
            lm.restartLoader(LOCATION_LOADER, null, this);
        } else {
            Timber.d("LOCATION_LOADER init: " + sessionId);
            lm.initLoader(LOCATION_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.d("onCreateLoader: " + id + "| session: " + mSessionId);

        if (id == SESSION_LOADER) {
            return new CursorLoader(getContext(),
                    IntervalContract.SessionEntry.buildSessionUri(mSessionId),
                    null,
                    null,
                    null,
                    null);
        } else if (id == LOCATION_LOADER) {
            return new CursorLoader(getContext(),
                    IntervalContract.LocationEntry.CONTENT_URI,
                    null,
                    null,
                    new String[]{String.valueOf(mSessionId)},
                    null);
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == SESSION_LOADER) {
            if (data != null && data.moveToFirst()) {
                String plineData = data.getString(
                        data.getColumnIndex(IntervalContract.SessionEntry.POLY_LINE_DATA));
                mPLineLocations = DBStringParseUtil.deserializeLocations(plineData);
            }
        } else if (loader.getId() == LOCATION_LOADER) {
            mIntervalLocations = new ArrayList<>();
            if (data != null) {
                mIntervalLocations = new ArrayList<>(data.getCount());
                while (data.moveToNext()) {
                    mIntervalLocations.add(new SessionInterval(
                            DBStringParseUtil.deserializeLocation(
                                    data.getString(
                                            data.getColumnIndex(IntervalContract.LocationEntry.LOCATION))),
                            data.getDouble(
                                    data.getColumnIndex(IntervalContract.LocationEntry.DISTANCE)),
                            data.getDouble(
                                    data.getColumnIndex(IntervalContract.LocationEntry.AVERAGE_VELOCITY))));
                }
                mListView.setAdapter(new LocationAdapter(getContext(), data));
            }

        }

        loadMap();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("onLoaderReset: " + loader.getId() + "| session: " + mSessionId);
    }

    private void loadMap() {
        if (mMap != null && mPLineLocations != null && mIntervalLocations != null) {
            mMap.clear();
            PolylineOptions opts = new PolylineOptions();
            for (Location l : mPLineLocations) {
                opts.add(new LatLng(l.getLatitude(), l.getLongitude()));
            }
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int density = (int) metrics.density;
            opts.width(density * 2).color(Color.RED);
            mMap.addPolyline(opts);

            List<MarkerOptions> markers = new ArrayList<>(mIntervalLocations.size());
            for (SessionInterval m : mIntervalLocations) {
                if (m.location == null) continue;
                markers.add(new MarkerOptions()
                        .position(new LatLng(m.location.getLatitude(), m.location.getLongitude()))
                        .title(getString(R.string.mph, UnitsUtil.metersPerSecondToMilesPerHour(m.velocity)))
                        .snippet(getString(R.string.miles, UnitsUtil.metersToMiles(m.distance)))
                );
            }
            if (markers.size() > 0) {
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                for (MarkerOptions m : markers) {
                    mMap.addMarker(m);
                    boundsBuilder.include(m.getPosition());
                }
                LatLngBounds bounds = boundsBuilder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, density * 96);
                mMap.animateCamera(cu);
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    class LocationAdapter extends CursorAdapter {

        public LocationAdapter(@NonNull Context context, @NonNull Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_session_detail, viewGroup, false);
            ViewHolder vh = new ViewHolder();
            vh.velocity = (TextView) view.findViewById(R.id.velocity);
            vh.distance = (TextView) view.findViewById(R.id.distance);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder) view.getTag();

            vh.velocity.setText(getString(R.string.mph,
                    UnitsUtil.metersPerSecondToMilesPerHour(cursor.getDouble(
                            cursor.getColumnIndex(IntervalContract.LocationEntry.AVERAGE_VELOCITY)))));

            vh.distance.setText(getString(R.string.miles, UnitsUtil.metersToMiles(cursor.getDouble(
                    cursor.getColumnIndex(IntervalContract.LocationEntry.DISTANCE)))));
        }

    }

    class ViewHolder {
        TextView velocity;
        TextView distance;

    }
}


