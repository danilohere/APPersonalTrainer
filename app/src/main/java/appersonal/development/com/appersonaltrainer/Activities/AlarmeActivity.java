package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import appersonal.development.com.appersonaltrainer.Controller.OnAlarmAguaReceiver;
import appersonal.development.com.appersonaltrainer.Controller.OnAlarmReceiver;
import appersonal.development.com.appersonaltrainer.Controller.OnBootReceiver;
import appersonal.development.com.appersonaltrainer.Model.Refeicoes;
import appersonal.development.com.appersonaltrainer.R;

import static android.widget.Toast.makeText;

public class AlarmeActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private static final String AGUA = "Agua";
    private static final String ALARME = "Alarme";
    private static final String ALARMETREINO = "AlarmeTreino";
    private static final String ALARMEREFEICOES = "AlarmeRefeicoes";
    private static final String HORATREINO = "HoraTreino";
    private static final String MINUTOTREINO = "MinutoTreino";
    private static final String MINUTOTREINOID = "MinutoTreinoId";
    private static final String HORAINICIO = "HoraInicio";
    private static final String MINUTOINICIO = "MinutoInicio";
    private static final String MINUTOINICIOID = "MinutoInicioId";
    private static final String HORAFIM = "HoraFim";
    private static final String MINUTOFIM = "MinutoFim";
    private static final String MINUTOFIMID = "MinutoFimId";
    private static final String INTERVALO = "Intervalo";
    private static final String INTERVALOID = "IntervaloId";
    private static final String DIASSEMANA = "DiasSemana";
    private static final String REFEICOES = "Refeicoes";

    private Spinner spnHoraInicio;
    private Spinner spnMinutoInicio;
    private Spinner spnHoraFim;
    private Spinner spnMinutoFim;
    private Spinner spnHoraTreino;
    private Spinner spnMinutoTreino;
    private RelativeLayout rltAlarme;
    private RelativeLayout rltAlarmeTreino;
    private RelativeLayout rltAlarmeRefeicoes;
    private ImageView btnHide;
    private ImageView btnShow;
    private ImageView btnHideTreino;
    private ImageView btnShowTreino;
    private ImageView btnHideRefeicoes;
    private ImageView btnShowRefeicoes;
    private Switch swtAlertaTreino;
    private Switch swtDespertador;
    private Button btnSegunda;
    private Button btnTerca;
    private Button btnQuarta;
    private Button btnQuinta;
    private Button btnSexta;
    private Button btnSabado;
    private Button btnDomingo;
    private AdapterRefeicoesPersonalizado adapter;
    private InterstitialAd mInterstitialAd;

    private ArrayList<Refeicoes> refeicoes;
    private ArrayAdapter<String> adapterHoras;
    private ArrayAdapter<String> adapterMinutos;

    private RelativeLayout.LayoutParams params;

    private boolean[] diasSemana = new boolean[7];
    private int ht;
    private int mt;
    private int hi;
    private int mi;
    private int hf;
    private int mf;
    private int mtid;
    private int miid;
    private int mfid;
    private int inter;
    private int lstposition;
    private long i;

    private String[] intervalo = {
            "15 minutos", "30 minutos", "1 hora", "2 horas", "3 horas", "4 horas"
    };

    private String[] minutos = {
            "00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
    };

    private String[] horas = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23"
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int qtdeAlarmes;

    @Override
    public void onBackPressed() {
        qtdeAlarmes = 0;
        if (swtDespertador.isChecked()) {

            createNotification();
            qtdeAlarmes++;
        }
        if (swtAlertaTreino.isChecked()) {
            createNotificationTreino();
            qtdeAlarmes++;
        }
        if (!refeicoes.isEmpty()) {
            createNotificationRefeicoes();
            SharedPreferences alarmeRefeicoes = getSharedPreferences(ALARMEREFEICOES, 0);
            SharedPreferences.Editor editor = alarmeRefeicoes.edit();
            editor.putInt("AlarmeRefeicoes", 1);
            editor.apply();
            qtdeAlarmes++;
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                if (qtdeAlarmes == 1) {
                    makeText(AlarmeActivity.this, "Alarme configurado", Toast.LENGTH_SHORT).show();
                } else if (qtdeAlarmes > 1) {
                    makeText(AlarmeActivity.this, "Alarmes configurados", Toast.LENGTH_SHORT).show();
                }
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                if (qtdeAlarmes == 1) {
                    makeText(AlarmeActivity.this, "Alarme configurado", Toast.LENGTH_SHORT).show();
                } else if (qtdeAlarmes > 1) {
                    makeText(AlarmeActivity.this, "Alarmes configurados", Toast.LENGTH_SHORT).show();

                }
                super.onAdFailedToLoad(i);
            }
        });

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            if (qtdeAlarmes == 1) {
                makeText(AlarmeActivity.this, "Alarme configurado", Toast.LENGTH_SHORT).show();
            } else if (qtdeAlarmes > 1) {
                //makeText(AlarmeActivity.this, "Alarmes configurados", Toast.LENGTH_SHORT).show();
            }
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onBackPressed();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem alterar = menu.add("Alterar refeição");
        final MenuItem excluir = menu.add("Excluir refeição");

        alterar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                criarDialog(1);
                return true;
            }
        });
        excluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                refeicoes.remove(lstposition);
                SharedPreferences refeicoesSP = getSharedPreferences(REFEICOES, 0);
                SharedPreferences.Editor editorRefeicoes = refeicoesSP.edit();
                editorRefeicoes.putInt("refeicoes_size", refeicoes.size());
                editorRefeicoes.remove("refeicao_" + lstposition);
                editorRefeicoes.remove("hora_" + lstposition);
                editorRefeicoes.remove("minuto_" + lstposition);
                editorRefeicoes.apply();

                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .addTestDevice("6993B0696AE064D27BBDC28B90575368")
//                .addTestDevice("D5C546361B50B5162E9BCD250E5EC1D2")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .addTestDevice("6993B0696AE064D27BBDC28B90575368")
//                .addTestDevice("D5C546361B50B5162E9BCD250E5EC1D2")
                .build();
        adView2.loadAd(adRequest2);

        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-4960619699535760/4978018539");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        rltAlarme = findViewById(R.id.rltAlarme);
        rltAlarmeTreino = findViewById(R.id.rltAlarmeTreino);

        swtDespertador = findViewById(R.id.swtAlerta);
        spnHoraInicio = findViewById(R.id.spnHoraInicio);
        spnMinutoInicio = findViewById(R.id.spnMinutoInicio);
        spnHoraFim = findViewById(R.id.spnHoraFim);
        spnMinutoFim = findViewById(R.id.spnMinutoFim);
        Spinner spnIntervalo = findViewById(R.id.spnIntervalo);
        btnHide = findViewById(R.id.btnHide);
        btnShow = findViewById(R.id.btnShow);

        swtAlertaTreino = findViewById(R.id.swtAlertaTreino);
        spnHoraTreino = findViewById(R.id.spnHoraTreino);
        spnMinutoTreino = findViewById(R.id.spnMinutoTreino);
        btnHideTreino = findViewById(R.id.btnHideTreino);
        btnShowTreino = findViewById(R.id.btnShowTreino);
        btnSegunda = findViewById(R.id.btnSegunda);
        btnTerca = findViewById(R.id.btnTerca);
        btnQuarta = findViewById(R.id.btnQuarta);
        btnQuinta = findViewById(R.id.btnQuinta);
        btnSexta = findViewById(R.id.btnSexta);
        btnSabado = findViewById(R.id.btnSabado);
        btnDomingo = findViewById(R.id.btnDomingo);

        Button btnMaisRefeicoes = findViewById(R.id.btnMaisRefeicoes);
        ListView lstRefeicoes = findViewById(R.id.lstRefeicoes);


        adapterHoras = new ArrayAdapter<>(this, R.layout.spinner_item, horas);
        adapterHoras.setDropDownViewResource(R.layout.spinner_dropdown_item);

        adapterMinutos = new ArrayAdapter<>(this, R.layout.spinner_item, minutos);
        adapterMinutos.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter<String> adapterIntervalo = new ArrayAdapter<>(this, R.layout.spinner_item, intervalo);
        adapterIntervalo.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spnHoraInicio.setAdapter(adapterHoras);
        spnHoraFim.setAdapter(adapterHoras);
        spnMinutoInicio.setAdapter(adapterMinutos);
        spnMinutoFim.setAdapter(adapterMinutos);
        spnIntervalo.setAdapter(adapterIntervalo);

        spnHoraTreino.setAdapter(adapterHoras);
        spnMinutoTreino.setAdapter(adapterMinutos);

        refeicoes = new ArrayList<>();
        adapter = new AdapterRefeicoesPersonalizado(refeicoes, this);
        lstRefeicoes.setAdapter(adapter);

        SharedPreferences alarmeRefeicoes = getSharedPreferences(ALARMEREFEICOES, Context.MODE_PRIVATE);
        if (alarmeRefeicoes.getInt("AlarmeRefeicoes", 0) != 0) {
            SharedPreferences refeicoesSP = getSharedPreferences(REFEICOES, Context.MODE_PRIVATE);
            int sizeR = refeicoesSP.getInt("refeicoes_size", 0);
            for (int i = 0; i < sizeR; i++) {
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
                refeicao.setTime(time);
                refeicoes.add(refeicao);
            }
            ordenaPorNumero(refeicoes);
        }



        SharedPreferences alarme = getSharedPreferences(ALARME, Context.MODE_PRIVATE);
        if (alarme.getInt("Alarme", 0) == 0) {
            swtDespertador.setChecked(false);
        } else {
            swtDespertador.setChecked(true);
        }

        SharedPreferences alarmeTreino = getSharedPreferences(ALARMETREINO, Context.MODE_PRIVATE);
        if (alarmeTreino.getInt("AlarmeTreino", 0) == 0) {
            swtAlertaTreino.setChecked(false);
        } else {
            swtAlertaTreino.setChecked(true);
        }

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swtDespertador.isChecked()) {
                    btnShow.setVisibility(View.INVISIBLE);
                    btnHide.setVisibility(View.VISIBLE);
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.lytAlerta);
                    params.topMargin = 10;
                    rltAlarme.setLayoutParams(params);
                }
            }
        });

        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHide.setVisibility(View.INVISIBLE);
                btnShow.setVisibility(View.VISIBLE);
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 1);
                params.addRule(RelativeLayout.BELOW, R.id.lytAlerta);
                params.topMargin = 10;
                rltAlarme.setLayoutParams(params);
            }
        });

        swtDespertador.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences alarme = getSharedPreferences(ALARME,0);
                    SharedPreferences.Editor editor = alarme.edit();
                    editor.putInt("Alarme", 1);
                    editor.apply();
                    btnShow.callOnClick();
                } else {
                    SharedPreferences agua = getSharedPreferences(AGUA, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorAgua = agua.edit();
                    editorAgua.putInt("Agua", 0);
                    editorAgua.apply();
                    SharedPreferences alarme = getSharedPreferences(ALARME,0);
                    SharedPreferences.Editor editor = alarme.edit();
                    editor.putInt("Alarme", 0);
                    editor.apply();
                    btnHide.callOnClick();
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

        btnShowTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swtAlertaTreino.isChecked()) {
                    btnShowTreino.setVisibility(View.INVISIBLE);
                    btnHideTreino.setVisibility(View.VISIBLE);
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.lytAlertaTreino);
                    params.topMargin = 10;
                    rltAlarmeTreino.setLayoutParams(params);
                }
            }
        });

        btnHideTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHideTreino.setVisibility(View.INVISIBLE);
                btnShowTreino.setVisibility(View.VISIBLE);
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 1);
                params.addRule(RelativeLayout.BELOW, R.id.lytAlertaTreino);
                params.topMargin = 10;
                rltAlarmeTreino.setLayoutParams(params);
            }
        });

        swtAlertaTreino.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences alarme = getSharedPreferences(ALARMETREINO, 0);
                    SharedPreferences.Editor editor = alarme.edit();
                    editor.putInt("AlarmeTreino", 1);
                    editor.apply();
                    btnShowTreino.callOnClick();
                } else {
                    SharedPreferences alarme = getSharedPreferences(ALARMETREINO, 0);
                    SharedPreferences.Editor editor = alarme.edit();
                    editor.putInt("AlarmeTreino", 0);
                    editor.apply();
                    btnHideTreino.callOnClick();
                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    Intent alarm = new Intent(AlarmeActivity.this, OnAlarmReceiver.class);
                    alarmIntent = PendingIntent.getBroadcast(AlarmeActivity.this, 1, alarm, 0);

                    if (alarmManager != null) {
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

        SharedPreferences diasSemanaSP = getSharedPreferences(DIASSEMANA, Context.MODE_PRIVATE);
        int size = diasSemanaSP.getInt("diasSemana_size", 0);
        for (int i = 0; i < size; i++) {
            diasSemana[i] = diasSemanaSP.getBoolean("diasSemana_" + i, false);
            if (diasSemana[i]) {
                alterarBotao(i);
            }
        }

        btnSegunda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[0]) {
                    diasSemana[0] = false;
                    btnSegunda.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[0] = true;
                    btnSegunda.setBackgroundResource(R.drawable.buttonshape);
                }
            }
        });

        btnTerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[1]) {
                    diasSemana[1] = false;
                    btnTerca.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[1] = true;
                    btnTerca.setBackgroundResource(R.drawable.buttonshape);
                }
            }
        });

        btnQuarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[2]) {
                    diasSemana[2] = false;
                    btnQuarta.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[2] = true;
                    btnQuarta.setBackgroundResource(R.drawable.buttonshape);
                }
            }
        });

        btnQuinta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[3]) {
                    diasSemana[3] = false;
                    btnQuinta.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[3] = true;
                    btnQuinta.setBackgroundResource(R.drawable.buttonshape);
                }
            }
        });

        btnSexta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[4]) {
                    diasSemana[4] = false;
                    btnSexta.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[4] = true;
                    btnSexta.setBackgroundResource(R.drawable.buttonshape);
                }
            }
        });

        btnSabado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[5]) {
                    diasSemana[5] = false;
                    btnSabado.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[5] = true;
                    btnSabado.setBackgroundResource(R.drawable.buttonshape);
                }
            }
        });

        btnDomingo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (diasSemana[6]) {
                    diasSemana[6] = false;
                    btnDomingo.setBackgroundResource(R.color.list_background);
                } else {
                    diasSemana[6] = true;
                    btnDomingo.setBackgroundResource(R.drawable.buttonshape);
                }
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
                miid = position;
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
                mfid = position;
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

        spnHoraTreino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ht = Integer.parseInt(spnHoraTreino.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnMinutoTreino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mt = Integer.parseInt(spnMinutoTreino.getSelectedItem().toString());
                mtid = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lstRefeicoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lstposition = position;
                registerForContextMenu(parent);
                openContextMenu(parent);
            }
        });

        btnMaisRefeicoes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                criarDialog(0);
            }
        });

        SharedPreferences horainicioAlarm = getSharedPreferences(HORAINICIO, Context.MODE_PRIVATE);
        spnHoraInicio.setSelection(horainicioAlarm.getInt("HoraInicio", 0));

        SharedPreferences minutoinicioAlarm = getSharedPreferences(MINUTOINICIOID, Context.MODE_PRIVATE);
        spnMinutoInicio.setSelection(minutoinicioAlarm.getInt("MinutoInicioId", 0));

        SharedPreferences horafimAlarm = getSharedPreferences(HORAFIM, Context.MODE_PRIVATE);
        spnHoraFim.setSelection(horafimAlarm.getInt("HoraFim", 0));

        SharedPreferences minutofimAlarm = getSharedPreferences(MINUTOFIMID, Context.MODE_PRIVATE);
        spnMinutoFim.setSelection(minutofimAlarm.getInt("MinutoFimId", 0));

        SharedPreferences intervaloAlarm = getSharedPreferences(INTERVALOID, Context.MODE_PRIVATE);
        spnIntervalo.setSelection(intervaloAlarm.getInt("IntervaloId", 0));

        SharedPreferences horaTreinoAlarm = getSharedPreferences(HORATREINO, Context.MODE_PRIVATE);
        spnHoraTreino.setSelection(horaTreinoAlarm.getInt("HoraTreino", 0));

        SharedPreferences minutoTreinoAlarm = getSharedPreferences(MINUTOTREINOID, Context.MODE_PRIVATE);
        spnMinutoTreino.setSelection(minutoTreinoAlarm.getInt("MinutoTreinoId", 0));

    }


    private void createNotification(){
        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme = new Intent(AlarmeActivity.this, OnAlarmAguaReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(AlarmeActivity.this, 0, alarme, 0);

        SharedPreferences horainicioAlarm = getSharedPreferences(HORAINICIO,0);
        SharedPreferences.Editor editorHoraInicio = horainicioAlarm.edit();
        editorHoraInicio.putInt("HoraInicio", hi);
        editorHoraInicio.apply();

        SharedPreferences minutoinicioAlarm = getSharedPreferences(MINUTOINICIO,0);
        SharedPreferences.Editor editorMinutoInicio = minutoinicioAlarm.edit();
        editorMinutoInicio.putInt("MinutoInicio", mi);
        editorMinutoInicio.apply();
        SharedPreferences minutoinicioIdAlarm = getSharedPreferences(MINUTOINICIOID, 0);
        SharedPreferences.Editor editorMinutoInicioId = minutoinicioIdAlarm.edit();
        editorMinutoInicioId.putInt("MinutoInicioId", miid);
        editorMinutoInicioId.apply();

        SharedPreferences horafimAlarm = getSharedPreferences(HORAFIM,0);
        SharedPreferences.Editor editorHoraFim = horafimAlarm.edit();
        editorHoraFim.putInt("HoraFim", hf);
        editorHoraFim.apply();

        SharedPreferences minutofimAlarm = getSharedPreferences(MINUTOFIM,0);
        SharedPreferences.Editor editorMinutoFim = minutofimAlarm.edit();
        editorMinutoFim.putInt("MinutoFim", mf);
        editorMinutoFim.apply();
        SharedPreferences minutofimIdAlarm = getSharedPreferences(MINUTOFIMID, 0);
        SharedPreferences.Editor editorMinutoFimId = minutofimIdAlarm.edit();
        editorMinutoFimId.putInt("MinutoFimId", mfid);
        editorMinutoFimId.apply();

        SharedPreferences intervaloAlarm = getSharedPreferences(INTERVALO,0);
        SharedPreferences.Editor editorIntervalo = intervaloAlarm.edit();
        editorIntervalo.putLong("Intervalo", i);
        editorIntervalo.apply();

        SharedPreferences intervaloIdAlarm = getSharedPreferences(INTERVALOID,0);
        SharedPreferences.Editor editorIntervaloId = intervaloIdAlarm.edit();
        editorIntervaloId.putInt("IntervaloId", inter);
        editorIntervaloId.apply();

        Calendar cmeia = Calendar.getInstance();
        cmeia.setTimeInMillis(System.currentTimeMillis());
        cmeia.set(Calendar.HOUR_OF_DAY, 0);
        cmeia.set(Calendar.MINUTE, 0);
        long meianoite = cmeia.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, meianoite, i, alarmIntent);

        ComponentName receiver = new ComponentName(this, OnBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void createNotificationTreino() {
        SharedPreferences diasSemanaSP = getSharedPreferences(DIASSEMANA, 0);
        SharedPreferences.Editor editorDiasSemana = diasSemanaSP.edit();
        editorDiasSemana.putInt("diasSemana_size", diasSemana.length);

        for (int i = 0; i < diasSemana.length; i++)
            editorDiasSemana.putBoolean("diasSemana_" + i, diasSemana[i]);

        editorDiasSemana.apply();

        alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        Intent alarme = new Intent(AlarmeActivity.this, OnAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(AlarmeActivity.this, 1, alarme, 0);

        Calendar ct = Calendar.getInstance();
        ct.setTimeInMillis(System.currentTimeMillis());
        ct.set(Calendar.HOUR_OF_DAY, ht);
        ct.set(Calendar.MINUTE, mt);
        long alarmeTreino = ct.getTimeInMillis();

        SharedPreferences horaTreinoAlarm = getSharedPreferences(HORATREINO, 0);
        SharedPreferences.Editor editorHoraTreino = horaTreinoAlarm.edit();
        editorHoraTreino.putInt("HoraTreino", ht);
        editorHoraTreino.apply();

        SharedPreferences minutoTreinoAlarm = getSharedPreferences(MINUTOTREINO, 0);
        SharedPreferences.Editor editorMinutoTreino = minutoTreinoAlarm.edit();
        editorMinutoTreino.putInt("MinutoTreino", mt);
        editorMinutoTreino.apply();
        SharedPreferences minutoTreinoIdAlarm = getSharedPreferences(MINUTOTREINOID, 0);
        SharedPreferences.Editor editorMinutoTreinoId = minutoTreinoIdAlarm.edit();
        editorMinutoTreinoId.putInt("MinutoTreinoId", mtid);
        editorMinutoTreinoId.apply();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmeTreino, 1000 * 60, alarmIntent);

        ComponentName receiver = new ComponentName(this, OnBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void alterarBotao(int dia) {
        switch (dia) {
            case 0:
                btnSegunda.setBackgroundResource(R.drawable.buttonshape);
                break;
            case 1:
                btnTerca.setBackgroundResource(R.drawable.buttonshape);
                break;
            case 2:
                btnQuarta.setBackgroundResource(R.drawable.buttonshape);
                break;
            case 3:
                btnQuinta.setBackgroundResource(R.drawable.buttonshape);
                break;
            case 4:
                btnSexta.setBackgroundResource(R.drawable.buttonshape);
                break;
            case 5:
                btnSabado.setBackgroundResource(R.drawable.buttonshape);
                break;
            case 6:
                btnDomingo.setBackgroundResource(R.drawable.buttonshape);
                break;
        }
    }

    private void createNotificationRefeicoes() {
        SharedPreferences refeicoesSP = getSharedPreferences(REFEICOES, 0);
        SharedPreferences.Editor editorRefeicoes = refeicoesSP.edit();
        editorRefeicoes.putInt("refeicoes_size", refeicoes.size());
        for (int i = 0; i < refeicoes.size(); i++) {
            editorRefeicoes.putString("refeicao_" + i, refeicoes.get(i).getRefeicao());
            editorRefeicoes.putString("hora_" + i, refeicoes.get(i).getHora());
            editorRefeicoes.putString("minuto_" + i, refeicoes.get(i).getMinuto());

            alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            Intent alarme = new Intent(AlarmeActivity.this, OnAlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(AlarmeActivity.this, i + 2, alarme, 0);

            Calendar ct = Calendar.getInstance();
            ct.setTimeInMillis(System.currentTimeMillis());
            ct.set(Calendar.HOUR_OF_DAY, Integer.parseInt(refeicoes.get(i).getHora()));
            ct.set(Calendar.MINUTE, Integer.parseInt(refeicoes.get(i).getMinuto()));
            long alarmeRefeicao = ct.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmeRefeicao, 1000 * 60, alarmIntent);

            ComponentName receiver = new ComponentName(this, OnBootReceiver.class);
            PackageManager pm = this.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
        editorRefeicoes.apply();
    }

    public class AdapterRefeicoesPersonalizado extends BaseAdapter {

        private final List<Refeicoes> refeicoes;
        private final Activity activity;

        AdapterRefeicoesPersonalizado(List<Refeicoes> refeicoes, Activity activity) {
            this.refeicoes = refeicoes;
            this.activity = activity;
        }


        @Override
        public int getCount() {
            return refeicoes.size();
        }

        @Override
        public Object getItem(int position) {
            return refeicoes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_refeicoes, parent, false);

            Refeicoes refeicao = refeicoes.get(position);

            TextView txtRefeicao =
                    view.findViewById(R.id.txtNome);
            TextView txtHora =
                    view.findViewById(R.id.txtHora);

            txtRefeicao.setText(refeicao.getRefeicao());
            String h = refeicao.getHora();
            String m = refeicao.getMinuto();
            txtHora.setText(h + ":" + m);
            return view;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void criarDialog(final int alt) {
        final Dialog dialog = new Dialog(AlarmeActivity.this);
        dialog.setContentView(R.layout.dialog_refeicoes);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("Alarme de Refeição");
        final EditText edtRefeicao = dialog.findViewById(R.id.edtRefeicao);
        final Spinner spnHoraRefeicao = dialog.findViewById(R.id.spnHoraRefeicao);
        final Spinner spnMinutoRefeicao = dialog.findViewById(R.id.spnMinutoRefeicao);

        spnHoraRefeicao.setAdapter(adapterHoras);
        spnMinutoRefeicao.setAdapter(adapterMinutos);
        if (alt == 1) {
            edtRefeicao.setText(refeicoes.get(lstposition).getRefeicao());
            spnHoraRefeicao.setSelection(Integer.parseInt(refeicoes.get(lstposition).getHora()));
            switch (Integer.parseInt(refeicoes.get(lstposition).getMinuto())) {
                case 0:
                    spnMinutoRefeicao.setSelection(0);
                    break;
                case 5:
                    spnMinutoRefeicao.setSelection(1);
                    break;
                case 10:
                    spnMinutoRefeicao.setSelection(2);
                    break;
                case 15:
                    spnMinutoRefeicao.setSelection(3);
                    break;
                case 20:
                    spnMinutoRefeicao.setSelection(4);
                    break;
                case 25:
                    spnMinutoRefeicao.setSelection(5);
                    break;
                case 30:
                    spnMinutoRefeicao.setSelection(6);
                    break;
                case 35:
                    spnMinutoRefeicao.setSelection(7);
                    break;
                case 40:
                    spnMinutoRefeicao.setSelection(8);
                    break;
                case 45:
                    spnMinutoRefeicao.setSelection(9);
                    break;
                case 50:
                    spnMinutoRefeicao.setSelection(10);
                    break;
                case 55:
                    spnMinutoRefeicao.setSelection(11);
                    break;
            }
        }


        Button btnOkRefeicao = dialog.findViewById(R.id.btnOkRefeicao);

        edtRefeicao.requestFocus();

        btnOkRefeicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtRefeicao.getText().toString().trim().isEmpty()) {
                    dialog.dismiss();
                } else {
                    Refeicoes refeicao = new Refeicoes();
                    refeicao.setRefeicao(edtRefeicao.getText().toString().trim());
                    refeicao.setHora(spnHoraRefeicao.getSelectedItem().toString());
                    refeicao.setMinuto(spnMinutoRefeicao.getSelectedItem().toString());
                    int h = Integer.parseInt(spnHoraRefeicao.getSelectedItem().toString());
                    int m = Integer.parseInt(spnMinutoRefeicao.getSelectedItem().toString());
                    Calendar cr = Calendar.getInstance();
                    cr.setTimeInMillis(System.currentTimeMillis());
                    cr.set(Calendar.HOUR_OF_DAY, h);
                    cr.set(Calendar.MINUTE, m);
                    long time = cr.getTimeInMillis();
                    refeicao.setTime(time);
                    if (alt == 1) {
                        refeicoes.remove(lstposition);
                        makeText(AlarmeActivity.this, "Refeição alterada", Toast.LENGTH_SHORT).show();
                    } else {
                        makeText(AlarmeActivity.this, "Refeição adicionada", Toast.LENGTH_SHORT).show();
                    }
                    refeicoes.add(refeicao);
                    ordenaPorNumero(refeicoes);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(edtRefeicao.getWindowToken(), 0);
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        dialog.show();
    }

    private static void ordenaPorNumero(List<Refeicoes> refeicoes) {
        Collections.sort(refeicoes, new Comparator<Refeicoes>() {
            @Override
            public int compare(Refeicoes o1, Refeicoes o2) {
                Integer t1 = (int) o1.getTime();
                Integer t2 = (int) o2.getTime();
                return t1.compareTo(t2);
            }
        });
    }

}
