package appersonal.development.com.appersonaltrainer.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Calendar;

public class BackgroundService extends Service {

    private static final String HORAINICIO = "HoraInicio";
    private static final String MINUTOINICIO = "MinutoInicio";
    private static final String INTERVALO = "Intervalo";
    private static final String HORAFIM = "HoraFim";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences horainicioAlarm = getSharedPreferences(HORAINICIO, Context.MODE_PRIVATE);
        int hi = (horainicioAlarm.getInt("HoraInicio", 0));

        SharedPreferences minutoinicioAlarm = getSharedPreferences(MINUTOINICIO, Context.MODE_PRIVATE);
        int mi = (minutoinicioAlarm.getInt("MinutoInicio", 0));

        SharedPreferences horafimAlarm = getSharedPreferences(HORAFIM, Context.MODE_PRIVATE);
        int hf = horafimAlarm.getInt("HoraFim", 0);

        SharedPreferences intervaloAlarm = getSharedPreferences(INTERVALO, Context.MODE_PRIVATE);
        long intervalo = intervaloAlarm.getLong("Intervalo", 0);

        Calendar ci = Calendar.getInstance();
        ci.setTimeInMillis(System.currentTimeMillis());
        ci.set(Calendar.HOUR_OF_DAY, hi);
        ci.set(Calendar.MINUTE, mi);
        long inicio = ci.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme = new Intent(BackgroundService.this, OnAlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(BackgroundService.this, 0, alarme, 0);

        if (hi > hf) {
            Calendar cmeia = Calendar.getInstance();
            cmeia.setTimeInMillis(System.currentTimeMillis());
            cmeia.set(Calendar.HOUR_OF_DAY, 0);
            cmeia.set(Calendar.MINUTE, 0);
            long meianoite = cmeia.getTimeInMillis();
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, intervalo, alarmIntent);
            }
        } else {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, inicio, intervalo, alarmIntent);
            }
        }


        super.onCreate();
    }

    //    public BackgroundService(){
//
//    }


}
