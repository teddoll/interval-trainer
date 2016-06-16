package com.teddoll.fitness.intervaltrainer.session;

import android.location.Location;

/**
 * Created by teddydoll on 6/10/16.
 */
public class SessionInterval {

    public final Location location;
    public final double distance;
    public final double velocity;

    public SessionInterval(Location location, double distance, double velocity) {
        this.location = location;
        this.distance = distance;
        this.velocity = velocity;
    }
}
