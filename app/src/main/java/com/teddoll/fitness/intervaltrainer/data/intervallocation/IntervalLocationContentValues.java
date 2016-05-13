package com.teddoll.fitness.intervaltrainer.data.intervallocation;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code interval_location} table.
 */
public class IntervalLocationContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return IntervalLocationColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable IntervalLocationSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable IntervalLocationSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public IntervalLocationContentValues putIntervalSessionId(long value) {
        mContentValues.put(IntervalLocationColumns.INTERVAL_SESSION_ID, value);
        return this;
    }


    public IntervalLocationContentValues putLocation(@Nullable String value) {
        mContentValues.put(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationContentValues putLocationNull() {
        mContentValues.putNull(IntervalLocationColumns.LOCATION);
        return this;
    }

    public IntervalLocationContentValues putAverageVelocity(@Nullable Float value) {
        mContentValues.put(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationContentValues putAverageVelocityNull() {
        mContentValues.putNull(IntervalLocationColumns.AVERAGE_VELOCITY);
        return this;
    }
}
