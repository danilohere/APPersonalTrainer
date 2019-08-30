package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.Model.Exercicios;
import appersonal.development.com.appersonaltrainer.R;

public class TreinoXActivity extends AppCompatActivity {

    private ListView lstExercicios;
    private TextView txtTreino;
    private TextView txtTempoMedio;
    private AlertDialog alerta;
    private SQLiteDatabase bancoDados;
    private InterstitialAd mInterstitialAd;
    ShareDialog shareDialog = new ShareDialog(this);
    private ArrayList<Integer> idsEx;
    private ArrayList<Integer> idsAer;
    private int idTreino;
    private int numIdEx;
    private boolean semTempo = false;
    private boolean completo = false;
    private String nomeTreino;
    private String maisAerobico = "";
    private static final String DATA = "Data";

    @Override
    protected void onResume() {
        recuperarExercicios();
        super.onResume();
    }

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
        setContentView(R.layout.activity_treino_x);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Implementa o ad na activity
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView.loadAd(adRequest);
        MobileAds.initialize(this,
                "ca-app-pub-4960619699535760/4978018539");
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-4960619699535760/4978018539");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            idTreino = extra.getInt("idTreino");
        }

        lstExercicios = findViewById(R.id.lstExercicios);
        txtTreino = findViewById(R.id.txtTreino);
        txtTempoMedio = findViewById(R.id.txtTempoMedio);
        Button btnFinalizar = findViewById(R.id.btnFinalizar);

        lstExercicios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent exercicio = new Intent(TreinoXActivity.this, ExercicioActivity.class);
                Intent aerobico = new Intent(TreinoXActivity.this, AerobicoActivity.class);
                if (position < numIdEx) {
                    exercicio.putExtra("idExercicio", idsEx.get(position));
                    exercicio.putExtra("idTreino", idTreino);
                    startActivity(exercicio);
                } else {
                    aerobico.putExtra("idAerobico", idsAer.get(position - numIdEx));
                    aerobico.putExtra("idTreino", idTreino);
                    startActivity(aerobico);
                }
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!completo) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TreinoXActivity.this);
                    builder.setTitle("Treino Incompleto");
                    builder.setMessage("Deseja cancelar o treino?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bancoDados.execSQL("UPDATE exercicios SET completo = 0, serieAtual = 1 WHERE idTreino = " + idTreino);
                            bancoDados.execSQL("UPDATE aerobicos SET completo = 0 WHERE idTreino = " + idTreino);
                            TreinoXActivity.this.finish();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdFailedToLoad(int i) {
                                    Toast.makeText(getApplicationContext(), "Treino cancelado", Toast.LENGTH_SHORT).show();
                                    super.onAdFailedToLoad(i);
                                }

                                @Override
                                public void onAdClosed() {
                                    Toast.makeText(getApplicationContext(), "Treino cancelado", Toast.LENGTH_SHORT).show();
                                    super.onAdClosed();
                                }

                            });
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Treino cancelado", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TreinoXActivity.this);
                    builder.setTitle("Compartilhar Treino no Facebook");
                    builder.setMessage("Deseja compartilhar no Facebook que acabou de concluir seu treino?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ShareLinkContent content = new ShareLinkContent.Builder()
                                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=appersonal.development.com.appersonaltrainer"))
                                    .setQuote("Acabei de fazer " + nomeTreino + " com o APPersonal Trainer!")
                                    .build();
                            shareDialog.show(content);
                            SharedPreferences dataTempo = getSharedPreferences(DATA, Context.MODE_PRIVATE);
                            long data = dataTempo.getLong("Data", 0);
                            bancoDados.execSQL("UPDATE exercicios SET completo = 0, serieAtual = 1 WHERE idTreino =" + idTreino);
                            bancoDados.execSQL("UPDATE aerobicos SET completo = 0 WHERE idTreino =" + idTreino);
                            bancoDados.execSQL("UPDATE treinos SET data = '" + data + "' WHERE idTreino =" + idTreino);
                            bancoDados.execSQL("INSERT INTO historico (treinoH, idTreino, data) VALUES ('" + nomeTreino + "', " + idTreino + ", " + data + ")");
                            Toast.makeText(getApplicationContext(), "Treino finalizado!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences dataTempo = getSharedPreferences(DATA, Context.MODE_PRIVATE);
                            long data = dataTempo.getLong("Data", 0);
                            bancoDados.execSQL("UPDATE exercicios SET completo = 0, serieAtual = 1 WHERE idTreino =" + idTreino);
                            bancoDados.execSQL("UPDATE aerobicos SET completo = 0 WHERE idTreino =" + idTreino);
                            bancoDados.execSQL("UPDATE treinos SET data = '" + data + "' WHERE idTreino =" + idTreino);
                            bancoDados.execSQL("INSERT INTO historico (treinoH, idTreino, data) VALUES ('" + nomeTreino + "', " + idTreino + ", " + data + ")");
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    Toast.makeText(getApplicationContext(), "Treino finalizado!", Toast.LENGTH_SHORT).show();
                                    super.onAdClosed();
                                }

                                @Override
                                public void onAdFailedToLoad(int i) {
                                    Toast.makeText(getApplicationContext(), "Treino finalizado!", Toast.LENGTH_SHORT).show();
                                    super.onAdFailedToLoad(i);
                                }
                            });

                            onBackPressed();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Treino finalizado!", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }
                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                }


            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void recuperarExercicios() {
        int tempoMedio = 0;
        numIdEx = 0;
        completo = true;
        ArrayList<Exercicios> exercicios = new ArrayList<>();
        idsEx = new ArrayList<>();
        idsAer = new ArrayList<>();
        AdapterExerciciosPersonalizado adaptador = new AdapterExerciciosPersonalizado(exercicios, this);

        try {
            Cursor cursorTreino = bancoDados.rawQuery("SELECT treino FROM treinos WHERE idTreino =" + idTreino, null);
            int indTreino = cursorTreino.getColumnIndex("treino");
            cursorTreino.moveToFirst();
            txtTreino.setText(cursorTreino.getString(indTreino));
            nomeTreino = txtTreino.getText().toString();
            cursorTreino.close();

            Cursor cursorExercicio = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);
            int indExercicio = cursorExercicio.getColumnIndex("exercicio");
            int indIdEx = cursorExercicio.getColumnIndex("idExercicio");
            int indCompleto = cursorExercicio.getColumnIndex("completo");
            int indTempo = cursorExercicio.getColumnIndex("tempoMedio");

            lstExercicios.setAdapter(adaptador);
            while (cursorExercicio.moveToNext()) {
                int tempo = cursorExercicio.getInt(indTempo);
                if (tempo == 0) {
                    semTempo = true;
                } else {
                    tempoMedio += cursorExercicio.getInt(indTempo) + 150;
                }
                Exercicios exerc = new Exercicios();
                exerc.setNome(cursorExercicio.getString(indExercicio));
                if (cursorExercicio.getInt(indCompleto) == 1) {
                    exerc.setCompleto("sim");
                } else {
                    exerc.setCompleto("não");
                    completo = false;
                }
                exercicios.add(exerc);
                idsEx.add(Integer.parseInt(cursorExercicio.getString(indIdEx)));
                numIdEx++;
            }
            cursorExercicio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Cursor cursorAerobico = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);
            int indAerobico = cursorAerobico.getColumnIndex("aerobico");
            int indIdAer = cursorAerobico.getColumnIndex("idAerobico");
            int indCompleto = cursorAerobico.getColumnIndex("completo");
            int indTempo = cursorAerobico.getColumnIndex("tempoMedio");
            lstExercicios.setAdapter(adaptador);
            while (cursorAerobico.moveToNext()) {
                if (cursorAerobico.getInt(indTempo) == 0) {
                    semTempo = true;
                } else if (cursorAerobico.getInt(indTempo) == 99999999) {
                    maisAerobico = " + aeróbico";
                } else {
                    tempoMedio += cursorAerobico.getInt(indTempo) + 150;
                }
                Exercicios exerc = new Exercicios();
                exerc.setNome(cursorAerobico.getString(indAerobico));
                if (cursorAerobico.getInt(indCompleto) == 1) {
                    exerc.setCompleto("sim");
                } else {
                    exerc.setCompleto("não");
                    completo = false;
                }
                exercicios.add(exerc);
                idsAer.add(Integer.parseInt(cursorAerobico.getString(indIdAer)));
            }
            cursorAerobico.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (semTempo) {
            txtTempoMedio.setText("Tempo médio não definido");
            Toast.makeText(this, "Por favor, abrir e salvar os exercícios desse treino em Editar treinos para gravar o tempo médio", Toast.LENGTH_LONG).show();
        } else {
            int h = tempoMedio / 3600;
            int m = (tempoMedio - (h * 3600)) / 60;
            if (m > 9) {
                if (m >= 60) {
                    txtTempoMedio.setText("Tempo médio de treino: " + h + ":00" + maisAerobico);
                } else {
                    txtTempoMedio.setText("Tempo médio de treino: " + h + ":" + m + maisAerobico);
                }
            } else {
                txtTempoMedio.setText("Tempo médio de treino: " + h + ":0" + m + maisAerobico);
            }
        }
    }

    public class AdapterExerciciosPersonalizado extends BaseAdapter {

        private final List<Exercicios> exercicios;
        private final Activity activity;

        AdapterExerciciosPersonalizado(List<Exercicios> exercicios, Activity activity) {
            this.exercicios = exercicios;
            this.activity = activity;
        }


        @Override
        public int getCount() {
            return exercicios.size();
        }

        @Override
        public Object getItem(int position) {
            return exercicios.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_exercicios, parent, false);

            Exercicios exercicio = exercicios.get(position);

            TextView txtNomeExer =
                    view.findViewById(R.id.txtNome);
            ImageView imgCompleto =
                    view.findViewById(R.id.imgCompleto);

            txtNomeExer.setText(exercicio.getNome());
            if (exercicio.getCompleto().equals("sim")) {
                imgCompleto.setImageResource(R.drawable.completo);
            }
            return view;
        }
    }
}
