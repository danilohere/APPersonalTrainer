package appersonal.development.com.appersonaltrainer.Controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.Activities.AguaActivity;
import appersonal.development.com.appersonaltrainer.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class OnAlarmReceiver extends BroadcastReceiver {

    private static final String HORATREINO = "HoraTreino";
    private static final String MINUTOTREINO = "MinutoTreino";
    private static final String ALARMETREINO = "AlarmeTreino";
    private static final String ALARMEREFEICOES = "AlarmeRefeicoes";
    private static final String DIASSEMANA = "DiasSemana";
    private static final String REFEICOES = "Refeicoes";
    private int alarmeTreino;
    private int alarmeRefeicoes;
    private boolean[] diasSemana = new boolean[7];

    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        int h = ca.get(Calendar.HOUR_OF_DAY);
        int m = ca.get(Calendar.MINUTE);
        int ds = ca.get(Calendar.DAY_OF_WEEK);
        ds = ds - 2;
        if (ds == -1)
            ds = 6;

        SharedPreferences horaTreinoAlarm = context.getSharedPreferences(HORATREINO, Context.MODE_PRIVATE);
        int ht = horaTreinoAlarm.getInt("HoraTreino", 0);
        SharedPreferences minutoTreinoAlarm = context.getSharedPreferences(MINUTOTREINO, Context.MODE_PRIVATE);
        int mt = minutoTreinoAlarm.getInt("MinutoTreino", 0);
        SharedPreferences alarmTreino = context.getSharedPreferences(ALARMETREINO, Context.MODE_PRIVATE);
        SharedPreferences diasSemanaSP = context.getSharedPreferences(DIASSEMANA, Context.MODE_PRIVATE);
        int size = diasSemanaSP.getInt("diasSemana_size", 0);
        for (int i = 0; i < size; i++) {
            diasSemana[i] = diasSemanaSP.getBoolean("diasSemana_" + i, false);
        }
        alarmeTreino = alarmTreino.getInt("AlarmeTreino", 0);
        if (h == ht && m == mt) {
            if (diasSemana[ds])
                notificacaoTreino(context);
        }
        SharedPreferences alarmRefeicoes = context.getSharedPreferences(ALARMEREFEICOES, Context.MODE_PRIVATE);
        alarmeRefeicoes = alarmRefeicoes.getInt("AlarmeRefeicoes", 0);
        SharedPreferences refeicoesSP = context.getSharedPreferences(REFEICOES, Context.MODE_PRIVATE);
        int sizeR = refeicoesSP.getInt("refeicoes_size", 0);
        for (int i = 0; i < sizeR; i++) {
            int hr = Integer.parseInt(refeicoesSP.getString("hora_" + i, ""));
            int mr = Integer.parseInt(refeicoesSP.getString("minuto_" + i, ""));
            if (h == hr && m == mr) {
                notificacaoRefeicao(context, refeicoesSP.getString("refeicao_" + i, ""));
            }
        }
    }

    void notificacaoTreino(Context context) {
        int mNotificationId = 2;
        if (alarmeTreino == 1) {
            //noinspection deprecation
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.applogopush)
                            .setContentTitle("HORA DO TREINO!")
                            .setContentText("Bora treinar?")
                            .setLights(100, 500, 100)
                            .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
                                    , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }
    }

    void notificacaoRefeicao(Context context, String refeicao) {
        int mNotificationId = 3;
        if (alarmeRefeicoes == 1) {
            //noinspection deprecation
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.refeicao)
                            .setContentTitle("Hora da refeição!")
                            .setContentText(refeicao)
                            .setLights(100, 500, 100)
                            .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
                                    , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }
    }
}
