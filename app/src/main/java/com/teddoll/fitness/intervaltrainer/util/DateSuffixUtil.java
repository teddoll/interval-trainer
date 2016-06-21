package com.teddoll.fitness.intervaltrainer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Date format util
 * Created by teddydoll on 6/20/16.
 */
public final class DateSuffixUtil {

    private DateSuffixUtil() {
        throw new RuntimeException(
                DateSuffixUtil.class.getSimpleName()
                        + ": Don't call private static class contstructor");
    }
    public static String getSuffixFormattedMonthDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MMM d'" + getDaySuffix(date) + "'", Locale.US);
        return format.format(date);
    }

    private static String getDaySuffix(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }

    }
}
