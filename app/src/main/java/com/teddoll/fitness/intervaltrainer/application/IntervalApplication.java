package com.teddoll.fitness.intervaltrainer.application;

import android.app.Application;

import com.teddoll.fitness.intervaltrainer.BuildConfig;

import timber.log.Timber;

/**
 * Created by teddydoll on 5/13/16.
 */
public class IntervalApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
