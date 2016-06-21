package com.teddoll.fitness.intervaltrainer.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.teddoll.fitness.intervaltrainer.R;
import com.teddoll.fitness.intervaltrainer.service.TipService;
import com.teddoll.fitness.intervaltrainer.session.LandingActivity;
import com.teddoll.fitness.intervaltrainer.util.DBStringParseUtil;
import com.teddoll.fitness.intervaltrainer.util.DateSuffixUtil;
import com.teddoll.fitness.intervaltrainer.util.UnitsUtil;

import java.util.Date;

public class TipReceiver extends AppWidgetProvider {
    public TipReceiver() {
    }


    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        Intent intent = new Intent(context, TipService.class);
        context.startService(intent);
    }

    public static void updateAppWidget(Context context,
                                       AppWidgetManager appWidgetManager,
                                       int[] appWidgetIds,
                                       String dateString, double distanceInMeters, String tip) {

        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            if (dateString != null) {

                Date date = DBStringParseUtil.deserializeDate(dateString);
                if (date != null) {

                    views.setTextViewText(R.id.date, DateSuffixUtil.getSuffixFormattedMonthDay(date));
                    views.setTextViewText(R.id.distance, context.getString(R.string.miles,
                            UnitsUtil.metersToMiles(distanceInMeters)));
                    views.setViewVisibility(R.id.distance, View.VISIBLE);
                } else {
                    views.setTextViewText(R.id.date, context.getString(R.string.no_records));
                    views.setViewVisibility(R.id.distance, View.GONE);
                }
            }
            views.setTextViewText(R.id.tip, tip);

            Intent intent = new Intent(context, LandingActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_container, pi);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }




}
