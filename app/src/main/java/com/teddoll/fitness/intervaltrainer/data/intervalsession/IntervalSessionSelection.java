package com.teddoll.fitness.intervaltrainer.data.intervalsession;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractSelection;

/**
 * Selection for the {@code interval_session} table.
 */
public class IntervalSessionSelection extends AbstractSelection<IntervalSessionSelection> {
    @Override
    protected Uri baseUri() {
        return IntervalSessionColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code IntervalSessionCursor} object, which is positioned before the first entry, or null.
     */
    public IntervalSessionCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new IntervalSessionCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public IntervalSessionCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code IntervalSessionCursor} object, which is positioned before the first entry, or null.
     */
    public IntervalSessionCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new IntervalSessionCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public IntervalSessionCursor query(Context context) {
        return query(context, null);
    }


    public IntervalSessionSelection id(long... value) {
        addEquals("interval_session." + IntervalSessionColumns._ID, toObjectArray(value));
        return this;
    }

    public IntervalSessionSelection idNot(long... value) {
        addNotEquals("interval_session." + IntervalSessionColumns._ID, toObjectArray(value));
        return this;
    }

    public IntervalSessionSelection orderById(boolean desc) {
        orderBy("interval_session." + IntervalSessionColumns._ID, desc);
        return this;
    }

    public IntervalSessionSelection orderById() {
        return orderById(false);
    }

    public IntervalSessionSelection startTime(String... value) {
        addEquals(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionSelection startTimeNot(String... value) {
        addNotEquals(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionSelection startTimeLike(String... value) {
        addLike(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionSelection startTimeContains(String... value) {
        addContains(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionSelection startTimeStartsWith(String... value) {
        addStartsWith(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionSelection startTimeEndsWith(String... value) {
        addEndsWith(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalSessionSelection orderByStartTime(boolean desc) {
        orderBy(IntervalSessionColumns.START_TIME, desc);
        return this;
    }

    public IntervalSessionSelection orderByStartTime() {
        orderBy(IntervalSessionColumns.START_TIME, false);
        return this;
    }

    public IntervalSessionSelection polyLineData(String... value) {
        addEquals(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionSelection polyLineDataNot(String... value) {
        addNotEquals(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionSelection polyLineDataLike(String... value) {
        addLike(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionSelection polyLineDataContains(String... value) {
        addContains(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionSelection polyLineDataStartsWith(String... value) {
        addStartsWith(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionSelection polyLineDataEndsWith(String... value) {
        addEndsWith(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalSessionSelection orderByPolyLineData(boolean desc) {
        orderBy(IntervalSessionColumns.POLY_LINE_DATA, desc);
        return this;
    }

    public IntervalSessionSelection orderByPolyLineData() {
        orderBy(IntervalSessionColumns.POLY_LINE_DATA, false);
        return this;
    }
}
