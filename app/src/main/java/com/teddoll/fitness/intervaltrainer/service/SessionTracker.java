package com.teddoll.fitness.intervaltrainer.service;

import android.location.Location;
import android.os.SystemClock;

/**
 * Created by teddydoll on 5/20/16.
 */
public final class SessionTracker {

    private long mTime;
    private Location mLocation;
    private double mVelocity;
    private double mDistance;
    private double mLastTrackedDistance;

    public void start(Location location) {
        mTime = SystemClock.uptimeMillis();
        mLocation = location;
        mVelocity = 0d;
        mDistance = 0d;
        mLastTrackedDistance = 0d;
    }

    public synchronized void update(Location location) {
        long current = SystemClock.uptimeMillis();
        long deltaInSeconds = (current - mTime) / 1000;
        double distance = distance(mLocation.getLatitude(),
                mLocation.getLongitude(), mLocation.getAltitude(),
                location.getLatitude(), location.getLongitude(), location.getAltitude());

        double velocity = distance / deltaInSeconds;
        if(mVelocity == 0) {
            mVelocity = velocity;
            mDistance = distance;
        } else {
            mVelocity = (mVelocity + velocity) / 2;
            mDistance += distance;
        }
        mLocation = location;
        mTime = current;


    }

    public Double getCurrentAverageVelocity() {
        return mVelocity;
    }

    public Location getLastLocation() {
        return mLocation;
    }

    public double getTotalDistance() {
        return mDistance;
    }

    public double getTrackedDistanceAndUpdate() {
        double ret = mDistance - mLastTrackedDistance;
        mLastTrackedDistance = mDistance;
        return ret;
    }

    private static double distance(double lat1, double lon1, double el1, double lat2,
                                   double lon2, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}
