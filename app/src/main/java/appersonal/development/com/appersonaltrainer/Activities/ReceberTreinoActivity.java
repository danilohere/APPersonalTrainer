package appersonal.development.com.appersonaltrainer.Activities;

import android.bluetooth.BluetoothAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import appersonal.development.com.appersonaltrainer.Controller.ConnectionThread;
import appersonal.development.com.appersonaltrainer.R;

public class ReceberTreinoActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean BtAtivo;
    ConnectionThread connect;
    static TextView txtTreino;
    private Handler h = new Handler();
    private int idTreino;
    static String Msg;
    static ArrayList<String> treinos;
    static Button btnSalvar;
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
    public void onBackPressed() {
        h.removeCallbacks(aviso);
        h.removeCallbacks(fechar);
        connect.cancel();
        btAdapter.disable();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receber_treino);

        //Implementa o botão voltar na ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Implementa o ad na activity
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .build();
        adView2.loadAd(adRequest2);

        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        treinos = new ArrayList<String>();
        txtTreino = (TextView) findViewById(R.id.txtTreino);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setEnabled(true);
        connect = new ConnectionThread();
        connect.start();
        h.postDelayed(aviso,25000);


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bancoDados.execSQL("INSERT INTO treinos (treino) VALUES ('" + treinos.get(0).toString() + "')");
                    Cursor cursor = bancoDados.rawQuery("SELECT * FROM treinos", null);
                    int indId = cursor.getColumnIndex("idTreino");
                    cursor.moveToLast();
                    idTreino = cursor.getInt(indId);
                    int c = Integer.parseInt(treinos.get(1).toString());
                    for (int i = 0; i < c; i++) {
                        if (treinos.get(i + 2).contains("INSERT INTO exercicios")) {
                            bancoDados.execSQL(treinos.get(i + 2) + ", " + idTreino + ", 0, 1)");
                        } else {
                            bancoDados.execSQL(treinos.get(i + 2) + ", " + idTreino + ")");
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ReceberTreinoActivity.this, "Erro ao receber treino, tente novamente", Toast.LENGTH_SHORT).show();
                    bancoDados.execSQL("DELETE FROM exercicios WHERE idTreino =" + idTreino);
                    bancoDados.execSQL("DELETE FROM treinos WHERE idTreino =" + idTreino);
                }
                h.removeCallbacks(aviso);
                h.removeCallbacks(fechar);
                connect.cancel();
                btAdapter.disable();
                finish();

            }
        });
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] treino = bundle.getByteArray("treino");
            Msg = new String(treino);
            treinos.add(Msg);
            txtTreino.setText("Treino "+treinos.get(0).toString() + " recebido");
            btnSalvar.setVisibility(View.VISIBLE);
        }
    };

    Runnable aviso = new Runnable() {
        @Override
        public void run() {
            if (btnSalvar.getVisibility()==View.INVISIBLE) {
                Toast.makeText(ReceberTreinoActivity.this, "Você voltará para a tela anterior em 5 segundos", Toast.LENGTH_LONG).show();
                h.postDelayed(fechar, 5000);
            }
        }
    };

    Runnable fechar = new Runnable() {
        @Override
        public void run() {
            if (btnSalvar.getVisibility() == View.INVISIBLE) {
                btAdapter.disable();
                finish();
                connect.cancel();
            }
        }
    };



}
