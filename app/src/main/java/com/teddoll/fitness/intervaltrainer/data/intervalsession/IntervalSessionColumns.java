package com.teddoll.fitness.intervaltrainer.data.intervalsession;

import android.net.Uri;
import android.provider.BaseColumns;

import com.teddoll.fitness.intervaltrainer.data.IntervalProvider;
import com.teddoll.fitness.intervaltrainer.data.intervallocation.IntervalLocationColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalsession.IntervalSessionColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalset.IntervalSetColumns;

/**
 * Interval Set.
 */
public class IntervalSessionColumns implements BaseColumns {
    public static final String TABLE_NAME = "interval_session";
    public static final Uri CONTENT_URI = Uri.parse(IntervalProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String START_TIME = "start_time";

    public static final String POLY_LINE_DATA = "poly_line_data";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            START_TIME,
            POLY_LINE_DATA
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(START_TIME) || c.contains("." + START_TIME)) return true;
            if (c.equals(POLY_LINE_DATA) || c.contains("." + POLY_LINE_DATA)) return true;
        }
        return false;
    }

}
