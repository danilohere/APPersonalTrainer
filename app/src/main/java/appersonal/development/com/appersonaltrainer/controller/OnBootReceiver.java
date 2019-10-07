package appersonal.development.com.appersonaltrainer.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //noinspection ConstantConditions
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent pushIntent = new Intent(context, BackgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(pushIntent);
            } else {
                context.startService(pushIntent);
            }
        }
    }
}