package com.teddoll.fitness.intervaltrainer.data.intervallocation;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.teddoll.fitness.intervaltrainer.data.base.AbstractSelection;
import com.teddoll.fitness.intervaltrainer.data.intervalsession.*;

/**
 * Selection for the {@code interval_location} table.
 */
public class IntervalLocationSelection extends AbstractSelection<IntervalLocationSelection> {
    @Override
    protected Uri baseUri() {
        return IntervalLocationColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code IntervalLocationCursor} object, which is positioned before the first entry, or null.
     */
    public IntervalLocationCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new IntervalLocationCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public IntervalLocationCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code IntervalLocationCursor} object, which is positioned before the first entry, or null.
     */
    public IntervalLocationCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new IntervalLocationCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public IntervalLocationCursor query(Context context) {
        return query(context, null);
    }


    public IntervalLocationSelection id(long... value) {
        addEquals("interval_location." + IntervalLocationColumns._ID, toObjectArray(value));
        return this;
    }

    public IntervalLocationSelection idNot(long... value) {
        addNotEquals("interval_location." + IntervalLocationColumns._ID, toObjectArray(value));
        return this;
    }

    public IntervalLocationSelection orderById(boolean desc) {
        orderBy("interval_location." + IntervalLocationColumns._ID, desc);
        return this;
    }

    public IntervalLocationSelection orderById() {
        return orderById(false);
    }

    public IntervalLocationSelection intervalSessionId(long... value) {
        addEquals(IntervalLocationColumns.INTERVAL_SESSION_ID, toObjectArray(value));
        return this;
    }

    public IntervalLocationSelection intervalSessionIdNot(long... value) {
        addNotEquals(IntervalLocationColumns.INTERVAL_SESSION_ID, toObjectArray(value));
        return this;
    }

    public IntervalLocationSelection intervalSessionIdGt(long value) {
        addGreaterThan(IntervalLocationColumns.INTERVAL_SESSION_ID, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionIdGtEq(long value) {
        addGreaterThanOrEquals(IntervalLocationColumns.INTERVAL_SESSION_ID, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionIdLt(long value) {
        addLessThan(IntervalLocationColumns.INTERVAL_SESSION_ID, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionIdLtEq(long value) {
        addLessThanOrEquals(IntervalLocationColumns.INTERVAL_SESSION_ID, value);
        return this;
    }

    public IntervalLocationSelection orderByIntervalSessionId(boolean desc) {
        orderBy(IntervalLocationColumns.INTERVAL_SESSION_ID, desc);
        return this;
    }

    public IntervalLocationSelection orderByIntervalSessionId() {
        orderBy(IntervalLocationColumns.INTERVAL_SESSION_ID, false);
        return this;
    }

    public IntervalLocationSelection intervalSessionStartTime(String... value) {
        addEquals(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionStartTimeNot(String... value) {
        addNotEquals(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionStartTimeLike(String... value) {
        addLike(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionStartTimeContains(String... value) {
        addContains(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionStartTimeStartsWith(String... value) {
        addStartsWith(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionStartTimeEndsWith(String... value) {
        addEndsWith(IntervalSessionColumns.START_TIME, value);
        return this;
    }

    public IntervalLocationSelection orderByIntervalSessionStartTime(boolean desc) {
        orderBy(IntervalSessionColumns.START_TIME, desc);
        return this;
    }

    public IntervalLocationSelection orderByIntervalSessionStartTime() {
        orderBy(IntervalSessionColumns.START_TIME, false);
        return this;
    }

    public IntervalLocationSelection intervalSessionPolyLineData(String... value) {
        addEquals(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionPolyLineDataNot(String... value) {
        addNotEquals(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionPolyLineDataLike(String... value) {
        addLike(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionPolyLineDataContains(String... value) {
        addContains(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionPolyLineDataStartsWith(String... value) {
        addStartsWith(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalLocationSelection intervalSessionPolyLineDataEndsWith(String... value) {
        addEndsWith(IntervalSessionColumns.POLY_LINE_DATA, value);
        return this;
    }

    public IntervalLocationSelection orderByIntervalSessionPolyLineData(boolean desc) {
        orderBy(IntervalSessionColumns.POLY_LINE_DATA, desc);
        return this;
    }

    public IntervalLocationSelection orderByIntervalSessionPolyLineData() {
        orderBy(IntervalSessionColumns.POLY_LINE_DATA, false);
        return this;
    }

    public IntervalLocationSelection location(String... value) {
        addEquals(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationSelection locationNot(String... value) {
        addNotEquals(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationSelection locationLike(String... value) {
        addLike(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationSelection locationContains(String... value) {
        addContains(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationSelection locationStartsWith(String... value) {
        addStartsWith(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationSelection locationEndsWith(String... value) {
        addEndsWith(IntervalLocationColumns.LOCATION, value);
        return this;
    }

    public IntervalLocationSelection orderByLocation(boolean desc) {
        orderBy(IntervalLocationColumns.LOCATION, desc);
        return this;
    }

    public IntervalLocationSelection orderByLocation() {
        orderBy(IntervalLocationColumns.LOCATION, false);
        return this;
    }

    public IntervalLocationSelection averageVelocity(Float... value) {
        addEquals(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationSelection averageVelocityNot(Float... value) {
        addNotEquals(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationSelection averageVelocityGt(float value) {
        addGreaterThan(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationSelection averageVelocityGtEq(float value) {
        addGreaterThanOrEquals(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationSelection averageVelocityLt(float value) {
        addLessThan(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationSelection averageVelocityLtEq(float value) {
        addLessThanOrEquals(IntervalLocationColumns.AVERAGE_VELOCITY, value);
        return this;
    }

    public IntervalLocationSelection orderByAverageVelocity(boolean desc) {
        orderBy(IntervalLocationColumns.AVERAGE_VELOCITY, desc);
        return this;
    }

    public IntervalLocationSelection orderByAverageVelocity() {
        orderBy(IntervalLocationColumns.AVERAGE_VELOCITY, false);
        return this;
    }
}
