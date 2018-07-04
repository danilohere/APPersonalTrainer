package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.Controller.GaleriaAerobicoAdapter;
import appersonal.development.com.appersonaltrainer.Controller.GaleriaExerciciosAdapter;
import appersonal.development.com.appersonaltrainer.Controller.GaleriaTreinarAdapter;
import appersonal.development.com.appersonaltrainer.R;

public class TutorialActivity extends AppCompatActivity {

    private Button btnCriarExercicio;
    private Button btnCriarAerobico;
    private Button btnRealizarTreino;
    private ViewPager vpgGaleria;
    private ImageView imgDeslize;
    private boolean img = false;

    @Override
    public void onBackPressed() {
        if (vpgGaleria.getVisibility() == View.VISIBLE) {
            vpgGaleria.setVisibility(View.INVISIBLE);
            vpgGaleria.animate().alpha(0.0f).setDuration(250);
            btnCriarExercicio.setVisibility(View.VISIBLE);
            btnCriarAerobico.setVisibility(View.VISIBLE);
            btnRealizarTreino.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
//                .addTestDevice("D5C546361B50B5162E9BCD250E5EC1D2")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
//                .addTestDevice("D5C546361B50B5162E9BCD250E5EC1D2")
                .build();
        adView2.loadAd(adRequest2);

        btnCriarExercicio = findViewById(R.id.btnCriarExercicio);
        btnCriarAerobico = findViewById(R.id.btnCriarAerobico);
        btnRealizarTreino = findViewById(R.id.btnRealizarTreino);
        imgDeslize = findViewById(R.id.imgDeslize);
        vpgGaleria = findViewById(R.id.vpgGaleria);

        btnCriarExercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaExerciciosAdapter adapter = new GaleriaExerciciosAdapter(getApplicationContext());
                vpgGaleria.setAdapter(adapter);
                if (!img) {
                    imgDeslize.setVisibility(View.VISIBLE);
                    imgDeslize.animate().alpha(1.0f).setDuration(250);
                    img = true;
                }
                Handler h = new Handler();
                final Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        imgDeslize.animate().alpha(0.0f).setDuration(250);
                    }
                };
                h.postDelayed(run, 2500);
                vpgGaleria.setVisibility(View.VISIBLE);
                vpgGaleria.animate().alpha(1.0f).setDuration(250);
                visible();
            }
        });

        btnCriarAerobico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaAerobicoAdapter adapter = new GaleriaAerobicoAdapter(getApplicationContext());
                vpgGaleria.setAdapter(adapter);
                if (!img) {
                    imgDeslize.setVisibility(View.VISIBLE);
                    imgDeslize.animate().alpha(1.0f).setDuration(250);
                    img = true;
                }
                Handler h = new Handler();
                final Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        imgDeslize.animate().alpha(0.0f).setDuration(250);
                    }
                };
                h.postDelayed(run, 2500);
                vpgGaleria.setVisibility(View.VISIBLE);
                vpgGaleria.animate().alpha(1.0f).setDuration(250);
                visible();
            }
        });

        btnRealizarTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaleriaTreinarAdapter adapter = new GaleriaTreinarAdapter(getApplicationContext());
                vpgGaleria.setAdapter(adapter);
                if (!img) {
                    imgDeslize.setVisibility(View.VISIBLE);
                    imgDeslize.animate().alpha(1.0f).setDuration(250);
                    img = true;
                }
                Handler h = new Handler();
                final Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        imgDeslize.animate().alpha(0.0f).setDuration(250);
                    }
                };
                h.postDelayed(run, 2500);
                vpgGaleria.setVisibility(View.VISIBLE);
                vpgGaleria.animate().alpha(1.0f).setDuration(250);
                visible();
            }
        });
    }

    void visible() {
        btnCriarExercicio.setVisibility(View.INVISIBLE);
        btnCriarAerobico.setVisibility(View.INVISIBLE);
        btnRealizarTreino.setVisibility(View.INVISIBLE);
    }
}



