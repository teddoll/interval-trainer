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
import com.teddoll.fitness.intervaltrainer.util.UnitsUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                    SimpleDateFormat format = new SimpleDateFormat("MMM d'" + getDaySuffix(date) + "'", Locale.US);
                    views.setTextViewText(R.id.date, format.format(date));
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

    public static String getDaySuffix(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }

    }


}
