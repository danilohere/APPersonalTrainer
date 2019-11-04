package appersonal.development.com.appersonaltrainer.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.model.Refeicoes;

public class BackgroundService extends Service {

    private static final String INTERVALO = "Intervalo";
    private static final String REFEICOES = "Refeicoes";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences intervaloAlarm = getSharedPreferences(INTERVALO, Context.MODE_PRIVATE);
        long intervalo = intervaloAlarm.getLong("Intervalo", 0);

        Calendar cmeia = Calendar.getInstance();
        cmeia.setTimeInMillis(System.currentTimeMillis());
        cmeia.set(Calendar.HOUR_OF_DAY, 0);
        cmeia.set(Calendar.MINUTE, 0);
        long meianoite = cmeia.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme = new Intent(BackgroundService.this, OnAlarmAguaReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(BackgroundService.this, 0, alarme, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, intervalo, alarmIntent);
        }


        AlarmManager alarmManager2 = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme2 = new Intent(BackgroundService.this, OnAlarmReceiver.class);
        PendingIntent alarmIntent2 = PendingIntent.getBroadcast(BackgroundService.this, 1, alarme2, 0);


        if (alarmManager2 != null) {
            alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, 60000, alarmIntent2);
        }


        SharedPreferences refeicoesSP = getSharedPreferences(REFEICOES, Context.MODE_PRIVATE);
        int sizeR = refeicoesSP.getInt("refeicoes_size", 0);
        if (sizeR > 0) {
            AlarmManager alarmManager3 = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            Intent alarme3 = new Intent(BackgroundService.this, OnAlarmRefeicaoReceiver.class);
            PendingIntent alarmIntent3 = PendingIntent.getBroadcast(BackgroundService.this, 2, alarme3, 0);

            if (alarmManager3 != null) {
                alarmManager3.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, 60000, alarmIntent3);
            }
        }
        super.onCreate();
    }
}
