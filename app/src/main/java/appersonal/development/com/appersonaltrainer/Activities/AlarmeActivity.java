package appersonal.development.com.appersonaltrainer.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

import appersonal.development.com.appersonaltrainer.Controller.OnAlarmReceiver;
import appersonal.development.com.appersonaltrainer.Controller.OnBootReceiver;
import appersonal.development.com.appersonaltrainer.R;

public class AlarmeActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private static final String ALARME = "Alarme";
    private static final String HORAINICIO = "HoraInicio";
    private static final String MINUTOINICIO = "MinutoInicio";
    private static final String HORAFIM = "HoraFim";
    private static final String MINUTOFIM = "MinutoFim";
    private static final String INTERVALO = "Intervalo";
    private static final String INTERVALOID = "IntervaloId";

    private Switch swtDespertador;
    private ImageView imgInfo;
    private Spinner spnHoraInicio;
    private Spinner spnMinutoInicio;
    private Spinner spnHoraFim;
    private Spinner spnMinutoFim;
    private Spinner spnIntervalo;
    private Button btnSalvar;
    private RelativeLayout rltAlarme;

    private int hi;
    private int mi;
    private int hf;
    private int mf;
    private int inter;
    private long i;

    private String[] intervalo = {
            "15 minutos", "30 minutos", "1 hora", "2 horas", "3 horas", "4 horas"
    };

    private String[] minutos = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"
    };

    private String[] horas = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23"
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);

        //Implementa o botão voltar na ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
//                .addTestDevice("D5C546361B50B5162E9BCD250E5EC1D2")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
//                .addTestDevice("D5C546361B50B5162E9BCD250E5EC1D2")
                .build();
        adView2.loadAd(adRequest2);

        imgInfo = (ImageView) findViewById(R.id.imgInfo);
        swtDespertador = (Switch) findViewById(R.id.swtAlerta);
        rltAlarme = (RelativeLayout) findViewById(R.id.rltAlarme);
        spnHoraInicio = (Spinner) findViewById(R.id.spnHoraInicio);
        spnMinutoInicio = (Spinner) findViewById(R.id.spnMinutoInicio);
        spnHoraFim = (Spinner) findViewById(R.id.spnHoraFim);
        spnMinutoFim = (Spinner) findViewById(R.id.spnMinutoFim);
        spnIntervalo = (Spinner) findViewById(R.id.spnIntervalo);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        ArrayAdapter<String> adapHoras = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                horas
        );

        ArrayAdapter<String> adapMinutos = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                minutos
        );

        ArrayAdapter<String> adapIntervalo = new ArrayAdapter<String>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                intervalo
        );


        spnHoraInicio.setAdapter(adapHoras);
        spnHoraFim.setAdapter(adapHoras);
        spnMinutoInicio.setAdapter(adapMinutos);
        spnMinutoFim.setAdapter(adapMinutos);
        spnIntervalo.setAdapter(adapIntervalo);

        SharedPreferences alarme = getSharedPreferences(ALARME, Context.MODE_PRIVATE);
        if (alarme.getInt("Alarme", 0) == 0) {
            swtDespertador.setChecked(false);
        } else {
            swtDespertador.setChecked(true);
        }

        if (swtDespertador.isChecked()){
            rltAlarme.setVisibility(View.VISIBLE);
        } else {
            rltAlarme.setVisibility(View.INVISIBLE);
        }

        swtDespertador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences alarme = getSharedPreferences(ALARME,0);
                    SharedPreferences.Editor editor = alarme.edit();
                    editor.putInt("Alarme", 1);
                    editor.commit();
                    rltAlarme.setVisibility(View.VISIBLE);
                } else {
                    SharedPreferences alarme = getSharedPreferences(ALARME,0);
                    SharedPreferences.Editor editor = alarme.edit();
                    editor.putInt("Alarme", 0);
                    editor.commit();
                    rltAlarme.setVisibility(View.INVISIBLE);
                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent alarm = new Intent(AlarmeActivity.this, OnAlarmReceiver.class);
                    alarmIntent = PendingIntent.getBroadcast(AlarmeActivity.this, 0, alarm, 0);

                    if (alarmManager!= null) {
                        alarmManager.cancel(alarmIntent);
                    }

                    ComponentName receiver = new ComponentName(AlarmeActivity.this, OnBootReceiver.class);
                    PackageManager pm = getPackageManager();

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AlarmeActivity.this, "Configurar um lembrete para tomar água", Toast.LENGTH_SHORT).show();
            }
        });

        spnHoraInicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hi = Integer.parseInt(spnHoraInicio.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnMinutoInicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mi = Integer.parseInt(spnMinutoInicio.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnHoraFim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hf = Integer.parseInt(spnHoraFim.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnMinutoFim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mf = Integer.parseInt(spnMinutoFim.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnIntervalo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                inter = position;
                switch(position){
                    case 0:
                        i = 15 * 1000 * 60;
//                        i = 1000 * 60;
                        break;
                    case 1:
                        i = 30 * 1000 * 60;
                        break;
                    case 2:
                        i = 60 * 1000 * 60;
                        break;
                    case 3:
                        i = 120 * 1000 * 60;
                        break;
                    case 4:
                        i = 180 * 1000 * 60;
                        break;
                    case 5:
                        i = 240 * 1000 * 60;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences horainicioAlarm = getSharedPreferences(HORAINICIO, Context.MODE_PRIVATE);
        spnHoraInicio.setSelection(horainicioAlarm.getInt("HoraInicio", 0));

        SharedPreferences minutoinicioAlarm = getSharedPreferences(MINUTOINICIO, Context.MODE_PRIVATE);
        spnMinutoInicio.setSelection(minutoinicioAlarm.getInt("MinutoInicio", 0));


        SharedPreferences horafimAlarm = getSharedPreferences(HORAFIM, Context.MODE_PRIVATE);
        spnHoraFim.setSelection(horafimAlarm.getInt("HoraFim", 0));


        SharedPreferences minutofimAlarm = getSharedPreferences(MINUTOFIM, Context.MODE_PRIVATE);
        spnMinutoFim.setSelection(minutofimAlarm.getInt("MinutoFim", 0));

        SharedPreferences intervaloAlarm = getSharedPreferences(INTERVALOID, Context.MODE_PRIVATE);
        spnIntervalo.setSelection(intervaloAlarm.getInt("IntervaloId", 0));


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
                Toast.makeText(AlarmeActivity.this, "Alarme configurado", Toast.LENGTH_SHORT).show();
                finish();

            }
        });




    }


    private void createNotification(){
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme = new Intent(AlarmeActivity.this, OnAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(AlarmeActivity.this, 0, alarme, 0);

        Calendar ci = Calendar.getInstance();
        ci.setTimeInMillis(System.currentTimeMillis());
        ci.set(Calendar.HOUR_OF_DAY, hi);
        ci.set(Calendar.MINUTE, mi);
        long inicio = ci.getTimeInMillis();

        Calendar cf = Calendar.getInstance();
        cf.setTimeInMillis(System.currentTimeMillis());
        cf.set(Calendar.HOUR_OF_DAY, hf);
        cf.set(Calendar.MINUTE, mf);
        long fim = cf.getTimeInMillis();

        SharedPreferences horainicioAlarm = getSharedPreferences(HORAINICIO,0);
        SharedPreferences.Editor editorHoraInicio = horainicioAlarm.edit();
        editorHoraInicio.putInt("HoraInicio", hi);
        editorHoraInicio.commit();

        SharedPreferences minutoinicioAlarm = getSharedPreferences(MINUTOINICIO,0);
        SharedPreferences.Editor editorMinutoInicio = minutoinicioAlarm.edit();
        editorMinutoInicio.putInt("MinutoInicio", mi);
        editorMinutoInicio.commit();


        SharedPreferences horafimAlarm = getSharedPreferences(HORAFIM,0);
        SharedPreferences.Editor editorHoraFim = horafimAlarm.edit();
        editorHoraFim.putInt("HoraFim", hf);
        editorHoraFim.commit();

        SharedPreferences minutofimAlarm = getSharedPreferences(MINUTOFIM,0);
        SharedPreferences.Editor editorMinutoFim = minutofimAlarm.edit();
        editorMinutoFim.putInt("MinutoFim", mf);
        editorMinutoFim.commit();

        SharedPreferences intervaloAlarm = getSharedPreferences(INTERVALO,0);
        SharedPreferences.Editor editorIntervalo = intervaloAlarm.edit();
        editorIntervalo.putLong("Intervalo", i);
        editorIntervalo.commit();

        SharedPreferences intervaloIdAlarm = getSharedPreferences(INTERVALOID,0);
        SharedPreferences.Editor editorIntervaloId = intervaloIdAlarm.edit();
        editorIntervaloId.putInt("IntervaloId", inter);
        editorIntervaloId.commit();

        if (hi > hf){
            Calendar cmeia = Calendar.getInstance();
            cmeia.setTimeInMillis(System.currentTimeMillis());
            cmeia.set(Calendar.HOUR_OF_DAY, 0);
            cmeia.set(Calendar.MINUTE, 0);
            long meianoite = cmeia.getTimeInMillis();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, i, alarmIntent);
        } else {

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, inicio, i, alarmIntent);
        }

        ComponentName receiver = new ComponentName(this, OnBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
