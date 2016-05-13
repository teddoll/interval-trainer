package com.teddoll.fitness.intervaltrainer.data.intervalsession;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code interval_session} table.
 */
public class IntervalSessionCursor extends AbstractCursor implements IntervalSessionModel {
    public IntervalSessionCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(IntervalSessionColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code start_time} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getStartTime() {
        String res = getStringOrNull(IntervalSessionColumns.START_TIME);
        return res;
    }

    /**
     * Get the {@code poly_line_data} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getPolyLineData() {
        String res = getStringOrNull(IntervalSessionColumns.POLY_LINE_DATA);
        return res;
    }
}
