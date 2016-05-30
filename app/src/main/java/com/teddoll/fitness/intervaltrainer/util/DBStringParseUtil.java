package com.teddoll.fitness.intervaltrainer.util;

import android.location.Location;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by teddydoll on 5/13/16.
 */
public final class DBStringParseUtil {

    private DBStringParseUtil(){};

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String serializeLocations(@NonNull List<Location> locations) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            sb.append(serializeLocation(location));
            sb.append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String serializeLocation(@NonNull Location location) {
        return String.format(Locale.US, "%.8f,%.8f", location.getLatitude(), location.getLongitude());
    }

    public static Location deserializeLocation(@NonNull String rawLocation) {
        String[] split = rawLocation.split(",");
        Location location = new Location("DBStringParseUtil");
        location.setLatitude(Double.parseDouble(split[0]));
        location.setLongitude(Double.parseDouble(split[1]));
        return location;

    }

    public static List<Location> deserializeLocations(@NonNull String rawLocations) {
        String[] split = rawLocations.split("|");
        List<Location> locations = new ArrayList<>(split.length);
        for(int i = 0; i < split.length; i++) {
            locations.add(deserializeLocation(split[i]));
        }
        return locations;
    }

    public static Date deserializeDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return format.parse(date);
    }

    public static String serializeDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return format.format(date);
    }

    public static List<Long> deserializeSetTimes(String setTimes) {
        String[] split = setTimes.split(",");
        List<Long> times = new ArrayList<>(split.length);
        for(int i = 0; i < split.length; i++) {
            times.add(Long.parseLong(split[i]));
        }
        return times;
    }

    public static String serializeSetTimes(List<Long> setTimes) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < setTimes.size(); i++) {
            sb.append(setTimes.get(i));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
