package com.teddoll.fitness.intervaltrainer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by teddydoll on 5/19/16.
 */
public final class IntervalContract {

    private IntervalContract(){}


    public static final String CONTENT_AUTHORITY = "com.teddoll.fitness.intervaltrainer.data.IntervalProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SESSION = "session";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_SET = "set";


    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Table name
        public static final String TABLE_NAME = "interval_location";

        /**
         * Primary key.
         */
        public static final String _ID = "_id";

        public static final String INTERVAL_SESSION_ID = "interval_session_id";

        public static final String LOCATION = "location";
        public static final String DISTANCE = "distance";
        public static final String AVERAGE_VELOCITY = "average_velocity";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SessionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SESSION;

        // Table name
        public static final String TABLE_NAME = "interval_session";

        /**
         * Primary key.
         */
        public static final String _ID = "_id";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";
        public static final String DISTANCE_TRAVELED = "total_distance";
        public static final String POLY_LINE_DATA = "poly_line_data";


        public static Uri buildSessionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SetEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SET).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SET;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SET;

        // Table name
        public static final String TABLE_NAME = "interval_set";

        /**
         * Primary key.
         */
        public static final String _ID = BaseColumns._ID;

        public static final String LABEL = "label";

        public static final String TOTAL_TIME = "total_time";

        public static final String TIMES = "times";


        public static Uri buildSetUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
