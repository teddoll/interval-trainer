package com.teddoll.fitness.intervaltrainer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.teddoll.fitness.intervaltrainer.BuildConfig;

import java.lang.reflect.Field;
import java.util.Arrays;

import timber.log.Timber;

public class IntervalProvider extends ContentProvider {
    private static final String TAG = IntervalProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final int URI_TYPE_INTERVAL_LOCATION = 0;
    private static final int URI_TYPE_INTERVAL_LOCATION_ID = 1;

    private static final int URI_TYPE_INTERVAL_SESSION = 2;
    private static final int URI_TYPE_INTERVAL_SESSION_ID = 3;

    private static final int URI_TYPE_INTERVAL_SET = 4;
    private static final int URI_TYPE_INTERVAL_SET_ID = 5;


    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(IntervalContract.CONTENT_AUTHORITY,
                IntervalContract.PATH_LOCATION, URI_TYPE_INTERVAL_LOCATION);
        URI_MATCHER.addURI(IntervalContract.CONTENT_AUTHORITY,
                IntervalContract.PATH_LOCATION + "/#", URI_TYPE_INTERVAL_LOCATION_ID);
        URI_MATCHER.addURI(IntervalContract.CONTENT_AUTHORITY,
                IntervalContract.PATH_SESSION, URI_TYPE_INTERVAL_SESSION);
        URI_MATCHER.addURI(IntervalContract.CONTENT_AUTHORITY,
                IntervalContract.PATH_SESSION + "/#", URI_TYPE_INTERVAL_SESSION_ID);
        URI_MATCHER.addURI(IntervalContract.CONTENT_AUTHORITY,
                IntervalContract.PATH_SET, URI_TYPE_INTERVAL_SET);
        URI_MATCHER.addURI(IntervalContract.CONTENT_AUTHORITY,
                IntervalContract.PATH_SET + "/#", URI_TYPE_INTERVAL_SET_ID);
    }

    protected SQLiteOpenHelper mSqLiteOpenHelper;

    @Override
    public boolean onCreate() {
        if (DEBUG) {
            // Enable logging of SQL statements as they are executed.
            try {
                Class<?> sqliteDebugClass = Class.forName("android.database.sqlite.SQLiteDebug");
                Field field = sqliteDebugClass.getDeclaredField("DEBUG_SQL_STATEMENTS");
                field.setAccessible(true);
                field.set(null, true);

                // Uncomment the following block if you also want logging of execution time (more verbose)
                // field = sqliteDebugClass.getDeclaredField("DEBUG_SQL_TIME");
                // field.setAccessible(true);
                // field.set(null, true);
            } catch (Throwable t) {
                Timber.w(TAG, "Could not enable SQLiteDebug logging", t);
            }
        }
        mSqLiteOpenHelper = IntervalSQLiteOpenHelper.getInstance(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_INTERVAL_LOCATION:
                return IntervalContract.LocationEntry.CONTENT_TYPE;
            case URI_TYPE_INTERVAL_LOCATION_ID:
                return IntervalContract.LocationEntry.CONTENT_ITEM_TYPE;
            case URI_TYPE_INTERVAL_SESSION:
                return IntervalContract.SessionEntry.CONTENT_TYPE;
            case URI_TYPE_INTERVAL_SESSION_ID:
                return IntervalContract.SessionEntry.CONTENT_ITEM_TYPE;
            case URI_TYPE_INTERVAL_SET:
                return IntervalContract.SetEntry.CONTENT_TYPE;
            case URI_TYPE_INTERVAL_SET_ID:
                return IntervalContract.SetEntry.CONTENT_ITEM_TYPE;

        }
        return null;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Timber.d(TAG, "insert uri=" + uri + " values=" + values);
        final SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case URI_TYPE_INTERVAL_LOCATION: {
                long _id = db.insert(IntervalContract.LocationEntry.TABLE_NAME, null, normalizeLocationValues(values));
                if (_id > 0)
                    returnUri = IntervalContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case URI_TYPE_INTERVAL_SESSION: {
                long _id = db.insert(IntervalContract.SessionEntry.TABLE_NAME, null, normalizeSessionValues(values));
                if (_id > 0)
                    returnUri = IntervalContract.SessionEntry.buildSessionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case URI_TYPE_INTERVAL_SET: {
                long _id = db.insert(IntervalContract.SetEntry.TABLE_NAME, null, normalizeSetValues(values));
                if (_id > 0)
                    returnUri = IntervalContract.SetEntry.buildSetUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        return returnUri;
    }

    /**
     * Functions not supported.
     *
     * @return 0 Not supported
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        Timber.d(TAG, "NOT SUPPORTED bulkInsert uri=" + uri + " values.length=" + values.length);
        return 0;
    }

    /**
     * Only allows update by ID.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Timber.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        final SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;

        String[] whereArgs = new String[1];
        if (selectionArgs.length > 0) {
            whereArgs[0] = selectionArgs[0];
        } else {
            throw new android.database.SQLException("update: Invalid selection args " + uri);
        }

        switch (match) {

            case URI_TYPE_INTERVAL_SESSION: {
                rowsUpdated = db.update(IntervalContract.SessionEntry.TABLE_NAME, normalizeSessionValues(values),
                        IntervalContract.SessionEntry._ID + "=?", whereArgs);
                break;
            }
            case URI_TYPE_INTERVAL_SET: {
                rowsUpdated = db.update(IntervalContract.SetEntry.TABLE_NAME, normalizeSetValues(values),
                        IntervalContract.SetEntry._ID + "=?", whereArgs);
                break;
            }
            case URI_TYPE_INTERVAL_LOCATION:
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            Context context = getContext();
            if (context != null) context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Only allows delete by ID.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Timber.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        final SQLiteDatabase db = mSqLiteOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;

        String[] whereArgs = new String[1];
        if (selectionArgs.length > 0) {
            whereArgs[0] = selectionArgs[0];
        } else {
            throw new android.database.SQLException("delete: Invalid selection args " + uri);
        }

        switch (match) {

            case URI_TYPE_INTERVAL_SESSION: {
                rowsUpdated = db.delete(IntervalContract.SessionEntry.TABLE_NAME,
                        IntervalContract.SessionEntry._ID + "=?", whereArgs);
                break;
            }
            case URI_TYPE_INTERVAL_SET: {
                rowsUpdated = db.delete(IntervalContract.SetEntry.TABLE_NAME,
                        IntervalContract.SetEntry._ID + "=?", whereArgs);
                break;
            }
            case URI_TYPE_INTERVAL_LOCATION:
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            Context context = getContext();
            if (context != null) context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Timber.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder);
        final SQLiteDatabase db = mSqLiteOpenHelper.getReadableDatabase();
        Cursor retCursor;
        switch (URI_MATCHER.match(uri)) {
            case URI_TYPE_INTERVAL_LOCATION: {
                retCursor = db.query(IntervalContract.LocationEntry.TABLE_NAME,
                        new String[]{IntervalContract.LocationEntry.LOCATION,
                                IntervalContract.LocationEntry.AVERAGE_VELOCITY},
                        null, null, null, null, null);
                break;
            }
            case URI_TYPE_INTERVAL_SESSION_ID: {
                retCursor = db.query(IntervalContract.SessionEntry.TABLE_NAME,
                        new String[]{IntervalContract.SessionEntry.START_TIME,
                                IntervalContract.SessionEntry.END_TIME,
                                IntervalContract.SessionEntry.DISTANCE_TRAVELED,
                                IntervalContract.SessionEntry.POLY_LINE_DATA},
                        IntervalContract.SessionEntry._ID + "=?", new String[]{uri.getPathSegments().get(1)},
                        null, null, null);
                break;
            }
            case URI_TYPE_INTERVAL_SESSION: {
                retCursor = db.query(IntervalContract.SessionEntry.TABLE_NAME,
                        new String[]{IntervalContract.SessionEntry._ID,
                                IntervalContract.SessionEntry.START_TIME,
                                IntervalContract.SessionEntry.END_TIME,
                                IntervalContract.SessionEntry.DISTANCE_TRAVELED},
                        null, null, null, null, null);
                break;
            }
            case URI_TYPE_INTERVAL_SET_ID: {
                retCursor = db.query(IntervalContract.SetEntry.TABLE_NAME,
                        new String[]{IntervalContract.SetEntry.LABEL,
                                IntervalContract.SetEntry.TIMES,
                                IntervalContract.SetEntry.TOTAL_TIME},
                        IntervalContract.SessionEntry._ID + "=?", new String[]{uri.getPathSegments().get(1)},
                        null, null, null);
                break;
            }
            case URI_TYPE_INTERVAL_SET: {
                retCursor = db.query(IntervalContract.SetEntry.TABLE_NAME,
                        new String[]{IntervalContract.SetEntry.LABEL,
                                IntervalContract.SetEntry.TIMES,
                                IntervalContract.SetEntry.TOTAL_TIME},
                        null, null, null, null, null);
                break;
            }
            case URI_TYPE_INTERVAL_LOCATION_ID:
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retCursor;

    }


    /*
     * Normalize Functions to prevent SQL injection.
     */
    private ContentValues normalizeLocationValues(ContentValues provided) {
        ContentValues values = new ContentValues();
        if (provided.containsKey(IntervalContract.LocationEntry.INTERVAL_SESSION_ID)) {
            values.put(IntervalContract.LocationEntry.INTERVAL_SESSION_ID,
                    provided.getAsInteger(IntervalContract.LocationEntry.INTERVAL_SESSION_ID));
        }
        if (provided.containsKey(IntervalContract.LocationEntry.LOCATION)) {
            values.put(IntervalContract.LocationEntry.LOCATION,
                    provided.getAsString(IntervalContract.LocationEntry.LOCATION));
        }
        if (provided.containsKey(IntervalContract.LocationEntry.AVERAGE_VELOCITY)) {
            values.put(IntervalContract.LocationEntry.AVERAGE_VELOCITY,
                    provided.getAsFloat(IntervalContract.LocationEntry.AVERAGE_VELOCITY));
        }
        if (provided.containsKey(IntervalContract.LocationEntry.DISTANCE)) {
            values.put(IntervalContract.LocationEntry.DISTANCE,
                    provided.getAsFloat(IntervalContract.LocationEntry.DISTANCE));
        }
        Timber.d(TAG, "normalizeLocationValues= " + values);
        return values;
    }

    private ContentValues normalizeSessionValues(ContentValues provided) {
        ContentValues values = new ContentValues();
        if (provided.containsKey(IntervalContract.SessionEntry.START_TIME)) {
            values.put(IntervalContract.SessionEntry.START_TIME,
                    provided.getAsString(IntervalContract.SessionEntry.START_TIME));
        }
        if (provided.containsKey(IntervalContract.SessionEntry.END_TIME)) {
            values.put(IntervalContract.SessionEntry.END_TIME,
                    provided.getAsString(IntervalContract.SessionEntry.END_TIME));
        }
        if (provided.containsKey(IntervalContract.SessionEntry.POLY_LINE_DATA)) {
            values.put(IntervalContract.SessionEntry.POLY_LINE_DATA,
                    provided.getAsString(IntervalContract.SessionEntry.POLY_LINE_DATA));
        }
        if(provided.containsKey(IntervalContract.SessionEntry.DISTANCE_TRAVELED)) {
            values.put(IntervalContract.SessionEntry.DISTANCE_TRAVELED,
                    provided.getAsFloat(IntervalContract.SessionEntry.DISTANCE_TRAVELED));
        }
        Timber.d(TAG, "normalizeSessionValues= " + values);
        return values;
    }

    private ContentValues normalizeSetValues(ContentValues provided) {
        ContentValues values = new ContentValues();
        if (provided.containsKey(IntervalContract.SetEntry.LABEL)) {
            values.put(IntervalContract.SetEntry.LABEL,
                    provided.getAsString(IntervalContract.SetEntry.LABEL));
        }
        if (provided.containsKey(IntervalContract.SetEntry.TOTAL_TIME)) {
            values.put(IntervalContract.SetEntry.TOTAL_TIME,
                    provided.getAsString(IntervalContract.SetEntry.TOTAL_TIME));
        }
        if (provided.containsKey(IntervalContract.SetEntry.TIMES)) {
            values.put(IntervalContract.SetEntry.TIMES,
                    provided.getAsString(IntervalContract.SetEntry.TIMES));
        }
        Timber.d(TAG, "normalizeSetValues= " + values);
        return values;
    }
}
