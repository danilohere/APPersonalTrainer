package appersonal.development.com.appersonaltrainer.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.R;

import static android.content.Context.MODE_PRIVATE;

public class OnAlarmRefeicaoReceiver extends BroadcastReceiver {

    private static final String ALARMEREFEICOES = "AlarmeRefeicoes";
    private static final String REFEICOES = "Refeicoes";
    private int alarmeRefeicoes;

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        int h = ca.get(Calendar.HOUR_OF_DAY);
        int m = ca.get(Calendar.MINUTE);

        SharedPreferences alarmRefeicoes = context.getSharedPreferences(ALARMEREFEICOES, MODE_PRIVATE);
        alarmeRefeicoes = alarmRefeicoes.getInt("AlarmeRefeicoes", 0);
        SharedPreferences refeicoesSP = context.getSharedPreferences(REFEICOES, MODE_PRIVATE);
        int sizeR = refeicoesSP.getInt("refeicoes_size", 0);
        for (int i = 0; i < sizeR; i++) {
            int hr = Integer.parseInt(refeicoesSP.getString("hora_" + i, ""));
            int mr = Integer.parseInt(refeicoesSP.getString("minuto_" + i, ""));
            if (h == hr && m == mr) {
                notificacaoRefeicao(context, refeicoesSP.getString("refeicao_" + i, ""));
            }
        }
    }

    private void notificacaoRefeicao(Context context, String refeicao) {
        int mNotificationId = 3;
        if (alarmeRefeicoes == 1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
                builder = new NotificationCompat.Builder(context, notificationChannel.getId());
            } else {
                //noinspection deprecation
                builder = new NotificationCompat.Builder(context);
            }

            builder = builder
                    .setSmallIcon(R.drawable.refeicao)
                    .setContentTitle("Hora da refeição!")
                    .setContentText(refeicao)
                    .setLights(100, 500, 100);
            assert notificationManager != null;
            notificationManager.notify(mNotificationId, builder.build());
        }
    }
}
