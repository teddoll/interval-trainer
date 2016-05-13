package com.teddoll.fitness.intervaltrainer.data.intervalset;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractSelection;

/**
 * Selection for the {@code interval_set} table.
 */
public class IntervalSetSelection extends AbstractSelection<IntervalSetSelection> {
    @Override
    protected Uri baseUri() {
        return IntervalSetColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code IntervalSetCursor} object, which is positioned before the first entry, or null.
     */
    public IntervalSetCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new IntervalSetCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public IntervalSetCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code IntervalSetCursor} object, which is positioned before the first entry, or null.
     */
    public IntervalSetCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new IntervalSetCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public IntervalSetCursor query(Context context) {
        return query(context, null);
    }


    public IntervalSetSelection id(long... value) {
        addEquals("interval_set." + IntervalSetColumns._ID, toObjectArray(value));
        return this;
    }

    public IntervalSetSelection idNot(long... value) {
        addNotEquals("interval_set." + IntervalSetColumns._ID, toObjectArray(value));
        return this;
    }

    public IntervalSetSelection orderById(boolean desc) {
        orderBy("interval_set." + IntervalSetColumns._ID, desc);
        return this;
    }

    public IntervalSetSelection orderById() {
        return orderById(false);
    }

    public IntervalSetSelection label(String... value) {
        addEquals(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetSelection labelNot(String... value) {
        addNotEquals(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetSelection labelLike(String... value) {
        addLike(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetSelection labelContains(String... value) {
        addContains(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetSelection labelStartsWith(String... value) {
        addStartsWith(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetSelection labelEndsWith(String... value) {
        addEndsWith(IntervalSetColumns.LABEL, value);
        return this;
    }

    public IntervalSetSelection orderByLabel(boolean desc) {
        orderBy(IntervalSetColumns.LABEL, desc);
        return this;
    }

    public IntervalSetSelection orderByLabel() {
        orderBy(IntervalSetColumns.LABEL, false);
        return this;
    }

    public IntervalSetSelection totalTime(Integer... value) {
        addEquals(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetSelection totalTimeNot(Integer... value) {
        addNotEquals(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetSelection totalTimeGt(int value) {
        addGreaterThan(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetSelection totalTimeGtEq(int value) {
        addGreaterThanOrEquals(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetSelection totalTimeLt(int value) {
        addLessThan(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetSelection totalTimeLtEq(int value) {
        addLessThanOrEquals(IntervalSetColumns.TOTAL_TIME, value);
        return this;
    }

    public IntervalSetSelection orderByTotalTime(boolean desc) {
        orderBy(IntervalSetColumns.TOTAL_TIME, desc);
        return this;
    }

    public IntervalSetSelection orderByTotalTime() {
        orderBy(IntervalSetColumns.TOTAL_TIME, false);
        return this;
    }

    public IntervalSetSelection times(String... value) {
        addEquals(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetSelection timesNot(String... value) {
        addNotEquals(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetSelection timesLike(String... value) {
        addLike(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetSelection timesContains(String... value) {
        addContains(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetSelection timesStartsWith(String... value) {
        addStartsWith(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetSelection timesEndsWith(String... value) {
        addEndsWith(IntervalSetColumns.TIMES, value);
        return this;
    }

    public IntervalSetSelection orderByTimes(boolean desc) {
        orderBy(IntervalSetColumns.TIMES, desc);
        return this;
    }

    public IntervalSetSelection orderByTimes() {
        orderBy(IntervalSetColumns.TIMES, false);
        return this;
    }
}
