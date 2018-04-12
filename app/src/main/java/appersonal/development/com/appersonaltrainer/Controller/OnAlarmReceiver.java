package appersonal.development.com.appersonaltrainer.Controller;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class OnAlarmReceiver extends BroadcastReceiver {

    private static final String HORAFIM = "HoraFim";
    private static final String MINUTOFIM = "MinutoFim";
    private static final String HORAINICIO = "HoraInicio";
    private static final String MINUTOINICIO = "MinutoInicio";
    private static final String ALARME = "Alarme";
    private int alarme;


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences horafimAlarm = context.getSharedPreferences(HORAFIM, Context.MODE_PRIVATE);
        int hf = horafimAlarm.getInt("HoraFim", 0);
        SharedPreferences minutofimAlarm = context.getSharedPreferences(MINUTOFIM, Context.MODE_PRIVATE);
        int mf = minutofimAlarm.getInt("MinutoFim", 0);
        SharedPreferences horainicioAlarm = context.getSharedPreferences(HORAINICIO, Context.MODE_PRIVATE);
        int hi = horainicioAlarm.getInt("HoraInicio", 0);
        SharedPreferences minutoinicioAlarm = context.getSharedPreferences(MINUTOINICIO, Context.MODE_PRIVATE);
        int mi = minutoinicioAlarm.getInt("MinutoInicio", 0);
        SharedPreferences alarm = context.getSharedPreferences(ALARME, Context.MODE_PRIVATE);
        alarme = alarm.getInt("Alarme", 0);

        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(System.currentTimeMillis());
        int h = ca.get(Calendar.HOUR_OF_DAY);
        int m = ca.get(Calendar.MINUTE);

        //Se o alarme não precisar virar a noite
        if (hi <= hf) {
            //se a hora de inicio e a hora final forem iguais
            if (hi == hf) {
                //ele verifica se o minuto inicial e minuto final também são iguais
                if (mi == mf) {
                    //se for, ele vai tocar sem restrição de horário
                    notificacao(context);
                }
                //se o minuto inicial for menor que o final
                else if (mi < mf) {
                    //verifica se a hora atual é igual
                    if (h == hi) {
                        //verifica se o minuto atual é maior que o inicial e menor que o final
                        if (m >= mi && m <= mf) {
                            //se sim, ele dispara o alarme
                            notificacao(context);
                        }
                    }
                }
                //se o minuto inicial for maior que o minuto final
                else {
                    //o alarme verifica se o horário atual é antes da meia noite
                    if (h < 24) {
                        //se for antes da meia noite, ele só precisa verificar se já é hora de iniciar, se a hora for maior, ele notifica
                        if (h > hi) {
                            notificacao(context);
                        }
                        //se hora for igual, ele precisa verificar se o minuto atual é maior ou igual ao inicial
                        else if (h == hi) {
                            if (m >= mi) {
                                notificacao(context);
                            }
                        }
                    }
                    //se for depois da meia noite
                    else {
                        //ele verifica se o horário atual é menor que o horário de fim, se for, ele notifica
                        if (h < hf) {
                            notificacao(context);
                        }
                        // se não for, ele verifica se a hora é igual
                        else if (h == hf) {
                            //se for igual, ele verifica se o minuto atual é menor ou igual ao final
                            if (m <= mf) {
                                notificacao(context);
                            }
                        }
                    }
                }
            }
            //se a hora de inicio for menor que a hora final
            else {
                //se a hora atual for maior ou igual a hora inicial
                if (h >= hi) {
                    //se a hora atual for igual a hora inicial
                    if (h == hi) {
                        //ele verifica se o minuto atual é maior que o inicial
                        if (m >= mi) {
                            //se for, ele envia a notificação
                            notificacao(context);
                        }
                    }
                    //se a hora atual for maior que a hora inicial
                    else {
                        //ele verifica se a hora atual é menor ou igual a hora final
                        if (h <= hf) {
                            //se for, ele verifica se é igual
                            if (h == hf) {
                                //se for igual, ele verifica o minuto atual para saber se é menor que o final
                                if (m <= mf) {
                                    //se sim, ele envia  a notificação
                                    notificacao(context);
                                }
                            }
                            // se for menor
                            else {
                                //ele não precisa verificar os minutos e já envia a notificação
                                notificacao(context);
                            }
                        }
                    }
                }
            }
        }
        //se o alarme precisar virar a noite
        else {
            //o alarme verifica se o horário atual é antes da meia noite
            if (h >= hi) {
                //se for antes da meia noite, ele só precisa verificar se já é hora de iniciar, se a hora for maior, ele notifica
                if (h > hi) {
                    notificacao(context);
                }
                //se hora for igual, ele precisa verificar se o minuto atual é maior ou igual ao inicial
                else if (h == hi) {
                    if (m >= mi) {
                        notificacao(context);
                    }
                }
            }
            //se for depois da meia noite
            else {
                //ele verifica se o horário atual é menor que o horário de fim, se for, ele notifica
                if (h < hf) {
                    notificacao(context);
                }
                // se não for, ele verifica se a hora é igual
                else if (h == hf) {
                    //se for igual, ele verifica se o minuto atual é menor ou igual ao final
                    if (m <= mf) {
                        notificacao(context);
                    }
                }
            }
        }
    }


    void notificacao(Context context) {
        if (alarme == 1) {
            //noinspection deprecation
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.applogopush)
                            .setContentTitle("Alerta de água")
                            .setContentText("Hora de tomar água!")
                            .setLights(100, 500, 100)
                            .setVibrate(new long[]{100, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500
                                    , 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500, 300, 500});

            int mNotificationId = 1;
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            if (mNotifyMgr != null) {
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }
        }
    }
}
