package appersonal.development.com.appersonaltrainer.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //noinspection ConstantConditions
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent pushIntent = new Intent(context, BackgroundService.class);
            context.startService(pushIntent);
        }
    }
}