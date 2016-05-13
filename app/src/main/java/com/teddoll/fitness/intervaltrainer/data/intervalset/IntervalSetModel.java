package com.teddoll.fitness.intervaltrainer.data.intervalset;

import com.teddoll.fitness.intervaltrainer.data.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Interval Set.
 */
public interface IntervalSetModel extends BaseModel {

    /**
     * Get the {@code label} value.
     * Can be {@code null}.
     */
    @Nullable
    String getLabel();

    /**
     * Get the {@code total_time} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getTotalTime();

    /**
     * Get the {@code times} value.
     * Can be {@code null}.
     */
    @Nullable
    String getTimes();
}
