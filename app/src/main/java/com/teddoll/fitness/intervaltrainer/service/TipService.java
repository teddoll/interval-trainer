package com.teddoll.fitness.intervaltrainer.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.Tips;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.teddoll.fitness.intervaltrainer.backend.tipApi.TipApi;
import com.teddoll.fitness.intervaltrainer.data.IntervalContract;
import com.teddoll.fitness.intervaltrainer.prefs.AppPrefs;
import com.teddoll.fitness.intervaltrainer.receiver.TipReceiver;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by teddydoll on 6/16/16.
 */
public class TipService extends IntentService {

    private static TipApi myApiService = null;

    public TipService() {
        super(TipService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (myApiService == null) {  // Only do this once
            TipApi.Builder builder = new TipApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        // Empty String breaks API
        String date = "unknown";
        double distance = 0;
        Cursor cursor = getContentResolver().query(IntervalContract.SessionEntry.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToLast()) {
                date = cursor.getString(cursor.getColumnIndex(IntervalContract.SessionEntry.END_TIME));
                distance = cursor.getDouble(cursor.getColumnIndex(IntervalContract.SessionEntry.DISTANCE_TRAVELED));
            }

            cursor.close();
        }
        String tip = Tips.UNKNOWN_WORK;
        try {
            tip = myApiService.getTip(date).execute().getTip();
            Timber.d(tip);
            AppPrefs.getInstance(this).saveTip(tip);
        } catch (IOException e) {
            Timber.e(e, "failed to retrieve tip.");
        }

        Context context = getApplicationContext();
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        int[] ids = awm.getAppWidgetIds(new ComponentName(context, TipReceiver.class));
        TipReceiver.updateAppWidget(context, awm, ids, date, distance, tip);

    }
}
