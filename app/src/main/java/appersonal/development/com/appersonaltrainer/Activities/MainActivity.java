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

import com.facebook.FacebookDialog;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.R;

public class MainActivity extends AppCompatActivity {

    private Button btnTreinar;
    private Button btnMeusTreinos;
    private Button btnHistorico;
    private Button btnHistoricoCorrida;
    private Button btnAlarme;
    private Button btnConfig;
    private ImageView imgFacebook;
    private ImageView imgYoutube;
    private SQLiteDatabase bancoDados;



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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Implementa o ad na activity
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .build();
        adView.loadAd(adRequest);

        btnTreinar = (Button) findViewById(R.id.btnTreinar);
        btnMeusTreinos = (Button) findViewById(R.id.btnMeusTreinos);
        btnAlarme = (Button) findViewById(R.id.btnAlarme);
        btnHistorico = (Button) findViewById(R.id.btnHistorico);
        btnHistoricoCorrida = (Button) findViewById(R.id.btnHistoricoCorrida);
        btnConfig = (Button) findViewById(R.id.btnConfig);
        imgYoutube = (ImageView) findViewById(R.id.imgYoutube);
        imgFacebook = (ImageView) findViewById(R.id.imgFacebook);

        LikeView likeView = findViewById(R.id.likeView);
        likeView.setLikeViewStyle(LikeView.Style.STANDARD);
        likeView.setAuxiliaryViewPosition(LikeView.AuxiliaryViewPosition.INLINE);
        likeView.setObjectIdAndType(
                "http://www.facebook.com/appersonaltrainer1",
                LikeView.ObjectType.OPEN_GRAPH);

        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent link = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1926237564285290"));
                    startActivity(link);
                } catch (Exception e){
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
                startActivity(new Intent(MainActivity.this,TreinarActivity.class));
            }
        });

        btnMeusTreinos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MeusTreinosActivity.class));
            }
        });

        btnAlarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AlarmeActivity.class));
            }
        });

        btnHistoricoCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HistoricoCorridaActivity.class));
            }
        });

        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HistoricoTreinoActivity.class));
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ConfiguracoesActivity.class));
            }
        });

        try {
//            this.deleteDatabase("appersonal");
//            bancoDados.execSQL("DROP TABLE IF EXISTS exercicios");
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS treinos(idTreino INTEGER PRIMARY KEY AUTOINCREMENT, treino VARCHAR, data LONG)");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS historico(idHistorico INTEGER PRIMARY KEY AUTOINCREMENT, idTreino INT, treinoH VARCHAR, data LONG)");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS exercicios(idExercicio INTEGER PRIMARY KEY AUTOINCREMENT, idTreino INTEGER, " +
                    "exercicio VARCHAR, series INT(2), serieAtual INT(2), tipoRep INT(1)," +
                    "rep1 INT(2), rep2 INT(2), rep3 INT(2), rep4 INT(2), rep5 INT(2), rep6 INT(2), rep7 INT(2), rep8 INT(2), " +
                    "tempoExecucao INT(2), descansoM INT(1), descansoS INT(2), unilateral INT(1), musculoSpinner INT(2), exercicioSpinner INT(2), completo INT(1)," +
                    "FOREIGN KEY(idTreino) REFERENCES treinos(idTreino))");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS aerobicos(idAerobico INTEGER PRIMARY KEY AUTOINCREMENT, idTreino INTEGER, " +
                    "aerobico VARCHAR, duracaoH INT(1), duracaoM INT(2), duracaoS INT(2), series INT(2), descansoM INT(1), descansoS INT(2), " +
                    "distancia INT(1), km DOUBLE(5), completo INT(1), "+
                    "FOREIGN KEY(idTreino) REFERENCES treinos(idTreino))");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS historicokm(idHistoricoKM INTEGER PRIMARY KEY AUTOINCREMENT, nomeCorrida VARCHAR, km VARCHAR, tempo VARCHAR, data LONG)");

            try{
                Cursor cursor = bancoDados.rawQuery("SELECT obs FROM exercicios", null);
            } catch(Exception e){
                e.printStackTrace();
                try{
                    bancoDados.execSQL("ALTER TABLE exercicios ADD COLUMN obs VARCHAR");
                } catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            try{
                Cursor cursor = bancoDados.rawQuery("SELECT obs FROM aerobicos", null);
            } catch(Exception e){
                e.printStackTrace();
                try{
                    bancoDados.execSQL("ALTER TABLE aerobicos ADD COLUMN obs VARCHAR");
                } catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            try{
                Cursor cursor = bancoDados.rawQuery("SELECT pos FROM exercicios", null);
            } catch(Exception e){
                e.printStackTrace();
                try{
                    bancoDados.execSQL("ALTER TABLE exercicios ADD COLUMN pos INTEGER");
                } catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            try{
                Cursor cursor = bancoDados.rawQuery("SELECT pos FROM aerobicos", null);
            } catch(Exception e){
                e.printStackTrace();
                try{
                    bancoDados.execSQL("ALTER TABLE aerobicos ADD COLUMN pos INTEGER");
                } catch (Exception ex){
                    ex.printStackTrace();
                }

            }

        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
