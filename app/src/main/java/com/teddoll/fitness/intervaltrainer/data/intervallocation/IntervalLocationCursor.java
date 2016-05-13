package com.teddoll.fitness.intervaltrainer.data.intervallocation;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractCursor;
import com.teddoll.fitness.intervaltrainer.data.intervalsession.*;

/**
 * Cursor wrapper for the {@code interval_location} table.
 */
public class IntervalLocationCursor extends AbstractCursor implements IntervalLocationModel {
    public IntervalLocationCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(IntervalLocationColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code interval_session_id} value.
     */
    public long getIntervalSessionId() {
        Long res = getLongOrNull(IntervalLocationColumns.INTERVAL_SESSION_ID);
        if (res == null)
            throw new NullPointerException("The value of 'interval_session_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code start_time} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getIntervalSessionStartTime() {
        String res = getStringOrNull(IntervalSessionColumns.START_TIME);
        return res;
    }

    /**
     * Get the {@code poly_line_data} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getIntervalSessionPolyLineData() {
        String res = getStringOrNull(IntervalSessionColumns.POLY_LINE_DATA);
        return res;
    }

    /**
     * Get the {@code location} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getLocation() {
        String res = getStringOrNull(IntervalLocationColumns.LOCATION);
        return res;
    }

    /**
     * Get the {@code average_velocity} value.
     * Can be {@code null}.
     */
    @Nullable
    public Float getAverageVelocity() {
        Float res = getFloatOrNull(IntervalLocationColumns.AVERAGE_VELOCITY);
        return res;
    }
}
