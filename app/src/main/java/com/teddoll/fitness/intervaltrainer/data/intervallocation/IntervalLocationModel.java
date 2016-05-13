package com.teddoll.fitness.intervaltrainer.data.intervallocation;

import com.teddoll.fitness.intervaltrainer.data.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interval Location
 */
public interface IntervalLocationModel extends BaseModel {

    /**
     * Get the {@code interval_session_id} value.
     */
    long getIntervalSessionId();

    /**
     * Get the {@code location} value.
     * Can be {@code null}.
     */
    @Nullable
    String getLocation();

    /**
     * Get the {@code average_velocity} value.
     * Can be {@code null}.
     */
    @Nullable
    Float getAverageVelocity();
}
