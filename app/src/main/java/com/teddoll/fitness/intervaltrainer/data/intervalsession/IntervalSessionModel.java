package com.teddoll.fitness.intervaltrainer.data.intervalsession;

import com.teddoll.fitness.intervaltrainer.data.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interval Set.
 */
public interface IntervalSessionModel extends BaseModel {

    /**
     * Get the {@code start_time} value.
     * Can be {@code null}.
     */
    @Nullable
    String getStartTime();

    /**
     * Get the {@code poly_line_data} value.
     * Can be {@code null}.
     */
    @Nullable
    String getPolyLineData();
}
