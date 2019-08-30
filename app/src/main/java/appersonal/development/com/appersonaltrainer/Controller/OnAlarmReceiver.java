package appersonal.development.com.appersonaltrainer.Controller;

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
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.Activities.TreinoXActivity;
import appersonal.development.com.appersonaltrainer.R;

import static android.content.Context.MODE_PRIVATE;

public class OnAlarmReceiver extends BroadcastReceiver {

    private SQLiteDatabase bancoDados;
    private static final String HORATREINO = "HoraTreino";
    private static final String MINUTOTREINO = "MinutoTreino";
    private static final String ALARMETREINO = "AlarmeTreino";
    private static final String ALARMEREFEICOES = "AlarmeRefeicoes";
    private static final String DIASSEMANA = "DiasSemana";
    private static final String REFEICOES = "Refeicoes";
    private int alarmeTreino;
    private int alarmeRefeicoes;
    private String treino;
    private String atual;
    private int idTreino;
    private boolean[] diasSemana = new boolean[7];

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
        int ds = ca.get(Calendar.DAY_OF_WEEK);
        ds = ds - 2;
        if (ds == -1)
            ds = 6;

        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM treinos ORDER BY data DESC", null);

            cursor.moveToFirst();

            int indData = cursor.getColumnIndex("data");

            long dataTreino = cursor.getLong(indData);
            long dataAtual = System.currentTimeMillis();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
            treino = formatarData.format(dataTreino);
            atual = formatarData.format(dataAtual);

            cursor.moveToLast();
            int indId = cursor.getColumnIndex("idTreino");
            idTreino = cursor.getInt(indId);

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences horaTreinoAlarm = context.getSharedPreferences(HORATREINO, MODE_PRIVATE);
        int ht = horaTreinoAlarm.getInt("HoraTreino", 0);
        SharedPreferences minutoTreinoAlarm = context.getSharedPreferences(MINUTOTREINO, MODE_PRIVATE);
        int mt = minutoTreinoAlarm.getInt("MinutoTreino", 0);
        SharedPreferences alarmTreino = context.getSharedPreferences(ALARMETREINO, MODE_PRIVATE);
        SharedPreferences diasSemanaSP = context.getSharedPreferences(DIASSEMANA, MODE_PRIVATE);
        int size = diasSemanaSP.getInt("diasSemana_size", 0);
        for (int i = 0; i < size; i++) {
            diasSemana[i] = diasSemanaSP.getBoolean("diasSemana_" + i, false);
        }
        alarmeTreino = alarmTreino.getInt("AlarmeTreino", 0);
        if (h == ht && m == mt) {
            if (diasSemana[ds] && !treino.equals(atual))
                notificacaoTreino(context);
        }
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

    void notificacaoTreino(Context context) {

        Intent intentT = new Intent(context, TreinoXActivity.class);
        intentT.putExtra("idTreino", idTreino);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentT, 0);

        int mNotificationId = 2;
        if (alarmeTreino == 1) {
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
                    .setSmallIcon(R.drawable.applogopush)
                    .setColor(ContextCompat.getColor(context, R.color.background_app))
                    .setContentTitle("HORA DO TREINO!")
                    .setContentText("Bora treinar?")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setLights(100, 500, 100)
                    .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
                            , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});
            assert notificationManager != null;
            notificationManager.notify(mNotificationId, builder.build());
            //            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(context)
//                            .setSmallIcon(R.drawable.applogopush)
//                            .setContentTitle("HORA DO TREINO!")
//                            .setContentText("Bora treinar?")
//                            .setLights(100, 500, 100)
//                            .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
//                                    , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});
//            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//            if (mNotifyMgr != null) {
//                mNotifyMgr.notify(mNotificationId, mBuilder.build());
//            }
        }
    }

    void notificacaoRefeicao(Context context, String refeicao) {
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
                    .setLights(100, 500, 100)
                    .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
                            , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});
            assert notificationManager != null;
            notificationManager.notify(mNotificationId, builder.build());
//            //noinspection deprecation
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(context)
//                            .setSmallIcon(R.drawable.refeicao)
//                            .setContentTitle("Hora da refeição!")
//                            .setContentText(refeicao)
//                            .setLights(100, 500, 100)
//                            .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
//                                    , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});
//            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//            if (mNotifyMgr != null) {
//                mNotifyMgr.notify(mNotificationId, mBuilder.build());
//            }
        }
    }
}
