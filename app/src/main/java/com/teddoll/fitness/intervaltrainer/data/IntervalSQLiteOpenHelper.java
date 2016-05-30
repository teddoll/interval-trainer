package com.teddoll.fitness.intervaltrainer.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.teddoll.fitness.intervaltrainer.BuildConfig;

public class IntervalSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = IntervalSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "intervals.db";
    private static final int DATABASE_VERSION = 1;
    private static IntervalSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final IntervalSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_INTERVAL_LOCATION = "CREATE TABLE IF NOT EXISTS "
            + IntervalContract.LocationEntry.TABLE_NAME + " ( "
            + IntervalContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IntervalContract.LocationEntry.INTERVAL_SESSION_ID + " INTEGER NOT NULL, "
            + IntervalContract.LocationEntry.LOCATION + " TEXT, "
            + IntervalContract.LocationEntry.AVERAGE_VELOCITY + " REAL "
            + ", CONSTRAINT fk_interval_session_id FOREIGN KEY (" + IntervalContract.LocationEntry.INTERVAL_SESSION_ID + ") REFERENCES interval_session (_id) ON DELETE CASCADE"
            + " );";

    public static final String SQL_CREATE_TABLE_INTERVAL_SESSION = "CREATE TABLE IF NOT EXISTS "
            + IntervalContract.SessionEntry.TABLE_NAME + " ( "
            + IntervalContract.SessionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IntervalContract.SessionEntry.START_TIME + " TEXT, "
            + IntervalContract.SessionEntry.START_LOCATION + " TEXT, "
            + IntervalContract.SessionEntry.POLY_LINE_DATA + " TEXT "
            + " );";

    public static final String SQL_CREATE_TABLE_INTERVAL_SET = "CREATE TABLE IF NOT EXISTS "
            + IntervalContract.SetEntry.TABLE_NAME + " ( "
            + IntervalContract.SetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + IntervalContract.SetEntry.LABEL + " TEXT, "
            + IntervalContract.SetEntry.TOTAL_TIME + " INTEGER, "
            + IntervalContract.SetEntry.TIMES + " TEXT "
            + " );";

    public static final String SQL_CREATE_INDEX_INTERVAL_SET_LABEL = "CREATE INDEX IDX_INTERVAL_SET_LABEL "
            + " ON " + IntervalContract.SetEntry.TABLE_NAME + " ( " + IntervalContract.SetEntry.LABEL + " );";

    // @formatter:on

    public static IntervalSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static IntervalSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static IntervalSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new IntervalSQLiteOpenHelper(context);
    }

    private IntervalSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new IntervalSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static IntervalSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new IntervalSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private IntervalSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new IntervalSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_INTERVAL_LOCATION);
        db.execSQL(SQL_CREATE_TABLE_INTERVAL_SESSION);
        db.execSQL(SQL_CREATE_TABLE_INTERVAL_SET);
        db.execSQL(SQL_CREATE_INDEX_INTERVAL_SET_LABEL);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
