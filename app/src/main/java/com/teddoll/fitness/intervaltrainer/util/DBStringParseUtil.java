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

import timber.log.Timber;

/**
 * Created by teddydoll on 5/13/16.
 */
public final class DBStringParseUtil {

    private DBStringParseUtil() {
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    public static String serializeLocations(@NonNull List<Location> locations) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i);
            sb.append(serializeLocation(location));
            sb.append("|");
        }
        if (sb.length() > 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static String serializeLocation(Location location) {
        if (location != null)
            return String.format(Locale.US, "%.8f,%.8f", location.getLatitude(), location.getLongitude());
        return "";
    }

    public static Location deserializeLocation(@NonNull String rawLocation) {
        if (rawLocation.isEmpty()) return null;
        String[] split = rawLocation.split(",");
        Location location = new Location("DBStringParseUtil");
        location.setLatitude(Double.parseDouble(split[0]));
        location.setLongitude(Double.parseDouble(split[1]));
        return location;

    }

    public static List<Location> deserializeLocations(@NonNull String rawLocations) {
        String[] split = rawLocations.split("\\|");
        List<Location> locations = new ArrayList<>(split.length);
        for (int i = 0; i < split.length; i++) {
            locations.add(deserializeLocation(split[i]));
        }
        return locations;
    }

    public static Date deserializeDate(String date) {
        if (date == null) return null;
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Zulu"));
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            Timber.e("failed to deserialize Date");
        }
        return null;
    }

    public static String serializeDate(Date date) {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Zulu"));
        return DATE_FORMAT.format(date);
    }

    public static List<Integer> deserializeSetTimes(String setTimes) {
        String[] split = setTimes.split(",");
        List<Integer> times = new ArrayList<>(split.length);
        for (int i = 0; i < split.length; i++) {
            times.add(Integer.parseInt(split[i]));
        }
        return times;
    }

    public static String serializeSetTimes(List<Integer> setTimes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < setTimes.size(); i++) {
            sb.append(setTimes.get(i));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
