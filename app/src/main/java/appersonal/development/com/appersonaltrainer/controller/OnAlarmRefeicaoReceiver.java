package appersonal.development.com.appersonaltrainer.controller;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.R;
import appersonal.development.com.appersonaltrainer.activities.TreinoXActivity;

import static android.content.Context.MODE_PRIVATE;

public class OnAlarmRefeicaoReceiver extends BroadcastReceiver {

    private static final String TAG = "Teste";
    private static final String HORATREINO = "HoraTreino";
    private static final String MINUTOTREINO = "MinutoTreino";
    private static final String ALARMETREINO = "AlarmeTreino";
    private static final String ALARMEREFEICOES = "AlarmeRefeicoes";
    private static final String DIASSEMANA = "DiasSemana";
    private static final String REFEICOES = "Refeicoes";
    private static final String DATA = "Data";
    private final boolean[] diasSemana = new boolean[7];
    private SQLiteDatabase bancoDados;
    private int alarmeTreino;
    private int alarmeRefeicoes;
    private int idTreino;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            bancoDados = context.openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
