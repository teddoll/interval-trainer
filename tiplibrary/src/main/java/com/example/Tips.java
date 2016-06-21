package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Tips {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String GOOD_WORK = "Keep up the good work!";
    private static final String OK_WORK = "It's beed a few days, get back to it!";
    private static final String DISMAL_WORK = "We miss you! Get back to work!";
    public static final String UNKNOWN_WORK = "Lets get training!";

        public static String getTip(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            Date date = format.parse(dateString);
            Date now  = new Date();
            long delta = now.getTime() - date.getTime();
            if(delta <= 1000 * 60 * 60 * 24 * 3) {
                return GOOD_WORK;
            } else if(delta <= 1000 * 60 * 60 * 24 * 5) {
                return OK_WORK;
            } else {
                return DISMAL_WORK;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return UNKNOWN_WORK;

    }

}
