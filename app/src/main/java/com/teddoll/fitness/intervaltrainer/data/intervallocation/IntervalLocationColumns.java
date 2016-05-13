package com.teddoll.fitness.intervaltrainer.data.intervallocation;

import android.net.Uri;
import android.provider.BaseColumns;

import com.teddoll.fitness.intervaltrainer.data.IntervalProvider;
import com.teddoll.fitness.intervaltrainer.data.intervallocation.IntervalLocationColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalsession.IntervalSessionColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalset.IntervalSetColumns;

/**
 * Interval Location
 */
public class IntervalLocationColumns implements BaseColumns {
    public static final String TABLE_NAME = "interval_location";
    public static final Uri CONTENT_URI = Uri.parse(IntervalProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String INTERVAL_SESSION_ID = "interval_session_id";

    public static final String LOCATION = "location";

    public static final String AVERAGE_VELOCITY = "average_velocity";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            INTERVAL_SESSION_ID,
            LOCATION,
            AVERAGE_VELOCITY
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(INTERVAL_SESSION_ID) || c.contains("." + INTERVAL_SESSION_ID)) return true;
            if (c.equals(LOCATION) || c.contains("." + LOCATION)) return true;
            if (c.equals(AVERAGE_VELOCITY) || c.contains("." + AVERAGE_VELOCITY)) return true;
        }
        return false;
    }

    public static final String PREFIX_INTERVAL_SESSION = TABLE_NAME + "__" + IntervalSessionColumns.TABLE_NAME;
}
