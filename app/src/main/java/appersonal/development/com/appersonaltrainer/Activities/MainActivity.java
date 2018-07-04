package appersonal.development.com.appersonaltrainer.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.share.widget.LikeView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.R;

public class MainActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_main);

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

        Button btnTreinar = findViewById(R.id.btnTreinar);
        Button btnMeusTreinos = findViewById(R.id.btnMeusTreinos);
        Button btnAlarme = findViewById(R.id.btnAlarme);
        Button btnHistorico = findViewById(R.id.btnHistorico);
        Button btnHistoricoCorrida = findViewById(R.id.btnHistoricoCorrida);
        Button btnTutorial = findViewById(R.id.btnTutorial);
        Button btnConfig = findViewById(R.id.btnConfig);
        ImageView imgYoutube = findViewById(R.id.imgYoutube);
        ImageView imgFacebook = findViewById(R.id.imgFacebook);

        @SuppressWarnings("deprecation") LikeView likeView = findViewById(R.id.likeView);
        //noinspection deprecation
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        //noinspection deprecation
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        //noinspection deprecation
        likeView.setObjectIdAndType(
                "http://www.facebook.com/appersonaltrainer1",
                LikeView.ObjectType.PAGE);

        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1926237564285290"));
                    startActivity(link);
                } catch (Exception e) {
                    Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/appersonaltrainer1"));
                    startActivity(link);
                }
            }
        });

        imgYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCQPRmQldP1OL1Qm72cTvx8A"));
                startActivity(link);
            }
        });

        btnTreinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TreinarActivity.class));
            }
        });

        btnMeusTreinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MeusTreinosActivity.class));
            }
        });

        btnAlarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AlarmeActivity.class));
            }
        });

        btnHistoricoCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoricoCorridaActivity.class));
            }
        });

        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HistoricoTreinoActivity.class));
            }
        });
        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
            }
        });

        try {
//            this.deleteDatabase("appersonal");
//            bancoDados.execSQL("DROP TABLE IF EXISTS exercicios");
            SQLiteDatabase bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS treinos(idTreino INTEGER PRIMARY KEY AUTOINCREMENT, treino VARCHAR, data LONG)");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS historico(idHistorico INTEGER PRIMARY KEY AUTOINCREMENT, idTreino INT, treinoH VARCHAR, data LONG)");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS exercicios(idExercicio INTEGER PRIMARY KEY AUTOINCREMENT, idTreino INTEGER, " +
                    "exercicio VARCHAR, series INT(2), serieAtual INT(2), tipoRep INT(1)," +
                    "rep1 INT(2), rep2 INT(2), rep3 INT(2), rep4 INT(2), rep5 INT(2), rep6 INT(2), rep7 INT(2), rep8 INT(2), " +
                    "tempoExecucao INT(2), descansoM INT(1), descansoS INT(2), unilateral INT(1), musculoSpinner INT(2), exercicioSpinner INT(2), completo INT(1)," +
                    "obs VARCHAR, pos INTEGER, tempoMedio INTEGER, " +
                    "FOREIGN KEY(idTreino) REFERENCES treinos(idTreino))");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS aerobicos(idAerobico INTEGER PRIMARY KEY AUTOINCREMENT, idTreino INTEGER, " +
                    "aerobico VARCHAR, duracaoH INT(1), duracaoM INT(2), duracaoS INT(2), series INT(2), descansoM INT(1), descansoS INT(2), " +
                    "distancia INT(1), km DOUBLE(5), completo INT(1), " +
                    "obs VARCHAR, pos INTEGER, tempoMedio INTEGER, " +
                    "FOREIGN KEY(idTreino) REFERENCES treinos(idTreino))");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS historicokm(idHistoricoKM INTEGER PRIMARY KEY AUTOINCREMENT, nomeCorrida VARCHAR, km VARCHAR, tempo VARCHAR, data LONG)");

            try {
                bancoDados.rawQuery("SELECT tempoMedio FROM aerobicos", null);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    bancoDados.execSQL("ALTER TABLE aerobicos ADD COLUMN tempoMedio INTEGER");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            try {
                bancoDados.rawQuery("SELECT tempoMedio FROM exercicios", null);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    bancoDados.execSQL("ALTER TABLE exercicios ADD COLUMN tempoMedio INTEGER");
                } catch (Exception ex) {
                    Toast.makeText(this, "Teste", Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
