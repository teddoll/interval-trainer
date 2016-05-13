package com.teddoll.fitness.intervaltrainer.data;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.teddoll.fitness.intervaltrainer.BuildConfig;
import com.teddoll.fitness.intervaltrainer.data.base.BaseContentProvider;
import com.teddoll.fitness.intervaltrainer.data.intervallocation.IntervalLocationColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalsession.IntervalSessionColumns;
import com.teddoll.fitness.intervaltrainer.data.intervalset.IntervalSetColumns;

public class IntervalProvider extends BaseContentProvider {
    private static final String TAG = IntervalProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "com.teddoll.fitness.intervaltrainer.data.IntervalProvider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_INTERVAL_LOCATION = 0;
    private static final int URI_TYPE_INTERVAL_LOCATION_ID = 1;

    private static final int URI_TYPE_INTERVAL_SESSION = 2;
    private static final int URI_TYPE_INTERVAL_SESSION_ID = 3;

    private static final int URI_TYPE_INTERVAL_SET = 4;
    private static final int URI_TYPE_INTERVAL_SET_ID = 5;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, IntervalLocationColumns.TABLE_NAME, URI_TYPE_INTERVAL_LOCATION);
        URI_MATCHER.addURI(AUTHORITY, IntervalLocationColumns.TABLE_NAME + "/#", URI_TYPE_INTERVAL_LOCATION_ID);
        URI_MATCHER.addURI(AUTHORITY, IntervalSessionColumns.TABLE_NAME, URI_TYPE_INTERVAL_SESSION);
        URI_MATCHER.addURI(AUTHORITY, IntervalSessionColumns.TABLE_NAME + "/#", URI_TYPE_INTERVAL_SESSION_ID);
        URI_MATCHER.addURI(AUTHORITY, IntervalSetColumns.TABLE_NAME, URI_TYPE_INTERVAL_SET);
        URI_MATCHER.addURI(AUTHORITY, IntervalSetColumns.TABLE_NAME + "/#", URI_TYPE_INTERVAL_SET_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return IntervalSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_INTERVAL_LOCATION:
                return TYPE_CURSOR_DIR + IntervalLocationColumns.TABLE_NAME;
            case URI_TYPE_INTERVAL_LOCATION_ID:
                return TYPE_CURSOR_ITEM + IntervalLocationColumns.TABLE_NAME;

            case URI_TYPE_INTERVAL_SESSION:
                return TYPE_CURSOR_DIR + IntervalSessionColumns.TABLE_NAME;
            case URI_TYPE_INTERVAL_SESSION_ID:
                return TYPE_CURSOR_ITEM + IntervalSessionColumns.TABLE_NAME;

            case URI_TYPE_INTERVAL_SET:
                return TYPE_CURSOR_DIR + IntervalSetColumns.TABLE_NAME;
            case URI_TYPE_INTERVAL_SET_ID:
                return TYPE_CURSOR_ITEM + IntervalSetColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_INTERVAL_LOCATION:
            case URI_TYPE_INTERVAL_LOCATION_ID:
                res.table = IntervalLocationColumns.TABLE_NAME;
                res.idColumn = IntervalLocationColumns._ID;
                res.tablesWithJoins = IntervalLocationColumns.TABLE_NAME;
                if (IntervalSessionColumns.hasColumns(projection)) {
                    res.tablesWithJoins += " LEFT OUTER JOIN " + IntervalSessionColumns.TABLE_NAME + " AS " + IntervalLocationColumns.PREFIX_INTERVAL_SESSION + " ON " + IntervalLocationColumns.TABLE_NAME + "." + IntervalLocationColumns.INTERVAL_SESSION_ID + "=" + IntervalLocationColumns.PREFIX_INTERVAL_SESSION + "." + IntervalSessionColumns._ID;
                }
                res.orderBy = IntervalLocationColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_INTERVAL_SESSION:
            case URI_TYPE_INTERVAL_SESSION_ID:
                res.table = IntervalSessionColumns.TABLE_NAME;
                res.idColumn = IntervalSessionColumns._ID;
                res.tablesWithJoins = IntervalSessionColumns.TABLE_NAME;
                res.orderBy = IntervalSessionColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_INTERVAL_SET:
            case URI_TYPE_INTERVAL_SET_ID:
                res.table = IntervalSetColumns.TABLE_NAME;
                res.idColumn = IntervalSetColumns._ID;
                res.tablesWithJoins = IntervalSetColumns.TABLE_NAME;
                res.orderBy = IntervalSetColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_INTERVAL_LOCATION_ID:
            case URI_TYPE_INTERVAL_SESSION_ID:
            case URI_TYPE_INTERVAL_SET_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
