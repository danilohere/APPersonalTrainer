package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.Controller.Compress;
import appersonal.development.com.appersonaltrainer.Controller.ConnectionThread;
import appersonal.development.com.appersonaltrainer.R;

public class MeusTreinosActivity extends AppCompatActivity {

    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int DISCOVERABLE_BLUETOOTH = 3;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean BtAtivo;
    private ListView lstTreinos;
    private RelativeLayout lytCompartilhar;
    private ImageView imgQR;
    private ArrayList<String> treinos;
    private ArrayList<Integer> ids;
    private int id;
    private boolean enviadoN;
    private boolean enviadoC;
    private String nomeTreino;
    private SQLiteDatabase bancoDados;
    private AlertDialog alerta;
    private int lstposition;
    private Handler h = new Handler();
    private int tentativa;
    private boolean StopRun1;
    private boolean StopRun2;
    private boolean desligaBt = false;
    @SuppressLint("StaticFieldLeak")
    static TextView txtTeste;
    ConnectionThread connect;

    private Cursor cursorE;
    private Cursor cursorA;
    private int indExercicio;
    private int indSeries;
    private int indTipoRep;
    private int indRep1;
    private int indRep2;
    private int indRep3;
    private int indRep4;
    private int indRep5;
    private int indRep6;
    private int indRep7;
    private int indRep8;
    private int indTempoExecucao;
    private int indDescansoM;
    private int indDescansoS;
    private int indUnilateral;
    private int indMusculoSpinner;
    private int indExercicioSpinner;
    private int indObs;
    private int indTempoMedio;

    private int indAerobico;
    private int indDuracaoH;
    private int indDuracaoM;
    private int indDuracaoS;
    private int indASeries;
    private int indADescansoM;
    private int indADescansoS;
    private int indDistancia;
    private int indKM;
    private int indATempoMedio;

    private AlertDialog.Builder builder;
    private String nomeDisp;

    private byte[] treino;
    private byte[] count;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem compartilhar = menu.add("Compartilhar");
        final MenuItem excluir = menu.add("Excluir treino");

        compartilhar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                lytCompartilhar.startAnimation(anim);
                lytCompartilhar.setVisibility(View.VISIBLE);
                final Runnable r = new Runnable() {
                    public void run() {
                        gerarLink(lstposition);
                    }
                };
                handler.postDelayed(r, 250);

                return true;
            }
        });
        excluir.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                excluirTreino(lstposition);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (lytCompartilhar.getVisibility() == View.VISIBLE){
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            lytCompartilhar.startAnimation(anim);
            lytCompartilhar.setVisibility(View.INVISIBLE);
            final Runnable r = new Runnable() {
                public void run() {
//                    Glide.with(MeusTreinosActivity.this)
//                            .load(R.drawable.qrcodeg) // aqui é teu gif
//                            .into(imgQR);
                    imgQR.setImageDrawable(getResources().getDrawable(R.drawable.qrcodeg));
                }
            };
            handler.postDelayed(r, 250);
        } else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_treinos);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Implementa o ad na activity
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView2.loadAd(adRequest2);

        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lstTreinos = findViewById(R.id.lstTreinos);
        registerForContextMenu(lstTreinos);
        Button btnNovo = findViewById(R.id.btnNovo);
        Button btnCopiar = findViewById(R.id.btnCopiar);
        lytCompartilhar = findViewById(R.id.lytCompartilhar);
//        Button btnBluetooh = findViewById(R.id.btnBluetooh);
        Button btnFechar = findViewById(R.id.btnFechar);
        imgQR = findViewById(R.id.imgQR);
//        Glide.with(this)
//                .load(R.drawable.qrcodeg) // aqui é teu gif
//                .into(imgQR);
        recuperarTreinos();
        lstTreinos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MeusTreinosActivity.this, EditarTreinoActivity.class);
                intent.putExtra("idTreino", ids.get(position));
                startActivity(intent);
            }
        });
        lstTreinos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                lstposition = position;
                nomeTreino = lstTreinos.getItemAtPosition(lstposition).toString();
                registerForContextMenu(parent);
                openContextMenu(parent);
                return true;
            }
        });
        btnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeusTreinosActivity.this, EditarTreinoActivity.class);
                intent.putExtra("idTreino", 500000);
                startActivity(intent);
            }
        });

//        btnBluetooh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ativarBT();
//            }
//        });

        btnCopiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MeusTreinosActivity.this);
//                builder.setTitle("Copiar treino");
//                builder.setMessage("Como deseja copiar o treino?");
//                builder.setNeutralButton("Escanear QRCode", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MeusTreinosActivity.this, "Escaneie o QRCodeActivity de outro dispositivo", Toast.LENGTH_SHORT).show();
                        IntentIntegrator integrator = new IntentIntegrator(MeusTreinosActivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setOrientationLocked(true);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.initiateScan();
//                    }
//                });
//                builder.setPositiveButton("Bluetooth", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
//                        startActivityForResult(discoverableIntent, DISCOVERABLE_BLUETOOTH);
//                    }
//                });
//                alerta = builder.create();
//                alerta.show();
            }
        });
        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recuperarTreinos();
    }

    private void recuperarTreinos() {
        Cursor cursor = bancoDados.rawQuery("SELECT * FROM treinos ORDER BY idTreino ASC", null);
        int indTreino = cursor.getColumnIndex("treino");
        int indId = cursor.getColumnIndex("idTreino");
        try {
            treinos = new ArrayList<>();
            ids = new ArrayList<>();
            lstTreinos.setAdapter(new MyAdapter(treinos));
            while (cursor.moveToNext()) {
                treinos.add(cursor.getString(indTreino));
                ids.add(Integer.parseInt(cursor.getString(indId)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private void excluirTreino(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MeusTreinosActivity.this);
        builder.setTitle("Excluir Treino");
        builder.setMessage("Deseja excluir " + treinos.get(position) + "?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bancoDados.execSQL("DELETE FROM exercicios WHERE idTreino =" + ids.get(position));
                bancoDados.execSQL("DELETE FROM treinos WHERE idTreino =" + ids.get(position));
                recuperarTreinos();
                Toast.makeText(getApplicationContext(), "Treino excluído", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alerta = builder.create();
        alerta.show();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                searchPairedDevices();
            } else {
                Toast.makeText(this, "Bluetooth não ativado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SELECT_PAIRED_DEVICE) {
            if (resultCode == RESULT_OK) {
                nomeDisp = data.getStringExtra("btDevName");
                if (PairedDevicesActivity.connect != null) {
                    if (PairedDevicesActivity.connect.running) {
                        connect = PairedDevicesActivity.connect;
                    } else {
                        connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                    }
                } else {
                    connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                }
                if (!connect.running) {
                    connect.start();
                }
                id = ids.get(lstposition);

                Toast.makeText(this, "Aguardando conexão...", Toast.LENGTH_LONG).show();

                final Runnable rEspera = new Runnable() {
                    @Override
                    public void run() {
                        if (txtTeste.getText().equals("---S")) {
                            builder = new AlertDialog.Builder(MeusTreinosActivity.this);
                            builder.setTitle("Compartilhar Treino");
                            builder.setMessage("Deseja compartilhar " + treinos.get(lstposition) + " com " + nomeDisp + "?");
                            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    enviarTreino(id);
                                }
                            });

                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        } else {
                            if (tentativa >= 3) {
                                Toast.makeText(MeusTreinosActivity.this, "Dispositivo não encontrado", Toast.LENGTH_SHORT).show();
                                connect.cancel();
                                try {
                                    PairedDevicesActivity.connect.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (!BtAtivo) {
                                    btAdapter.disable();
                                }
                            } else {
                                tentativa++;
                                Toast.makeText(MeusTreinosActivity.this, "Aguardando conexão...", Toast.LENGTH_SHORT).show();
                                h.postDelayed(this, 2000);
                            }

                        }
                    }
                };

                h.postDelayed(rEspera, 3500);
//
            } else {
                Toast.makeText(this, "Nenhum dispositivo selecionado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == DISCOVERABLE_BLUETOOTH) {
            int duration = 30;
            if (resultCode == duration) {
                Intent intent = new Intent(this, ReceberTreinoActivity.class);
                BtAtivo = btAdapter.isEnabled();
                intent.putExtra("BtAtivo", BtAtivo);
                startActivity(intent);
            }
        } else if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");
                if (result.getContents().contains("APPersonal Trainer QRCode")){
                    String mensagem = result.getContents().replace("APPersonal Trainer QRCode", "");
                    mensagem = Compress.descompacta(mensagem);
                    String[] sql = mensagem.split("@");
                    bancoDados.execSQL(sql[1]);
                    Cursor cursor = bancoDados.rawQuery("SELECT * FROM treinos", null);
                    int indId = cursor.getColumnIndex("idTreino");
                    cursor.moveToLast();
                    int idTreino = cursor.getInt(indId);
                    cursor.close();
                    for (int i = 2; i < sql.length; i++) {
                        if (sql[i].contains("INSERT INTO exercicios")) {
                            bancoDados.execSQL(sql[i] + ", " + idTreino + ", 0, 1)");
                        } else {
                            bancoDados.execSQL(sql[i] + ", " + idTreino + ")");
                        }
                    }
                    Toast.makeText(this, "Treino copiado!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "QR Code inválido, verifique se o código é um treino.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    //INÍCIO TRANSFERIR QRCODE

    private void gerarLink(int position) {
        String mensagem = "APPersonal Trainer QRCode";
        String sql = "@INSERT INTO treinos (treino) VALUES ('" + nomeTreino + "')";

        try {
            cursorE = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + ids.get(position) + " ORDER BY pos ASC", null);
            cursorE.moveToFirst();
            int count = cursorE.getCount();
            for (int i=0; i <= count; i++){
                indExercicio = cursorE.getColumnIndex("exercicio");
                indSeries = cursorE.getColumnIndex("series");
                indTipoRep = cursorE.getColumnIndex("tipoRep");
                indRep1 = cursorE.getColumnIndex("rep1");
                indRep2 = cursorE.getColumnIndex("rep2");
                indRep3 = cursorE.getColumnIndex("rep3");
                indRep4 = cursorE.getColumnIndex("rep4");
                indRep5 = cursorE.getColumnIndex("rep5");
                indRep6 = cursorE.getColumnIndex("rep6");
                indRep7 = cursorE.getColumnIndex("rep7");
                indRep8 = cursorE.getColumnIndex("rep8");
                indTempoExecucao = cursorE.getColumnIndex("tempoExecucao");
                indDescansoM = cursorE.getColumnIndex("descansoM");
                indDescansoS = cursorE.getColumnIndex("descansoS");
                indUnilateral = cursorE.getColumnIndex("unilateral");
                indMusculoSpinner = cursorE.getColumnIndex("musculoSpinner");
                indExercicioSpinner = cursorE.getColumnIndex("exercicioSpinner");
                indObs = cursorE.getColumnIndex("obs");
                indTempoMedio = cursorE.getColumnIndex("tempoMedio");

                String Exercicio = cursorE.getString(indExercicio);
                String Series = cursorE.getString(indSeries);
                String TipoRep = cursorE.getString(indTipoRep);
                String Rep1 = cursorE.getString(indRep1);
                String Rep2 = cursorE.getString(indRep2);
                String Rep3 = cursorE.getString(indRep3);
                String Rep4 = cursorE.getString(indRep4);
                String Rep5 = cursorE.getString(indRep5);
                String Rep6 = cursorE.getString(indRep6);
                String Rep7 = cursorE.getString(indRep7);
                String Rep8 = cursorE.getString(indRep8);
                String TempoExecucao = cursorE.getString(indTempoExecucao);
                String DescansoM = cursorE.getString(indDescansoM);
                String DescansoS = cursorE.getString(indDescansoS);
                String Unilateral = cursorE.getString(indUnilateral);
                String MusculoSpinner = cursorE.getString(indMusculoSpinner);
                String ExercicioSpinner = cursorE.getString(indExercicioSpinner);
                String Obs = cursorE.getString(indObs);
                String tempoMedio = cursorE.getString(indTempoMedio);
                //noinspection StringConcatenationInLoop
                sql = sql + "@INSERT INTO exercicios (exercicio, series, tipoRep, rep1, rep2," +
                        "rep3, rep4, rep5, rep6, rep7, rep8, tempoExecucao, descansoM, descansoS, unilateral, musculoSpinner, exercicioSpinner, obs, tempoMedio, idTreino, completo, serieAtual) VALUES" +
                        "('" + Exercicio + "', " + Series + ", " + TipoRep + ", " + Rep1 + "" +
                        ", " + Rep2 + ", " + Rep3 + ", " + Rep4 + "" +
                        ", " + Rep5 + ", " + Rep6 + ", " + Rep7 + "" +
                        ", " + Rep8 + ", " + TempoExecucao + ", " + DescansoM + "" +
                        ", " + DescansoS + ", " + Unilateral +
                        ", " + MusculoSpinner + ", " + ExercicioSpinner + ", '" + Obs + "', " + tempoMedio;
                cursorE.moveToNext();
            }
            cursorE.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cursorA = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino = " + ids.get(position) + " ORDER BY pos ASC", null);
            cursorA.moveToFirst();
            int count = cursorA.getCount();
            for (int i = 0; i <= count; i++) {
                indAerobico = cursorA.getColumnIndex("aerobico");
                indDuracaoH = cursorA.getColumnIndex("duracaoH");
                indDuracaoM = cursorA.getColumnIndex("duracaoM");
                indDuracaoS = cursorA.getColumnIndex("duracaoS");
                indASeries = cursorA.getColumnIndex("series");
                indADescansoM = cursorA.getColumnIndex("descansoM");
                indADescansoS = cursorA.getColumnIndex("descansoS");
                indDistancia = cursorA.getColumnIndex("distancia");
                indATempoMedio = cursorA.getColumnIndex("tempoMedio");
                indKM = cursorA.getColumnIndex("km");

                String Aerobico = cursorA.getString(indAerobico);
                String DuracaoH = cursorA.getString(indDuracaoH);
                String DuracaoM = cursorA.getString(indDuracaoM);
                String DuracaoS = cursorA.getString(indDuracaoS);
                String Series = cursorA.getString(indASeries);
                String DescansoM = cursorA.getString(indADescansoM);
                String DescansoS = cursorA.getString(indADescansoS);
                String Distancia = cursorA.getString(indDistancia);
                String KM = cursorA.getString(indKM);
                String tempoMedio = cursorA.getString(indATempoMedio);

                //noinspection StringConcatenationInLoop
                sql = sql + "@INSERT INTO aerobicos (aerobico, duracaoH, duracaoM, duracaoS, series, " +
                        "descansoM, descansoS, distancia, km, tempoMedio, idTreino) VALUES " +
                        "('" + Aerobico + "', " + DuracaoH + ", " + DuracaoM + ", " + DuracaoS + ", "
                        + Series + ", " + DescansoM + ", " + DescansoS + ", " + Distancia + ", " + KM + ", " + tempoMedio;
                cursorA.moveToNext();
            }
            cursorA.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sql = Compress.compacta(sql);
        mensagem = mensagem + sql;


        try {
            bitmap = TextToImageEncode(mensagem);

            imgQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            //noinspection SuspiciousNameCombination
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    //FIM TRANSFERÊNCIA QRCODE

    //INICIO TRANSFERÊNCIA BLUETOOTH

    private void ativarBT() {

        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
            BtAtivo = false;
        } else {
            searchPairedDevices();
            BtAtivo = true;
        }
    }

    public void searchPairedDevices() {
        Intent searchPairedDevicesIntent = new Intent(this, PairedDevicesActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString = null;
            if (data != null) {
                dataString = new String(data);
            }
            if (dataString != null) {
                txtTeste.setText(dataString);
            }
        }
    };

    private void enviarTreino(int idTreino) {
        StopRun1 = false;
        StopRun2 = false;
        enviadoN = false;
        enviadoC = false;
        int c = 0;
        treino = nomeTreino.getBytes();

        try {
            Cursor cursorEX = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + idTreino, null);
            Cursor cursorAE = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino = " + idTreino, null);
            c = c + cursorEX.getCount();
            c = c + cursorAE.getCount();
            cursorEX.close();
            cursorAE.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String cString = "" + c;
        count = cString.getBytes();

        try {
            cursorE = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + idTreino + " ORDER BY pos ASC", null);
            indExercicio = cursorE.getColumnIndex("exercicio");
            indSeries = cursorE.getColumnIndex("series");
            indTipoRep = cursorE.getColumnIndex("tipoRep");
            indRep1 = cursorE.getColumnIndex("rep1");
            indRep2 = cursorE.getColumnIndex("rep2");
            indRep3 = cursorE.getColumnIndex("rep3");
            indRep4 = cursorE.getColumnIndex("rep4");
            indRep5 = cursorE.getColumnIndex("rep5");
            indRep6 = cursorE.getColumnIndex("rep6");
            indRep7 = cursorE.getColumnIndex("rep7");
            indRep8 = cursorE.getColumnIndex("rep8");
            indTempoExecucao = cursorE.getColumnIndex("tempoExecucao");
            indDescansoM = cursorE.getColumnIndex("descansoM");
            indDescansoS = cursorE.getColumnIndex("descansoS");
            indUnilateral = cursorE.getColumnIndex("unilateral");
            indMusculoSpinner = cursorE.getColumnIndex("musculoSpinner");
            indExercicioSpinner = cursorE.getColumnIndex("exercicioSpinner");
            indObs = cursorE.getColumnIndex("obs");
            indTempoMedio = cursorE.getColumnIndex("tempoMedio");

            cursorA = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino = " + id + " ORDER BY pos ASC", null);
            indAerobico = cursorA.getColumnIndex("aerobico");
            indDuracaoH = cursorA.getColumnIndex("duracaoH");
            indDuracaoM = cursorA.getColumnIndex("duracaoM");
            indDuracaoS = cursorA.getColumnIndex("duracaoS");
            indASeries = cursorA.getColumnIndex("series");
            indADescansoM = cursorA.getColumnIndex("descansoM");
            indADescansoS = cursorA.getColumnIndex("descansoS");
            indDistancia = cursorA.getColumnIndex("distancia");
            indKM = cursorA.getColumnIndex("km");
            indATempoMedio = cursorA.getColumnIndex("tempoMedio");

            cursorE.moveToFirst();
            cursorA.moveToFirst();

            h.post(runEnvio);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final Runnable runEnvio = new Runnable() {
        @Override
        public void run() {
            h.postDelayed(this, 50);
            try {
                if (!enviadoN && !enviadoC) {
                    enviadoN = true;
                    connect.write(treino);
                } else if (enviadoN && !enviadoC) {
                    enviadoC = true;
                    connect.write(count);
                } else {
                    if (!StopRun1 && !StopRun2) {
                        try {
                            String Exercicio = cursorE.getString(indExercicio);
                            String Series = cursorE.getString(indSeries);
                            String TipoRep = cursorE.getString(indTipoRep);
                            String Rep1 = cursorE.getString(indRep1);
                            String Rep2 = cursorE.getString(indRep2);
                            String Rep3 = cursorE.getString(indRep3);
                            String Rep4 = cursorE.getString(indRep4);
                            String Rep5 = cursorE.getString(indRep5);
                            String Rep6 = cursorE.getString(indRep6);
                            String Rep7 = cursorE.getString(indRep7);
                            String Rep8 = cursorE.getString(indRep8);
                            String TempoExecucao = cursorE.getString(indTempoExecucao);
                            String DescansoM = cursorE.getString(indDescansoM);
                            String DescansoS = cursorE.getString(indDescansoS);
                            String Unilateral = cursorE.getString(indUnilateral);
                            String MusculoSpinner = cursorE.getString(indMusculoSpinner);
                            String ExercicioSpinner = cursorE.getString(indExercicioSpinner);
                            String Obs = cursorE.getString(indObs);
                            String tempoMedio = cursorE.getString(indTempoMedio);

                            String Msg = ("INSERT INTO exercicios (exercicio, series, tipoRep, rep1, rep2," +
                                    "rep3, rep4, rep5, rep6, rep7, rep8, tempoExecucao, descansoM, descansoS, unilateral, musculoSpinner, exercicioSpinner, obs, tempoMedio, idTreino, completo, serieAtual) VALUES" +
                                    "('" + Exercicio + "', " + Series + ", " + TipoRep + ", " + Rep1 + "" +
                                    ", " + Rep2 + ", " + Rep3 + ", " + Rep4 + "" +
                                    ", " + Rep5 + ", " + Rep6 + ", " + Rep7 + "" +
                                    ", " + Rep8 + ", " + TempoExecucao + ", " + DescansoM + "" +
                                    ", " + DescansoS + ", " + Unilateral +
                                    ", " + MusculoSpinner + ", " + ExercicioSpinner + ", '" + Obs + "', " + tempoMedio);

                            byte[] msg1 = Msg.getBytes();
                            connect.write(msg1);
                            cursorE.moveToNext();
                        } catch (Exception e) {
                            StopRun1 = true;
                            e.printStackTrace();
                        }
                    } else if (StopRun1 && !StopRun2) {
                        try {
                            String Aerobico = cursorA.getString(indAerobico);
                            String DuracaoH = cursorA.getString(indDuracaoH);
                            String DuracaoM = cursorA.getString(indDuracaoM);
                            String DuracaoS = cursorA.getString(indDuracaoS);
                            String Series = cursorA.getString(indASeries);
                            String DescansoM = cursorA.getString(indADescansoM);
                            String DescansoS = cursorA.getString(indADescansoS);
                            String Distancia = cursorA.getString(indDistancia);
                            String KM = cursorA.getString(indKM);
                            String tempoMedio = cursorA.getString(indATempoMedio);

                            String Msg = ("INSERT INTO aerobicos (aerobico, duracaoH, duracaoM, duracaoS, series, " +
                                    "descansoM, descansoS, distancia, km, idTreino) VALUES " +
                                    "('" + Aerobico + "', " + DuracaoH + ", " + DuracaoM + ", " + DuracaoS + ", "
                                    + Series + ", " + DescansoM + ", " + DescansoS + ", " + Distancia + ", " + KM + ", " + tempoMedio);

                            byte[] msg2 = Msg.getBytes();
                            connect.write(msg2);
                            cursorA.moveToNext();
                        } catch (Exception e) {
                            StopRun2 = true;
                            e.printStackTrace();
                        }
                    } else {
                        h.removeCallbacks(runEnvio);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!desligaBt) {
                    desligaBt = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            connect.cancel();
                            try {
                                PairedDevicesActivity.connect.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!BtAtivo) {
                                btAdapter.disable();
                            }
                        }
                    }, 1000);
                }
            }
        }
    };

    //FIM TRANSFERÊNCIA BLUETOOTH

    static class MyAdapter extends BaseAdapter {
        private List<String> mItems;

        private static class ViewHolder {
            public TextView text;

            ViewHolder(View v) {
                text = (TextView) v;
                text.setTextColor(Color.WHITE);
            }
        }

        MyAdapter(List<String> items) {
            mItems = items;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder vh;

            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                vh = new ViewHolder(view);
                view.setTag(vh);
            } else {
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }

            vh.text.setText(mItems.get(position));

            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }
    }

}
