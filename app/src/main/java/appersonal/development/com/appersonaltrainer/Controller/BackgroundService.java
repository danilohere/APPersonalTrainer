package appersonal.development.com.appersonaltrainer.Controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.Model.Refeicoes;

public class BackgroundService extends Service {

    private static final String HORAINICIO = "HoraInicio";
    private static final String MINUTOINICIO = "MinutoInicio";
    private static final String INTERVALO = "Intervalo";
    private static final String HORAFIM = "HoraFim";
    private static final String HORATREINO = "HoraTreino";
    private static final String MINUTOTREINO = "MinutoTreino";
    private static final String REFEICOES = "Refeicoes";

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

        Calendar cmeia = Calendar.getInstance();
        cmeia.setTimeInMillis(System.currentTimeMillis());
        cmeia.set(Calendar.HOUR_OF_DAY, 0);
        cmeia.set(Calendar.MINUTE, 0);
        long meianoite = cmeia.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme = new Intent(BackgroundService.this, OnAlarmAguaReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(BackgroundService.this, 0, alarme, 0);

//        if (hi > hf) {
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, intervalo, alarmIntent);
            }
//        } else {
//            if (alarmManager != null) {
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, inicio, intervalo, alarmIntent);
//            }
//        }

        SharedPreferences horaTreinoAlarm = getSharedPreferences(HORATREINO, Context.MODE_PRIVATE);
        int ht = horaTreinoAlarm.getInt("HoraTreino", 0);

        SharedPreferences minutoTreinoAlarm = getSharedPreferences(MINUTOTREINO, Context.MODE_PRIVATE);
        int mt = minutoTreinoAlarm.getInt("MinutoTreino", 0);

        AlarmManager alarmManager2 = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme2 = new Intent(BackgroundService.this, OnAlarmReceiver.class);
        PendingIntent alarmIntent2 = PendingIntent.getBroadcast(BackgroundService.this, 1, alarme2, 0);

        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(System.currentTimeMillis());
        ct.set(Calendar.HOUR_OF_DAY, ht);
        ct.set(Calendar.MINUTE, mt);
        long alarmeTreino = ct.getTimeInMillis();

        if (alarmManager2 != null) {
            alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, 60 * 1000, alarmIntent2);
        }


        SharedPreferences refeicoesSP = getSharedPreferences(REFEICOES, Context.MODE_PRIVATE);
        int sizeR = refeicoesSP.getInt("refeicoes_size", 0);
        for (int i = 0; i < sizeR; i++) {

            AlarmManager alarmManager3 = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            Intent alarme3 = new Intent(BackgroundService.this, OnAlarmReceiver.class);
            PendingIntent alarmIntent3 = PendingIntent.getBroadcast(BackgroundService.this, i + 2, alarme3, 0);

            Refeicoes refeicao = new Refeicoes();
            refeicao.setRefeicao(refeicoesSP.getString("refeicao_" + i, ""));
            refeicao.setHora(refeicoesSP.getString("hora_" + i, ""));
            refeicao.setMinuto(refeicoesSP.getString("minuto_" + i, ""));
            int h = Integer.parseInt(refeicoesSP.getString("hora_" + i, ""));
            int m = Integer.parseInt(refeicoesSP.getString("minuto_" + i, ""));
            Calendar cr = Calendar.getInstance();
            cr.setTimeInMillis(System.currentTimeMillis());
            cr.set(Calendar.HOUR_OF_DAY, h);
            cr.set(Calendar.MINUTE, m);
            long time = cr.getTimeInMillis();

            if (alarmManager3 != null) {
                alarmManager3.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, 60 * 1000, alarmIntent3);
            }
        }

        super.onCreate();
    }
}
