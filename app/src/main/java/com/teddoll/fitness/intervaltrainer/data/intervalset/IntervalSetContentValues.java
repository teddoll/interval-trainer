package com.teddoll.fitness.intervaltrainer.data.intervalset;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code interval_set} table.
 */
public class IntervalSetContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return IntervalSetColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable IntervalSetSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable IntervalSetSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public IntervalSetContentValues putLabel(@Nullable String value) {
        mContentValues.put(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetContentValues putLabelNull() {
        mContentValues.putNull(IntervalSetColumns.LABEL);
        return this;
    }

    public IntervalSetContentValues putTotalTime(@Nullable Integer value) {
        mContentValues.put(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetContentValues putTotalTimeNull() {
        mContentValues.putNull(IntervalSetColumns.TOTAL_TIME);
        return this;
    }

    public IntervalSetContentValues putTimes(@Nullable String value) {
        mContentValues.put(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetContentValues putTimesNull() {
        mContentValues.putNull(IntervalSetColumns.TIMES);
        return this;
    }
}
