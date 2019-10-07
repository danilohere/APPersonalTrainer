package appersonal.development.com.appersonaltrainer.activities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.R;


public class ConfiguracoesActivity extends AppCompatActivity {

    private RadioGroup rdgVoz;
    private RadioGroup rdgSons;

    private static final String INICIO_AUTOMATICO = "InicioAut";
    private static final String VOZ = "Voz";
    private static final String SONS = "Sons";
    private static final String BOTAOFONE = "BotaoFone";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .addTestDevice("6993B0696AE064D27BBDC28B90575368")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .addTestDevice("6993B0696AE064D27BBDC28B90575368")
                .build();
        adView2.loadAd(adRequest2);

        Switch swtInicioAut = findViewById(R.id.swtInicioAut);
        rdgVoz = findViewById(R.id.rdgvoz);
        RadioButton rdbMasculina = findViewById(R.id.rdbMasculina);
        RadioButton rdbFeminina = findViewById(R.id.rdbFeminina);
        Switch swtBotaoFone = findViewById(R.id.swtBotaoFone);
        rdgSons = findViewById(R.id.rdgSons);
        RadioButton rdbPadrao = findViewById(R.id.rdbSomPadrao);
        RadioButton rdbBambam = findViewById(R.id.rdbSomBambam);


        swtInicioAut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences inicioaut = getSharedPreferences(INICIO_AUTOMATICO, 0);
                    SharedPreferences.Editor editor = inicioaut.edit();
                    editor.putInt("InicioAut", 1);
                    editor.apply();
                } else {
                    SharedPreferences inicioaut = getSharedPreferences(INICIO_AUTOMATICO, 0);
                    SharedPreferences.Editor editor = inicioaut.edit();
                    editor.putInt("InicioAut", 0);
                    editor.apply();
                }
            }
        });

        rdgVoz.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rdgVoz.getCheckedRadioButtonId() == R.id.rdbMasculina) {
                    SharedPreferences voz = getSharedPreferences(VOZ, 0);
                    SharedPreferences.Editor editor = voz.edit();
                    editor.putInt("Voz", 0);
                    editor.apply();
                } else {
                    SharedPreferences voz = getSharedPreferences(VOZ, 0);
                    SharedPreferences.Editor editor = voz.edit();
                    editor.putInt("Voz", 1);
                    editor.apply();
                }
            }
        });

        swtBotaoFone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences botaofone = getSharedPreferences(BOTAOFONE, 0);
                    SharedPreferences.Editor editor = botaofone.edit();
                    editor.putInt("BotaoFone", 1);
                    editor.apply();
                } else {
                    SharedPreferences botaofone = getSharedPreferences(BOTAOFONE, 0);
                    SharedPreferences.Editor editor = botaofone.edit();
                    editor.putInt("BotaoFone", 0);
                    editor.apply();
                }
            }
        });

        rdgSons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rdgSons.getCheckedRadioButtonId() == R.id.rdbSomPadrao) {
                    SharedPreferences sons = getSharedPreferences(SONS, 0);
                    SharedPreferences.Editor editor = sons.edit();
                    editor.putInt("Sons", 0);
                    editor.apply();
                } else {
                    SharedPreferences sons = getSharedPreferences(SONS, 0);
                    SharedPreferences.Editor editor = sons.edit();
                    editor.putInt("Sons", 1);
                    editor.apply();
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
        if (voz.getInt("Voz", 0) == 0) {
            rdbMasculina.setChecked(true);
            rdbFeminina.setChecked(false);
        } else {
            rdbMasculina.setChecked(false);
            rdbFeminina.setChecked(true);
        }

        SharedPreferences sons = getSharedPreferences(SONS, Context.MODE_PRIVATE);
        if (sons.getInt("Sons", 0) == 0) {
            rdbPadrao.setChecked(true);
            rdbBambam.setChecked(false);
        } else {
            rdbPadrao.setChecked(false);
            rdbBambam.setChecked(true);
        }


    }
}
