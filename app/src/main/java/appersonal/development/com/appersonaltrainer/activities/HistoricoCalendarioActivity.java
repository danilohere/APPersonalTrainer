package appersonal.development.com.appersonaltrainer.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import appersonal.development.com.appersonaltrainer.model.Agua;
import appersonal.development.com.appersonaltrainer.model.Corrida;
import appersonal.development.com.appersonaltrainer.model.Treinos;
import appersonal.development.com.appersonaltrainer.R;

public class HistoricoCalendarioActivity extends AppCompatActivity {

    private static final String DIASSEMANA = "DiasSemana";
    private final boolean[] diasSemana = new boolean[7];
    private Boolean primeiro = true;
    private SQLiteDatabase bancoDados;
    private ListView lstHistoricoTreino;
    private ListView lstHistoricoCorrida;
    private String mes;
    private String ano;
    private float previousX;
    private TextView txtMes;
    private final Button[][] diasCalendario = new Button[8][9];
    private Button btnFechar;
    private RelativeLayout lytTreinoHistorico;
    private TableLayout tbCalendario;
    private ImageView fundo;
    private TextView txtfundo;
    private TextView txtData;
    private TextView txtAgua;
    private TextView txtDomingo;
    private TextView txtSegunda;
    private TextView txtTerca;
    private TextView txtQuarta;
    private TextView txtQuinta;
    private TextView txtSexta;
    private TextView txtSabado;
    private boolean folgaDom = true;
    private boolean folgaSeg = true;
    private boolean folgaTer = true;
    private boolean folgaQua = true;
    private boolean folgaQui = true;
    private boolean folgaSex = true;
    private boolean folgaSab = true;
    private ArrayList<Treinos> treinos;
    private ArrayList<Corrida> corridas;
    private ArrayList<Agua> aguas;
    private boolean temTreino = false;
    private boolean temCorrida = false;
    private boolean temAgua = false;
    private RelativeLayout.LayoutParams params;

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
        if (lytTreinoHistorico.getVisibility() == View.VISIBLE) {
            btnFechar.callOnClick();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_calendario);

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

        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lytTreinoHistorico = findViewById(R.id.lytTreinoHistorico);
        tbCalendario = findViewById(R.id.tbCalendario);
        fundo = findViewById(R.id.fundo);
        txtfundo = findViewById(R.id.txtfundo);
        btnFechar = findViewById(R.id.btnFechar);
        lstHistoricoTreino = findViewById(R.id.lstHistoricoTreino);
        lstHistoricoCorrida = findViewById(R.id.lstHistoricoCorrida);
        txtAgua = findViewById(R.id.txtAgua);
        txtMes = findViewById(R.id.txtMes);
        txtData = findViewById(R.id.txtData);
        txtAgua = findViewById(R.id.txtAgua);
        txtDomingo = findViewById(R.id.txtDomingo);
        txtSegunda = findViewById(R.id.txtSegunda);
        txtTerca = findViewById(R.id.txtTerca);
        txtQuarta = findViewById(R.id.txtQuarta);
        txtQuinta = findViewById(R.id.txtQuinta);
        txtSexta = findViewById(R.id.txtSexta);
        txtSabado = findViewById(R.id.txtSabado);
        diasCalendario[1][1] = findViewById(R.id.s1d1);
        diasCalendario[1][2] = findViewById(R.id.s1d2);
        diasCalendario[1][3] = findViewById(R.id.s1d3);
        diasCalendario[1][4] = findViewById(R.id.s1d4);
        diasCalendario[1][5] = findViewById(R.id.s1d5);
        diasCalendario[1][6] = findViewById(R.id.s1d6);
        diasCalendario[1][7] = findViewById(R.id.s1d7);
        diasCalendario[2][1] = findViewById(R.id.s2d1);
        diasCalendario[2][2] = findViewById(R.id.s2d2);
        diasCalendario[2][3] = findViewById(R.id.s2d3);
        diasCalendario[2][4] = findViewById(R.id.s2d4);
        diasCalendario[2][5] = findViewById(R.id.s2d5);
        diasCalendario[2][6] = findViewById(R.id.s2d6);
        diasCalendario[2][7] = findViewById(R.id.s2d7);
        diasCalendario[3][1] = findViewById(R.id.s3d1);
        diasCalendario[3][2] = findViewById(R.id.s3d2);
        diasCalendario[3][3] = findViewById(R.id.s3d3);
        diasCalendario[3][4] = findViewById(R.id.s3d4);
        diasCalendario[3][5] = findViewById(R.id.s3d5);
        diasCalendario[3][6] = findViewById(R.id.s3d6);
        diasCalendario[3][7] = findViewById(R.id.s3d7);
        diasCalendario[4][1] = findViewById(R.id.s4d1);
        diasCalendario[4][2] = findViewById(R.id.s4d2);
        diasCalendario[4][3] = findViewById(R.id.s4d3);
        diasCalendario[4][4] = findViewById(R.id.s4d4);
        diasCalendario[4][5] = findViewById(R.id.s4d5);
        diasCalendario[4][6] = findViewById(R.id.s4d6);
        diasCalendario[4][7] = findViewById(R.id.s4d7);
        diasCalendario[5][1] = findViewById(R.id.s5d1);
        diasCalendario[5][2] = findViewById(R.id.s5d2);
        diasCalendario[5][3] = findViewById(R.id.s5d3);
        diasCalendario[5][4] = findViewById(R.id.s5d4);
        diasCalendario[5][5] = findViewById(R.id.s5d5);
        diasCalendario[5][6] = findViewById(R.id.s5d6);
        diasCalendario[5][7] = findViewById(R.id.s5d7);
        diasCalendario[6][1] = findViewById(R.id.s6d1);
        diasCalendario[6][2] = findViewById(R.id.s6d2);
        diasCalendario[6][3] = findViewById(R.id.s6d3);
        diasCalendario[6][4] = findViewById(R.id.s6d4);
        diasCalendario[6][5] = findViewById(R.id.s6d5);
        diasCalendario[6][6] = findViewById(R.id.s6d6);
        diasCalendario[6][7] = findViewById(R.id.s6d7);

        try {
            iniciar();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                lytTreinoHistorico.startAnimation(anim);
                lytTreinoHistorico.setVisibility(View.INVISIBLE);
                tbCalendario.setVisibility(View.VISIBLE);
                fundo.startAnimation(anim);
                fundo.setVisibility(View.INVISIBLE);
                txtfundo.startAnimation(anim);
                txtfundo.setVisibility(View.INVISIBLE);
                temAgua = false;
                temCorrida = false;
                temTreino = false;
                txtAgua.setText("");
            }
        });
    }

    private void iniciar() throws ParseException {
        if (primeiro) {
            long data = System.currentTimeMillis();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");
            mes = formatarMes.format(data);
            ano = formatarAno.format(data);
            primeiro = false;
        }
        String date;
        switch (mes) {
            case "01":
                date = "Janeiro/" + ano;
                break;
            case "02":
                date = "Fevereiro/" + ano;
                break;
            case "03":
                date = "Março/" + ano;
                break;
            case "04":
                date = "Abril/" + ano;
                break;
            case "05":
                date = "Maio/" + ano;
                break;
            case "06":
                date = "Junho/" + ano;
                break;
            case "07":
                date = "Julho/" + ano;
                break;
            case "08":
                date = "Agosto/" + ano;
                break;
            case "09":
                date = "Setembro/" + ano;
                break;
            case "10":
                date = "Outubro/" + ano;
                break;
            case "11":
                date = "Novembro/" + ano;
                break;
            default:
                date = "Dezembro/" + ano;
                break;
        }
        txtMes.setText(date);
        criarCalendario();
    }

    private void anterior() {
        if (mes.equals("01")) {
            mes = "12";
            ano = String.valueOf(Integer.parseInt(ano) - 1);
            try {
                iniciar();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            int proxMes = Integer.parseInt(mes) - 1;
            if (proxMes > 9) {
                mes = String.valueOf(proxMes);
            } else {
                mes = "0" + proxMes;
            }
            try {
                iniciar();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void proximo() {
        if (mes.equals("12")) {
            mes = "01";
            ano = String.valueOf(Integer.parseInt(ano) + 1);
            try {
                iniciar();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            int proxMes = Integer.parseInt(mes) + 1;
            if (proxMes > 9) {
                mes = String.valueOf(proxMes);
            } else {
                mes = "0" + proxMes;
            }
            try {
                iniciar();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void criarCalendario() throws ParseException {
        int ultimoDia = ultimoDiaMes();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date PrimeiroDiaDoMes = dateFormat.parse("01/" + mes + "/" + ano);

        Calendar calendar = Calendar.getInstance();
        if (PrimeiroDiaDoMes != null) {
            calendar.setTime(PrimeiroDiaDoMes);
        }

        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        SharedPreferences diasSemanaSP = getSharedPreferences(DIASSEMANA, Context.MODE_PRIVATE);
        int size = diasSemanaSP.getInt("diasSemana_size", 0);
        boolean diaConfigurado = false;
        for (int i = 0; i < size; i++) {
            diasSemana[i] = diasSemanaSP.getBoolean("diasSemana_" + i, false);
            if (diasSemana[i]) {
                alterarBotao(i);
                diaConfigurado = true;
            }
        }
        if (!diaConfigurado) {
            Toast.makeText(this, "Para melhorar a visualização, configure os dias de treino na página de lembretes!", Toast.LENGTH_LONG).show();
        }


        int dia = 1;

        for (int s = 1; s <= 6; s++) {
            for (int d = 1; d <= 7; d++) {
                if ((d < diaSemana && s == 1) || dia > ultimoDia) {
                    diasCalendario[s][d].setText("");
                    diasCalendario[s][d].setEnabled(false);
                    diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                } else {
                    diasCalendario[s][d].setText(String.valueOf(dia));
                    diasCalendario[s][d].setEnabled(true);
                    switch (d) {
                        case 1:
                            if (folgaDom) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                        case 2:
                            if (folgaSeg) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                        case 3:
                            if (folgaTer) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                        case 4:
                            if (folgaQua) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                        case 5:
                            if (folgaQui) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                        case 6:
                            if (folgaSex) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                        default:
                            if (folgaSab) {
                                diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapelocked));
                            } else {
                                diasCalendario[s][d].setBackgroundColor(Color.TRANSPARENT);
                            }
                            break;
                    }
                    dia++;
                }
            }
        }

        recuperarTreinos();
    }


    private int ultimoDiaMes() {
        int ultimoDia = 31;
        switch (mes) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                ultimoDia = 31;
                break;
            case "04":
            case "06":
            case "09":
            case "11":
                ultimoDia = 30;
                break;
            case "02":
                if (Integer.parseInt(ano) % 400 == 0 || (Integer.parseInt(ano) % 4 == 0 && Integer.parseInt(ano) % 100 != 0)) {
                    ultimoDia = 29;
                } else {
                    ultimoDia = 28;
                }
                break;
        }
        return ultimoDia;
    }


    private void recuperarTreinos() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM historico ORDER BY data ASC", null);

            int indTreino = cursor.getColumnIndex("treinoH");
            int indData = cursor.getColumnIndex("data");
            int indDataInicio = cursor.getColumnIndex("dataInicio");

            treinos = new ArrayList<>();

            while (cursor.moveToNext()) {
                final long data = cursor.getLong(indData);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");
                String mesTreino = formatarMes.format(data);
                String anoTreino = formatarAno.format(data);

                try {
                    if (mes.equals(mesTreino) && ano.equals(anoTreino)) {
                        Treinos treino = new Treinos();
                        treino.setNome(cursor.getString(indTreino));
                        treino.setData(cursor.getLong(indData));
                        treino.setDataInicio(cursor.getLong(indDataInicio));
                        treinos.add(treino);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM historicokm ORDER BY data ASC", null);
            int indKM = cursor.getColumnIndex("km");
            int indtempo = cursor.getColumnIndex("tempo");
            int indData = cursor.getColumnIndex("data");

            corridas = new ArrayList<>();

            while (cursor.moveToNext()) {
                final long data = cursor.getLong(indData);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");
                String mesCorrida = formatarMes.format(data);
                String anoCorrida = formatarAno.format(data);

                try {
                    if (mes.equals(mesCorrida) && ano.equals(anoCorrida)) {
                        Corrida corrida = new Corrida();
                        corrida.setKm(cursor.getString(indKM));
                        corrida.setTempo(cursor.getString(indtempo));
                        corrida.setData(cursor.getLong(indData));
                        corridas.add(corrida);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM historicoagua ORDER BY data ASC", null);

            int indCoposAgua = cursor.getColumnIndex("coposAgua");
            int indData = cursor.getColumnIndex("data");

            aguas = new ArrayList<>();

            while (cursor.moveToNext()) {
                final long data = cursor.getLong(indData);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");
                String mesAgua = formatarMes.format(data);
                String anoAgua = formatarAno.format(data);

                try {
                    if (mes.equals(mesAgua) && ano.equals(anoAgua)) {
                        Agua agua = new Agua();
                        agua.setCopos(cursor.getInt(indCoposAgua));
                        agua.setData(cursor.getLong(indData));
                        aguas.add(agua);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        adicionarFuncaoBotoes();
    }

    private void adicionarFuncaoBotoes() {
        //procurando a semana no calendário
        for (int s = 1; s <= 6; s++) {
            //procurando o dia da semana no calendário
            for (int d = 1; d <= 7; d++) {

                //se for um botão que seja dia
                if (!diasCalendario[s][d].getText().equals("")) {

                    final int dia = Integer.parseInt(diasCalendario[s][d].getText().toString());
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarData = new SimpleDateFormat("dd");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");

                    //COMEÇO DA VERIFICAÇÃO SE TEM TREINO NAQUELE DIA
                    long data = 0;
                    final ArrayList<Treinos> t = new ArrayList<>();
                    for (Treinos treino : treinos) {
                        String date = formatarData.format(treino.getData());
                        if (Integer.parseInt(date) == dia) {
                            t.add(treino);
                            data = treino.getData();
                            diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape));
                        }
                    }

                    final ArrayList<Agua> a = new ArrayList<>();
                    for (Agua agua : aguas) {
                        String date = formatarData.format(agua.getData());
                        if (Integer.parseInt(date) == dia) {
                            a.add(agua);
                            data = agua.getData();
                        }
                    }


                    final ArrayList<Corrida> c = new ArrayList<>();
                    for (Corrida corrida : corridas) {
                        String date = formatarData.format(corrida.getData());
                        if (Integer.parseInt(date) == dia) {
                            c.add(corrida);
                            data = corrida.getData();
                            diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape));
                        }
                    }
                    long hoje = System.currentTimeMillis();
                    int diaHoje = Integer.parseInt(formatarData.format(hoje));
                    int mesHoje = Integer.parseInt(formatarMes.format(hoje));
                    int anoHoje = Integer.parseInt(formatarAno.format(hoje));
                    if (dia == diaHoje && Integer.parseInt(mes) == mesHoje && Integer.parseInt(ano) == anoHoje) {
                        diasCalendario[s][d].setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape3));
                    }

                    final int finalD = d;
                    final long finalData = data;
                    diasCalendario[s][d].setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(View view) {
                            if (!t.isEmpty()) {
                                AdapterTreinosPersonalizado adaptador = new AdapterTreinosPersonalizado(t, HistoricoCalendarioActivity.this);
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (120 * getApplicationContext().getResources().getDisplayMetrics().density));
                                params.addRule(RelativeLayout.BELOW, R.id.txtData);
                                params.topMargin = 20;
                                lstHistoricoTreino.setLayoutParams(params);
                                lstHistoricoTreino.setAdapter(adaptador);
                                temTreino = true;
                            } else {
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
                                params.addRule(RelativeLayout.BELOW, R.id.txtData);
                                lstHistoricoTreino.setLayoutParams(params);
                            }
                            if (!c.isEmpty()) {
                                AdapterCorridasPersonalizado adaptador = new AdapterCorridasPersonalizado(c, HistoricoCalendarioActivity.this);
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (100 * getApplicationContext().getResources().getDisplayMetrics().density));
                                params.addRule(RelativeLayout.BELOW, R.id.lstHistoricoTreino);
                                params.topMargin = 20;
                                lstHistoricoCorrida.setLayoutParams(params);
                                lstHistoricoCorrida.setAdapter(adaptador);
                                temCorrida = true;
                            } else {
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
                                params.addRule(RelativeLayout.BELOW, R.id.lstHistoricoTreino);
                                lstHistoricoCorrida.setLayoutParams(params);
                            }
                            if (!a.isEmpty()) {
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.BELOW, R.id.lstHistoricoCorrida);
                                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                params.topMargin = 20;
                                txtAgua.setLayoutParams(params);
                                for (Agua agua : aguas) {
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarData = new SimpleDateFormat("dd");
                                    String date = formatarData.format(agua.getData());
                                    if (Integer.parseInt(date) == dia) {
                                        if (agua.getCopos() == 1) {
                                            txtAgua.setText("Você tomou água " + agua.getCopos() + " vez nesse dia");
                                        } else {
                                            txtAgua.setText("Você tomou água " + agua.getCopos() + " vezes nesse dia");
                                        }
                                        temAgua = true;
                                    }
                                }
                            } else {
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 0);
                                params.addRule(RelativeLayout.BELOW, R.id.lstHistoricoCorrida);
                                txtAgua.setLayoutParams(params);
                            }

                            if (temTreino || temCorrida || temAgua) {
                                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                Animation animfast = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_fast);
                                lytTreinoHistorico.startAnimation(anim);
                                lytTreinoHistorico.setVisibility(View.VISIBLE);
                                tbCalendario.setVisibility(View.INVISIBLE);
                                fundo.startAnimation(animfast);
                                fundo.setVisibility(View.VISIBLE);
                                txtfundo.startAnimation(anim);
                                txtfundo.setVisibility(View.VISIBLE);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM");
                                String date = formatarData.format(finalData);
                                String diaSemana;
                                switch (finalD) {
                                    case 1:
                                        diaSemana = "Dom";
                                        break;
                                    case 2:
                                        diaSemana = "Seg";
                                        break;
                                    case 3:
                                        diaSemana = "Ter";
                                        break;
                                    case 4:
                                        diaSemana = "Qua";
                                        break;
                                    case 5:
                                        diaSemana = "Qui";
                                        break;
                                    case 6:
                                        diaSemana = "Sex";
                                        break;
                                    default:
                                        diaSemana = "Sáb";
                                        break;
                                }
                                txtData.setText(diaSemana + " " + date);
                            }
                        }
                    });
                }
            }
        }
    }

    private void alterarBotao(int dia) {
        switch (dia + 1) {
            case 1:
                txtSegunda.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaSeg = false;
                break;
            case 2:
                txtTerca.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaTer = false;
                break;
            case 3:
                txtQuarta.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaQua = false;
                break;
            case 4:
                txtQuinta.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaQui = false;
                break;
            case 5:
                txtSexta.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaSex = false;
                break;
            case 6:
                txtSabado.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaSab = false;
                break;
            case 7:
                txtDomingo.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshape2));
                folgaDom = false;
                break;
        }
    }

    private class AdapterTreinosPersonalizado extends BaseAdapter {

        private final List<Treinos> treinos;
        private final Activity activity;

        AdapterTreinosPersonalizado(List<Treinos> treinos, Activity activity) {
            this.treinos = treinos;
            this.activity = activity;
        }


        @Override
        public int getCount() {
            return treinos.size();
        }

        @Override
        public Object getItem(int position) {
            return treinos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_historico, parent, false);

            Treinos treino = treinos.get(position);

            TextView txtNomeTreino =
                    view.findViewById(R.id.txtNome);
            TextView txtDuracao =
                    view.findViewById(R.id.txtDuracao);
            TextView txtHora =
                    view.findViewById(R.id.txtHora);

            txtNomeTreino.setText(treino.getNome());
            long data = treino.getData();
            if (treino.getDataInicio() != 0) {
                long dataInicio = treino.getDataInicio();
                dataInicio = data - dataInicio;
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarDuracao = new SimpleDateFormat("HH:mm");
                String duracao = formatarDuracao.format(dataInicio);
                txtDuracao.setText("Duração do treino: " + duracao);
            } else {
                txtDuracao.setText("");
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarHora = new SimpleDateFormat("HH");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMinuto = new SimpleDateFormat("mm");
            String hora = formatarHora.format(data);
            String minuto = formatarMinuto.format(data);
            txtHora.setText("Finalizado às " + hora + "h" + minuto);

            return view;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            previousX = x;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (x > previousX) {
                if (x - previousX > 150) {
                    anterior();
                }
            } else {
                if (previousX - x > 150) {
                    proximo();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private class AdapterCorridasPersonalizado extends BaseAdapter {

        private final List<Corrida> corridas;
        private final Activity activity;

        AdapterCorridasPersonalizado(ArrayList<Corrida> corridas, Activity activity) {
            this.corridas = corridas;
            this.activity = activity;
        }


        @Override
        public int getCount() {
            return corridas.size();
        }

        @Override
        public Object getItem(int position) {
            return corridas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_historico_corrida, parent, false);

            Corrida corrida = corridas.get(position);

            TextView txtKMTempo =
                    view.findViewById(R.id.txtKMTempo);
            TextView txtData =
                    view.findViewById(R.id.txtData);
            TextView txtVelocidade =
                    view.findViewById(R.id.txtVelocidade);
            TextView txtHora =
                    view.findViewById(R.id.txtHora);

            txtKMTempo.setText(corrida.getKm() + " em " + corrida.getTempo());
            long data = corrida.getData();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM");
            String date = formatarData.format(data);
            txtData.setText(date);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarHora = new SimpleDateFormat("HH:mm");
            String horario = formatarHora.format(data);
            txtHora.setText(horario);
            int hora = Integer.parseInt(corrida.getTempo().substring(0, 1));
            int min = Integer.parseInt(corrida.getTempo().substring(2, 4));
            int seg = Integer.parseInt(corrida.getTempo().substring(5, 7));
            int tempoA = (hora * 3600) + (min * 60) + seg;
            String skm = corrida.getKm().replaceAll(" KM", "");
            double km = Double.parseDouble(skm.replaceAll(",", "."));
            double kmph;
            if (km < 0.01) {
                kmph = 0.0;
            } else {
                kmph = ((km * 1000) / tempoA) * 3.6;
            }
            txtVelocidade.setText("Velocidade média: " + formatar(kmph) + " km/h");

            return view;
        }
    }

    private Double formatar(Double a) {
        NumberFormat format = new DecimalFormat("#.##");
        String num = format.format(a).replaceAll(",", ".");
        return Double.parseDouble(num);
    }

}
