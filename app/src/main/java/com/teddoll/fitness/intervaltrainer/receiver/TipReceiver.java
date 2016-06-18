package com.teddoll.fitness.intervaltrainer.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.teddoll.fitness.intervaltrainer.service.TipService;

public class TipReceiver extends BroadcastReceiver {
    public TipReceiver() {
    }

    public static void startTipTimer(@NonNull Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TipReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_DAY,
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        updateTip(context);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        updateTip(context);
    }

    private static void updateTip(@NonNull Context context) {
        context.startService(new Intent(context, TipService.class));
    }
}
