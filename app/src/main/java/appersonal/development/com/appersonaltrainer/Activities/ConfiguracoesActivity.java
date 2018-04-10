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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.Controller.OnAlarmReceiver;
import appersonal.development.com.appersonaltrainer.Controller.OnBootReceiver;
import appersonal.development.com.appersonaltrainer.R;


public class ConfiguracoesActivity extends AppCompatActivity {

    private Switch swtInicioAut;
    private Switch swtBotaoFone;

    private RadioGroup rdgVoz;
    private RadioButton rdbMasculina;
    private RadioButton rdbFeminina;
    private RadioGroup rdgSons;
    private RadioButton rdbPadrao;
    private RadioButton rdbBambam;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private static final String INICIO_AUTOMATICO = "InicioAut";
    private static final String VOZ = "Voz";
    private static final String SONS = "Sons";
    private static final String BOTAOFONE = "BotaoFone";

    private static final String INTERVALOID = "IntervaloId";

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
        setContentView(R.layout.activity_configuracoes);

        //Implementa o botão voltar na ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView2.loadAd(adRequest2);

        swtInicioAut = (Switch) findViewById(R.id.swtInicioAut);
        rdgVoz = (RadioGroup) findViewById(R.id.rdgvoz);
        rdbMasculina = (RadioButton) findViewById(R.id.rdbMasculina);
        rdbFeminina = (RadioButton) findViewById(R.id.rdbFeminina);
        swtBotaoFone = (Switch) findViewById(R.id.swtBotaoFone);
        rdgSons = (RadioGroup) findViewById(R.id.rdgSons);
        rdbPadrao = (RadioButton) findViewById(R.id.rdbSomPadrao);
        rdbBambam = (RadioButton) findViewById(R.id.rdbSomBambam);




        swtInicioAut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences inicioaut = getSharedPreferences(INICIO_AUTOMATICO,0);
                    SharedPreferences.Editor editor = inicioaut.edit();
                    editor.putInt("InicioAut", 1);
                    editor.commit();
                } else {
                    SharedPreferences inicioaut = getSharedPreferences(INICIO_AUTOMATICO,0);
                    SharedPreferences.Editor editor = inicioaut.edit();
                    editor.putInt("InicioAut", 0);
                    editor.commit();
                }
            }
        });

        rdgVoz.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rdgVoz.getCheckedRadioButtonId() == R.id.rdbMasculina){
                    SharedPreferences voz = getSharedPreferences(VOZ, 0);
                    SharedPreferences.Editor editor = voz.edit();
                    editor.putInt("Voz", 0);
                    editor.commit();
                } else {
                    SharedPreferences voz = getSharedPreferences(VOZ, 0);
                    SharedPreferences.Editor editor = voz.edit();
                    editor.putInt("Voz", 1);
                    editor.commit();
                }
            }
        });

        swtBotaoFone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences botaofone = getSharedPreferences(BOTAOFONE,0);
                    SharedPreferences.Editor editor = botaofone.edit();
                    editor.putInt("BotaoFone", 1);
                    editor.commit();
                } else {
                    SharedPreferences botaofone = getSharedPreferences(BOTAOFONE,0);
                    SharedPreferences.Editor editor = botaofone.edit();
                    editor.putInt("BotaoFone", 0);
                    editor.commit();
                }
            }
        });

        rdgSons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rdgSons.getCheckedRadioButtonId() == R.id.rdbSomPadrao){
                    SharedPreferences sons = getSharedPreferences(SONS, 0);
                    SharedPreferences.Editor editor = sons.edit();
                    editor.putInt("Sons", 0);
                    editor.commit();
                } else {
                    SharedPreferences sons = getSharedPreferences(SONS, 0);
                    SharedPreferences.Editor editor = sons.edit();
                    editor.putInt("Sons", 1);
                    editor.commit();
                }
            }
        });

        SharedPreferences inicioaut = getSharedPreferences(INICIO_AUTOMATICO, Context.MODE_PRIVATE);
        if (inicioaut.getInt("InicioAut", 0) == 0) {
            swtInicioAut.setChecked(false);
        } else {
            swtInicioAut.setChecked(true);
        }

        SharedPreferences botaofone = getSharedPreferences(BOTAOFONE, Context.MODE_PRIVATE);
        if (botaofone.getInt("BotaoFone", 0) == 0) {
            swtBotaoFone.setChecked(false);
        } else {
            swtBotaoFone.setChecked(true);
        }

        SharedPreferences voz = getSharedPreferences(VOZ, Context.MODE_PRIVATE);
        if (voz.getInt("Voz", 0) == 0){
            rdbMasculina.setChecked(true);
            rdbFeminina.setChecked(false);
        } else {
            rdbMasculina.setChecked(false);
            rdbFeminina.setChecked(true);
        }

        SharedPreferences sons = getSharedPreferences(SONS, Context.MODE_PRIVATE);
        if (sons.getInt("Sons", 0) == 0){
            rdbPadrao.setChecked(true);
            rdbBambam.setChecked(false);
        } else {
            rdbPadrao.setChecked(false);
            rdbBambam.setChecked(true);
        }


    }
}
