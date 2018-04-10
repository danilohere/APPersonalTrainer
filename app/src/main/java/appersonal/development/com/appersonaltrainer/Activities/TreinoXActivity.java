package appersonal.development.com.appersonaltrainer.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
    private String nomeTreino;
    private ArrayList<Exercicios> exercicios;
    private ArrayList<Integer> idsEx;
    private ArrayList<Integer> idsAer;
    private int idTreino;
    private int numIdEx;
    private SQLiteDatabase bancoDados;
    private Bundle extra;
    private TextView txtTreino;
    private Button btnFinalizar;
    private AlertDialog alerta;
    private boolean completo=false;
    private InterstitialAd mInterstitialAd;
    ShareDialog shareDialog = new ShareDialog(this);


    @Override
    protected void onResume() {
        recuperarExercicios();
        super.onResume();
    }

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
        setContentView(R.layout.activity_treino_x);

        //Implementa o botão voltar na ActionBar
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

        try{
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        extra = getIntent().getExtras();
        if (extra!=null){
            idTreino = extra.getInt("idTreino");
        }

        lstExercicios = findViewById(R.id.lstExercicios);
        txtTreino = findViewById(R.id.txtTreino);
        btnFinalizar = findViewById(R.id.btnFinalizar);

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
                            Toast.makeText(getApplicationContext(), "Treino cancelado", Toast.LENGTH_SHORT).show();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
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
                                    .setQuote("Acabei de fazer "+nomeTreino+" com o APPersonal Trainer!")
                                    .build();
                            shareDialog.show(content);
                            long data = System.currentTimeMillis();
                            bancoDados.execSQL("UPDATE exercicios SET completo = 0, serieAtual = 1 WHERE idTreino ="+ idTreino);
                            bancoDados.execSQL("UPDATE aerobicos SET completo = 0 WHERE idTreino ="+ idTreino);
                            bancoDados.execSQL("UPDATE treinos SET data = '"+ data +"' WHERE idTreino ="+ idTreino);
                            bancoDados.execSQL("INSERT INTO historico (treinoH, idTreino, data) VALUES ('"+ nomeTreino +"', "+ idTreino +", "+data+")");
                            Toast.makeText(getApplicationContext(), "Treino finalizado!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            long data = System.currentTimeMillis();
                            bancoDados.execSQL("UPDATE exercicios SET completo = 0, serieAtual = 1 WHERE idTreino ="+ idTreino);
                            bancoDados.execSQL("UPDATE aerobicos SET completo = 0 WHERE idTreino ="+ idTreino);
                            bancoDados.execSQL("UPDATE treinos SET data = '"+ data +"' WHERE idTreino ="+ idTreino);
                            bancoDados.execSQL("INSERT INTO historico (treinoH, idTreino, data) VALUES ('"+ nomeTreino +"', "+ idTreino +", "+data+")");
                            Toast.makeText(getApplicationContext(), "Treino finalizado!", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
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

    private void recuperarExercicios() {
        numIdEx=0;
        completo = true;
        exercicios = new ArrayList<>();
        idsEx = new ArrayList<>();
        idsAer = new ArrayList<>();
        AdapterExerciciosPersonalizado adaptador = new AdapterExerciciosPersonalizado(exercicios, this);

        try {
            Cursor cursorTreino = bancoDados.rawQuery("SELECT treino FROM treinos WHERE idTreino =" + idTreino, null);
            int indTreino = cursorTreino.getColumnIndex("treino");
            cursorTreino.moveToFirst();
            txtTreino.setText(cursorTreino.getString(indTreino));
            nomeTreino = ""+txtTreino.getText();
            Cursor cursorExercicio = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);

            int indExercicio = cursorExercicio.getColumnIndex("exercicio");
            int indIdEx = cursorExercicio.getColumnIndex("idExercicio");
            int indCompleto = cursorExercicio.getColumnIndex("completo");
            lstExercicios.setAdapter(adaptador);
            cursorExercicio.moveToFirst();
            while (cursorExercicio != null) {
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
                cursorExercicio.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Cursor cursorTreino = bancoDados.rawQuery("SELECT treino FROM treinos WHERE idTreino =" + idTreino, null);
            int indTreino = cursorTreino.getColumnIndex("treino");
            cursorTreino.moveToFirst();
            txtTreino.setText(cursorTreino.getString(indTreino));
            Cursor cursorAerobico = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);


            int indAerobico = cursorAerobico.getColumnIndex("aerobico");
            int indIdAer = cursorAerobico.getColumnIndex("idAerobico");
            int indCompleto = cursorAerobico.getColumnIndex("completo");
            lstExercicios.setAdapter(adaptador);
            cursorAerobico.moveToFirst();
            while (cursorAerobico != null) {
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
                cursorAerobico.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class AdapterExerciciosPersonalizado extends BaseAdapter {

        private final List<Exercicios> exercicios;
        private final Activity activity;

        public AdapterExerciciosPersonalizado(List<Exercicios> exercicios, Activity activity) {
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
            View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_exercicios, parent, false);

            Exercicios exercicio = exercicios.get(position);

            TextView txtNomeExer = (TextView)
                    view.findViewById(R.id.txtNome);
            ImageView imgCompleto = (ImageView)
                    view.findViewById(R.id.imgCompleto);

            txtNomeExer.setText(exercicio.getNome());
            if (exercicio.getCompleto().equals("sim")){
                imgCompleto.setImageResource(R.drawable.completo);
            }
            return view;
        }
    }
}
