package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.Controller.ConnectionThread;
import appersonal.development.com.appersonaltrainer.R;

public class MeusTreinosActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int DISCOVERABLE_BLUETOOTH = 3;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean BtAtivo;
    private ListView lstTreinos;
    private ArrayList<String> treinos;
    private ArrayList<Integer> ids;
    private int id;
    private boolean enviadoN;
    private boolean enviadoC;
    private String nomeTreino;
    private SQLiteDatabase bancoDados;
    private AlertDialog alerta;
    private int lstposition;
    private int duration = 30;
    private Handler h = new Handler();
    private int tentativa;
    private boolean StopRun1;
    private boolean StopRun2;
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

    private int indAerobico;
    private int indDuracaoH;
    private int indDuracaoM;
    private int indDuracaoS;
    private int indASeries;
    private int indADescansoM;
    private int indADescansoS;
    private int indDistancia;
    private int indKM;

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
                ativarBT();
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
        Button btnReceber = findViewById(R.id.btnReceber);
        txtTeste = findViewById(R.id.txtTeste);
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
        btnReceber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
                startActivityForResult(discoverableIntent, DISCOVERABLE_BLUETOOTH);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            connect.cancel();
                                            try {
                                                PairedDevicesActivity.connect.cancel();
                                            } catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            if (!BtAtivo) {
                                                btAdapter.disable();
                                            }
                                        }
                                    }, 1000);

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
                            if (tentativa >= 3){
                                Toast.makeText(MeusTreinosActivity.this, "Dispositivo não encontrado", Toast.LENGTH_SHORT).show();
                                connect.cancel();
                                try {
                                    PairedDevicesActivity.connect.cancel();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                if (!BtAtivo){
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
            if (resultCode == duration) {
                Intent intent = new Intent(this, ReceberTreinoActivity.class);
                startActivity(intent);
            }
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
            cursorE = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + idTreino, null);
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

            cursorA = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino = " + id, null);
            indAerobico = cursorA.getColumnIndex("aerobico");
            indDuracaoH = cursorA.getColumnIndex("duracaoH");
            indDuracaoM = cursorA.getColumnIndex("duracaoM");
            indDuracaoS = cursorA.getColumnIndex("duracaoS");
            indASeries = cursorA.getColumnIndex("series");
            indADescansoM = cursorA.getColumnIndex("descansoM");
            indADescansoS = cursorA.getColumnIndex("descansoS");
            indDistancia = cursorA.getColumnIndex("distancia");
            indKM = cursorA.getColumnIndex("km");

            cursorE.moveToFirst();
            cursorA.moveToFirst();

            h.post(runEnvio);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursorA.close();
            cursorE.close();
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

                            String Msg = ("INSERT INTO exercicios (exercicio, series, tipoRep, rep1, rep2," +
                                    "rep3, rep4, rep5, rep6, rep7, rep8, tempoExecucao, descansoM, descansoS, unilateral, musculoSpinner, exercicioSpinner, obs, idTreino, completo, serieAtual) VALUES" +
                                    "('" + Exercicio + "', " + Series + ", " + TipoRep + ", " + Rep1 + "" +
                                    ", " + Rep2 + ", " + Rep3 + ", " + Rep4 + "" +
                                    ", " + Rep5 + ", " + Rep6 + ", " + Rep7 + "" +
                                    ", " + Rep8 + ", " + TempoExecucao + ", " + DescansoM + "" +
                                    ", " + DescansoS + ", " + Unilateral +
                                    ", " + MusculoSpinner + ", " + ExercicioSpinner + ", '" + Obs + "'");

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

                            String Msg = ("INSERT INTO aerobicos (aerobico, duracaoH, duracaoM, duracaoS, series, " +
                                    "descansoM, descansoS, distancia, km, idTreino) VALUES " +
                                    "('" + Aerobico + "', " + DuracaoH + ", " + DuracaoM + ", " + DuracaoS + ", "
                                    + Series + ", " + DescansoM + ", " + DescansoS + ", " + Distancia + ", " + KM);

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

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };

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
