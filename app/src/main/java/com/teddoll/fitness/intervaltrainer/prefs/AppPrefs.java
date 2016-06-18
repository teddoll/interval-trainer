package com.teddoll.fitness.intervaltrainer.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.example.Tips;
import com.teddoll.fitness.intervaltrainer.BuildConfig;

/**
 * Created by teddydoll on 6/16/16.
 */
public final class AppPrefs {

    public static final long TIP_DELTA_DISPLAY_TIME = 1000 * 60 * 60 * 24;

    private static final String PREFS = BuildConfig.APPLICATION_ID;
    private static final String TIP = "_tip";
    private static final String LAST_TIP = "_last_tip";
    private SharedPreferences mPrefs;
    private static AppPrefs sAppPrefs;

    public static AppPrefs getInstance(@NonNull Context context) {
        if(sAppPrefs == null) sAppPrefs = new AppPrefs(context);
        return sAppPrefs;
    }

    private AppPrefs(@NonNull Context context) {
        mPrefs = context.getSharedPreferences(PREFS, 0);
    }

    public void saveTip(String tip) {
        mPrefs.edit().putString(TIP, tip).apply();
    }

    public String getTip() {
        return mPrefs.getString(TIP, Tips.UNKNOWN_WORK);
    }

    public void setLastTipTime(long time) {
        mPrefs.edit().putLong(LAST_TIP, time).apply();
    }

    public long getLastTipTime() {
        return mPrefs.getLong(LAST_TIP, 0);
    }




}
