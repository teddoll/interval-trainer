package com.teddoll.fitness.intervaltrainer.data.intervalset;

import android.net.Uri;
import android.provider.BaseColumns;

import com.teddoll.fitness.intervaltrainer.data.IntervalProvider;
import com.teddoll.fitness.intervaltrainer.data.intervallocation.IntervalLocationColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalsession.IntervalSessionColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalset.IntervalSetColumns;

/**
 * Interval Set.
 */
public class IntervalSetColumns implements BaseColumns {
    public static final String TABLE_NAME = "interval_set";
    public static final Uri CONTENT_URI = Uri.parse(IntervalProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    /**
     * Primary key.
     */
    public static final String _ID = BaseColumns._ID;

    public static final String LABEL = "label";

    public static final String TOTAL_TIME = "total_time";

    public static final String TIMES = "times";


    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] ALL_COLUMNS = new String[] {
            _ID,
            LABEL,
            TOTAL_TIME,
            TIMES
    };
    // @formatter:on

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (c.equals(LABEL) || c.contains("." + LABEL)) return true;
            if (c.equals(TOTAL_TIME) || c.contains("." + TOTAL_TIME)) return true;
            if (c.equals(TIMES) || c.contains("." + TIMES)) return true;
        }
        return false;
    }

}
