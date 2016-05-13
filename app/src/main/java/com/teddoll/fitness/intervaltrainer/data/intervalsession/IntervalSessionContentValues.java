package com.teddoll.fitness.intervaltrainer.data.intervalsession;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code interval_session} table.
 */
public class IntervalSessionContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return IntervalSessionColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable IntervalSessionSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable IntervalSessionSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public IntervalSessionContentValues putStartTime(@Nullable String value) {
        mContentValues.put(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionContentValues putStartTimeNull() {
        mContentValues.putNull(IntervalSessionColumns.START_TIME);
        return this;
    }

    public IntervalSessionContentValues putPolyLineData(@Nullable String value) {
        mContentValues.put(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionContentValues putPolyLineDataNull() {
        mContentValues.putNull(IntervalSessionColumns.POLY_LINE_DATA);
        return this;
    }
}
