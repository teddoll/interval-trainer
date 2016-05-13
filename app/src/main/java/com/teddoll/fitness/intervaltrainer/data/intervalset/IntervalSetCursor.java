package com.teddoll.fitness.intervaltrainer.data.intervalset;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code interval_set} table.
 */
public class IntervalSetCursor extends AbstractCursor implements IntervalSetModel {
    public IntervalSetCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(IntervalSetColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code label} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getLabel() {
        String res = getStringOrNull(IntervalSetColumns.LABEL);
        return res;
    }

    /**
     * Get the {@code total_time} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getTotalTime() {
        Integer res = getIntegerOrNull(IntervalSetColumns.TOTAL_TIME);
        return res;
    }

    /**
     * Get the {@code times} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getTimes() {
        String res = getStringOrNull(IntervalSetColumns.TIMES);
        return res;
    }
}
