package appersonal.development.com.appersonaltrainer.Controller;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import appersonal.development.com.appersonaltrainer.Activities.MainActivity;


/**
 * Created by Danilo on 11/05/2017.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent pushIntent = new Intent(context, BackgroundService.class);
            context.startService(pushIntent);
        }
    }
}
