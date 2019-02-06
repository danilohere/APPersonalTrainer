package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import appersonal.development.com.appersonaltrainer.R;

public class ExercicioActivity extends AppCompatActivity {

    private ToggleButton btnIniciar;
    private Button btnAnterior;
    private Button btnProximo;
    private TextView txtExercicio;
    private TextView txtObs;
    private TextView txtRep;
    private TextView txtUnilateral;
    private TextView txtSeries;
    private TextView txtSeriesTotal;
    private TextView txtDescanso;
    private TextView txtTemporizador;
    private TextView txtTempoExecucao;
    private Switch swtInicioAut;
    private ImageView imgExercicio;

    private int tempoExecucao;
    private int tempoExecucaoInicial;
    private int[] rep = new int[20];
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private int[] repEsq = new int[20];
    private int[] repInd = new int[20];
    private int series;
    private int descanso;
    private int tipoRep;
    private int seriesDrop = 1;
    private int sa = 1;
    private int su = 1;
    private int sd = 1;
    private int r = 0;
    private int re = 0;
    private int s = 1;
    private int falha;
    private int contbotao = 0;
    private int uni;
    private int id;
    private int idTreino;
    private int preparo = 0;
    private int iniciando = 0;
    private int antprox;
    private int temporizador;
    private int vozselecionada;
    private int som;
    private int bf;
    private int e;
    private int m;
    private boolean completo = false;
    private boolean rosca21 = false;

    private Handler handler = new Handler();
    private Runnable runnableRep;
    private Runnable runnablePause;
    private CountDownTimer cdTimer;

    private MediaPlayer largada;
    private MediaPlayer bambam;
    private MediaPlayer contador;

    private SQLiteDatabase bancoDados;

    private static final String INICIO_AUTOMATICO = "InicioAut";
    private static final String VOZ = "Voz";
    private static final String BOTAOFONE = "BotaoFone";
    private static final String SONS = "Sons";
    private static final String DATA = "Data";

    @Override
    public void onBackPressed() {

        if (imgExercicio.getVisibility() == View.VISIBLE) {
            imgExercicio.setVisibility(View.INVISIBLE);
        } else {
            if (btnIniciar.isChecked()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExercicioActivity.this);
                builder.setTitle("Exercício em execução");
                builder.setMessage("Deseja parar o exercício?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnIniciar.setChecked(false);
                        swtInicioAut.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        handler.removeCallbacks(runnableRep);
                        if (cdTimer != null)
                            cdTimer.cancel();
                        btnIniciar.setChecked(false);
                        if (largada != null)
                            largada.release();
                        if (contador != null)
                            contador.release();
                        if (bambam != null)
                            bambam.release();
                        if (!completo) {
                            bancoDados.execSQL("UPDATE exercicios SET serieAtual = " + s + " WHERE idExercicio =" + id);
                        } else {
                            bancoDados.execSQL("UPDATE exercicios SET completo = 1, serieAtual = " + s + " WHERE idExercicio =" + id);
                        }
                        ExercicioActivity.this.finish();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alerta = builder.create();
                alerta.show();
            } else {
                if (!completo) {
                    bancoDados.execSQL("UPDATE exercicios SET serieAtual = " + s + " WHERE idExercicio =" + id);
                } else {
                    bancoDados.execSQL("UPDATE exercicios SET completo = 1, serieAtual = " + s + " WHERE idExercicio =" + id);
                }
                super.onBackPressed();
            }
        }
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
        setContentView(R.layout.activity_exercicio);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        Bundle extra;
        extra = intent.getExtras();
        if (extra != null) {
            idTreino = extra.getInt("idTreino");
            id = extra.getInt("idExercicio");
        }

        //Implementa o ad na activity
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView.loadAd(adRequest);

        btnIniciar = findViewById(R.id.btnIniciar);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnProximo = findViewById(R.id.btnProximo);
        Button btnMais = findViewById(R.id.btnMais);
        Button btnMenos = findViewById(R.id.btnMenos);
        txtRep = findViewById(R.id.txtRep);
        txtUnilateral = findViewById(R.id.txtUnilateral);
        txtObs = findViewById(R.id.txtObs);
        txtSeries = findViewById(R.id.txtSeries);
        txtSeriesTotal = findViewById(R.id.txtSeriesTotal);
        txtDescanso = findViewById(R.id.txtDescanso);
        txtTemporizador = findViewById(R.id.txtTemporizador);
        txtExercicio = findViewById(R.id.txtExercicio);
        swtInicioAut = findViewById(R.id.swtInicioAut);
        txtTempoExecucao = findViewById(R.id.txtTempoExecucao);
        ImageView imgImagem = findViewById(R.id.imgImagem);
        imgExercicio = findViewById(R.id.imgExercicio);


        SharedPreferences botaofone = getSharedPreferences(BOTAOFONE, ConfiguracoesActivity.MODE_PRIVATE);
        SharedPreferences sons = getSharedPreferences(SONS, ConfiguracoesActivity.MODE_PRIVATE);
        som = sons.getInt("Sons", 0);
        bf = botaofone.getInt("BotaoFone", 0);

        SharedPreferences voz = getSharedPreferences(VOZ, ConfiguracoesActivity.MODE_PRIVATE);
        vozselecionada = voz.getInt("Voz", 0);

        carregarValores(0);
        Descanso();

        btnMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempoExecucao - tempoExecucaoInicial < 5 && tempoExecucao - tempoExecucaoInicial > -5) {
                    tempoExecucao++;
                    txtTempoExecucao.setText(String.valueOf(tempoExecucao));
                } else {
                    Toast.makeText(getApplicationContext(), "Valor máximo atingido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempoExecucao > 1) {
                    tempoExecucao--;
                    txtTempoExecucao.setText(String.valueOf(tempoExecucao));
                } else {
                    Toast.makeText(getApplicationContext(), "Valor mínimo atingido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnIniciar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                r = 0;
                if (!swtInicioAut.isChecked()) {
                    preparo = 0;
                }
                if (isChecked) {
                    temporizador = 3;
                    btnAnterior.setEnabled(false);
                    btnProximo.setEnabled(false);
                    if (re > 0) {
                        r = re;
                    }
                    runnablePause = new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            if (rosca21) {
                                r = 1;
                                executarExercicio();
                            } else if (temporizador == 3 && preparo == 0) {
                                txtTemporizador.setTextSize(48);
                                txtTemporizador.setText("Preparar!");
                                txtTemporizador.setVisibility(View.VISIBLE);
                                handler.postDelayed(runnablePause, 2000);
                                preparo = 1;
                                iniciando = 1;
                            } else if (temporizador > 0) {
                                txtTemporizador.setVisibility(View.VISIBLE);
                                txtTemporizador.setTextSize(60);
                                handler.postDelayed(runnablePause, 1000);
                                txtTemporizador.setText(String.valueOf(temporizador));
                                contagem(temporizador);
                                temporizador--;
                            } else {
                                handler.removeCallbacks(runnablePause);
                                txtTemporizador.setVisibility(View.INVISIBLE);
                                if (iniciando == 1 && uni == 1) {
                                    sa = 2;
                                    iniciando = 0;
                                    executarExercicio();
                                } else if (iniciando == 0) {
                                    if (uni == 1)
                                        r--;
                                    sa = 1;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            executarExercicio();
                                        }
                                    }, tempoExecucao * 1000);
                                } else {
                                    iniciando = 0;
                                    executarExercicio();
                                }

                                if (som == 0) {
                                    largada(2);
                                } else {
                                    bambam(1);
                                }
                            }
                        }
                    };
                    handler.post(runnablePause);
                    temporizador = 3;
                } else {
                    btnProximo.setEnabled(true);
                    btnAnterior.setEnabled(true);
                    if (tipoRep == 3 && falha == 1) {
                        r = 51;
                        falha = 0;
                    } else {
                        Descanso();
                        handler.removeCallbacks(runnableRep);
                        handler.removeCallbacks(runnablePause);
                        if (cdTimer != null)
                            cdTimer.cancel();
                    }
                }
            }
        });

        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (antprox == 1) {
                    Toast.makeText(getApplicationContext(), "Último exercício", Toast.LENGTH_SHORT).show();
                } else {
                    if (!completo) {
                        bancoDados.execSQL("UPDATE exercicios SET serieAtual = " + s + " WHERE idExercicio =" + id);
                        carregarValores(1);
                    } else {
                        bancoDados.execSQL("UPDATE exercicios SET completo = 1, serieAtual = " + s + " WHERE idExercicio =" + id);
                        btnIniciar.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        handler.removeCallbacks(runnableRep);
                        if (cdTimer != null)
                            cdTimer.cancel();
                        carregarValores(1);
                    }
                }
            }
        });

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (antprox == 2) {
                    Toast.makeText(getApplicationContext(), "Primeiro exercício", Toast.LENGTH_SHORT).show();
                } else {
                    if (!completo) {
                        bancoDados.execSQL("UPDATE exercicios SET serieAtual = " + s + " WHERE idExercicio =" + id);
                        carregarValores(2);
                    } else {
                        bancoDados.execSQL("UPDATE exercicios SET completo = 1, serieAtual = " + s + " WHERE idExercicio =" + id);
                        btnIniciar.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        handler.removeCallbacks(runnableRep);
                        if (cdTimer != null)
                            cdTimer.cancel();
                        carregarValores(2);
                    }
                }
            }
        });
        imgImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherImagem();
                imgExercicio.setVisibility(View.VISIBLE);
            }
        });
        imgExercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgExercicio.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (bf == 1) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
                contbotao += 1;
                if (btnIniciar.isChecked()) {
                    btnIniciar.setChecked(false);
                    contbotao = 0;
                } else {
                    Runnable runnableBotaoFone = new Runnable() {
                        @Override
                        public void run() {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (contbotao == 1) {
                                        btnIniciar.setChecked(true);
                                        contbotao = 0;
                                        handler.removeCallbacks(this);
                                    } else if (contbotao == 2) {
                                        Toast.makeText(ExercicioActivity.this, "Próximo", Toast.LENGTH_SHORT).show();
                                        btnProximo.callOnClick();
                                        contbotao = 0;
                                        handler.removeCallbacks(this);
                                    } else if (contbotao == 3) {
                                        btnAnterior.callOnClick();
                                        Toast.makeText(ExercicioActivity.this, "Anterior", Toast.LENGTH_SHORT).show();
                                        contbotao = 0;
                                        handler.removeCallbacks(this);
                                    }
                                }
                            }, 1500);
                        }
                    };
                    handler.post(runnableBotaoFone);
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // MÉTODOS CRIADOS PARA A CLASSE

    @SuppressLint("SetTextI18n")
    private void Descanso() {
        long m = descanso / 60;
        long s = descanso % 60;
        if (s > 9) {
            txtDescanso.setText(m + ":" + s);
        } else {
            txtDescanso.setText(m + ":0" + s);
        }
    }

    @SuppressLint("SetTextI18n")
    private void executarExercicio() {
        txtSeries.setText(String.valueOf(s));
        falha = 1;
        runnableRep = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (r <= rep[s - 1]) { //Se a repetição atual for menor ou igual à repetição total
                    contagem(r);
                    if (r == rep[s - 1]) { //Se a repetição atual for igual à final, ou seja, fim da série
                        iniciando = 1;
                        if (s == series) { //Se a série atual for igual à final, ou seja, última série
                            if (tipoRep == 2 && sd < seriesDrop * 2 && uni == 2) { // Se for unilateral e dropset
                                if (som == 0) {
                                    largada(1);
                                } else {
                                    bambam(0);
                                }
                                Vibrar(300);
                            } else if (tipoRep == 2 && sd < seriesDrop) { //Se for dropset
                                if (som == 0) {
                                    largada(1);
                                } else {
                                    bambam(0);
                                }
                                Vibrar(300);
                            } else if (uni == 2 && su < 2) { //Se for unilateral
                                if (som == 0) {
                                    largada(1);
                                } else {
                                    bambam(0);
                                }
                                Vibrar(300);
                            } else { //Se não tiver em nenhuma das condições acima,
                                if (uni != 1) {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (som == 0) {
                                                largada(2);
                                            } else {
                                                bambam.release();
                                                Random al = new Random();
                                                int i = al.nextInt(4) + 2;
                                                bambam(i);
                                            }
                                        }
                                    }, 300);
                                    Vibrar(700);
                                    completo = true;
                                    verificarProximo();
                                    swtInicioAut.setChecked(false);
                                    txtTemporizador.setVisibility(View.VISIBLE);
                                    txtTemporizador.setTextSize(35);
                                    txtTemporizador.setText("Exercício Concluído");
                                    atualizarData();
                                } else if (sa == 2) {
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (som == 0) {
                                                largada(2);
                                            } else {
                                                bambam.release();
                                                Random al = new Random();
                                                int i = al.nextInt(4) + 2;
                                                bambam(i);
                                            }
                                        }
                                    }, 300);
                                    Vibrar(700);
                                    completo = true;
                                    verificarProximo();
                                    swtInicioAut.setChecked(false);
                                    txtTemporizador.setVisibility(View.VISIBLE);
                                    txtTemporizador.setTextSize(35);
                                    txtTemporizador.setText("Exercício Concluído");
                                    atualizarData();
                                }
                            }
                        } else { //Se não for a última série
                            if (uni != 1) { //Se for diferente de Alternado
                                if (tipoRep == 2 && sd < seriesDrop && uni != 2) { //Se for dropset e diferente de unilateral
                                    Vibrar(300);
                                } else if (tipoRep == 2 && sd < seriesDrop * 2 && uni == 2) { //Se for dropset e unilateral
                                    if (sd == seriesDrop) {
                                        if (som == 0) {
                                            largada(1);
                                        } else {
                                            bambam(0);
                                        }
                                    }
                                    Vibrar(300);
                                } else {
                                    if (som == 0) {
                                        largada(1);
                                    } else {
                                        bambam(0);
                                    }
                                    Vibrar(300);
                                }
                            } else if (sa == 2) { //Se for alternado, ele precisa ser a segunda repetição para tocar o áudio
                                if (som == 0) {
                                    largada(1);
                                } else {
                                    bambam(0);
                                }
                                Vibrar(300);
                            }
                        }
                    }
                    handler.postDelayed(runnableRep, tempoExecucao * 1000);
                    txtRep.setText(String.valueOf(r));
                    r++;
                    re = r;
                    if (uni == 1 && sa < 2) { // verifica se é alternado e em qual repetição está
                        r--;
                        sa++;
                    } else {
                        sa = 1;
                    }
                } else { //se a repetição atual for maior que o total
                    if (uni == 2 && tipoRep == 2) { // verifica se é dropset unilateral
                        if (sd < seriesDrop * 2) {
                            sd++;
                            descansar(5);
                        } else {
                            sd = 1;
                            if (s < series) {
                                s++;
                                descansar(descanso);
                            } else {
                                completo = true;
                                verificarProximo();
                                txtTemporizador.setVisibility(View.VISIBLE);
                                txtTemporizador.setTextSize(35);
                                txtTemporizador.setText("Exercício Concluído");
                                atualizarData();
                                swtInicioAut.setChecked(false);
                                if (antprox != 1) {
                                    btnIniciar.setChecked(false);
                                    btnProximo.callOnClick();
                                } else {
                                    btnIniciar.setChecked(false);
                                    onBackPressed();
                                }
                            }
                        }
                        handler.removeCallbacks(runnableRep);
                        re = 0;
                    } else {
                        if (uni == 2 && su < 2) {
                            su++;
                            descansar(2);
                        } else if (tipoRep == 2 && sd < seriesDrop) {
                            sd++;
                            if (txtExercicio.getText().toString().equals("Rosca 21")) {
                                rosca21 = true;
                                descansar(1);
                            } else {
                                descansar(5);
                            }
                        } else {
                            sd = 1;
                            su = 1;
                            if (s < series) {
                                rosca21 = false;
                                s++;
                                descansar(descanso);
                            } else {
                                completo = true;
                                verificarProximo();
                                txtTemporizador.setVisibility(View.VISIBLE);
                                txtTemporizador.setTextSize(35);
                                txtTemporizador.setText("Exercício Concluído");
                                atualizarData();
                                swtInicioAut.setChecked(false);
                                if (antprox != 1) {
                                    btnIniciar.setChecked(false);
                                    btnProximo.callOnClick();
                                } else {
                                    btnIniciar.setChecked(false);
                                    try {
                                        imgExercicio.setVisibility(View.INVISIBLE);
                                        onBackPressed();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                        handler.removeCallbacks(runnableRep);
                        re = 0;
                    }
                }
            }
        };
        handler.post(runnableRep);
    }

    private void descansar(int t) {
        cdTimer = new CountDownTimer((t) * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                long t = (millisUntilFinished / 1000);
                long m = t / 60;
                long s = t % 60;
                if (s > 9) {
                    txtDescanso.setText(m + ":" + s);
                } else {
                    txtDescanso.setText(m + ":0" + s);
                }
                if (m == 1 && s == 0) {
                    contagem(1101);
                }
                if (m == 0 && s == 30) {
                    contagem(1030);
                }
                if (m == 0 && s == 10) {
                    contagem(1010);
                }
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                txtSeries.setText(String.valueOf(s));
                btnIniciar.setChecked(false);
                Descanso();
                if (swtInicioAut.isChecked()) {
                    btnIniciar.setChecked(true);
                } else {
                    if (uni == 2 && tipoRep == 2 && sd >= seriesDrop * 2) {
                        btnIniciar.setChecked(true);
                    }
                    if (uni == 2 && su >= 2) {
                        btnIniciar.setChecked(true);
                    }
                    if (tipoRep == 2 && sd >= seriesDrop) {
                        btnIniciar.setChecked(true);
                    }
                }
            }
        }.start();
    }

    private void contagem(int num) {

        if (vozselecionada == 0) {
            switch (num) {
                case 0:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m0);
                    break;
                case 1:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m1);
                    break;
                case 2:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m2);
                    break;
                case 3:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m3);
                    break;
                case 4:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m4);
                    break;
                case 5:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m5);
                    break;
                case 6:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m6);
                    break;
                case 7:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m7);
                    break;
                case 8:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m8);
                    break;
                case 9:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m9);
                    break;
                case 10:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m10);
                    break;
                case 11:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m11);
                    break;
                case 12:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m12);
                    break;
                case 13:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m13);
                    break;
                case 14:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m14);
                    break;
                case 15:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m15);
                    break;
                case 16:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m16);
                    break;
                case 17:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m17);
                    break;
                case 18:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m18);
                    break;
                case 19:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m19);
                    break;
                case 20:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m20);
                    break;
                case 21:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m21);
                    break;
                case 22:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m22);
                    break;
                case 23:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m23);
                    break;
                case 24:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m24);
                    break;
                case 25:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m25);
                    break;
                case 26:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m26);
                    break;
                case 27:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m27);
                    break;
                case 28:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m28);
                    break;
                case 29:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m29);
                    break;
                case 30:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m30);
                    break;
                case 31:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m31);
                    break;
                case 32:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m32);
                    break;
                case 33:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m33);
                    break;
                case 34:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m34);
                    break;
                case 35:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m35);
                    break;
                case 36:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m36);
                    break;
                case 37:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m37);
                    break;
                case 38:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m38);
                    break;
                case 39:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m39);
                    break;
                case 40:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m40);
                    break;
                case 41:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m41);
                    break;
                case 42:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m42);
                    break;
                case 43:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m43);
                    break;
                case 44:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m44);
                    break;
                case 45:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m45);
                    break;
                case 46:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m46);
                    break;
                case 47:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m47);
                    break;
                case 48:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m48);
                    break;
                case 49:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m49);
                    break;
                case 50:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m50);
                    break;
                case 1010:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m10s);
                    break;
                case 1030:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m30s);
                    break;
                case 1101:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m1m);
                    break;
                case 1110:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m10m);
                    break;
                case 1130:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m30m);
                    break;
                default:
                    break;
            }
        } else {
            switch (num) {
                case 0:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.m0);
                    break;
                case 1:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f1);
                    break;
                case 2:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f2);
                    break;
                case 3:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f3);
                    break;
                case 4:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f4);
                    break;
                case 5:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f5);
                    break;
                case 6:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f6);
                    break;
                case 7:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f7);
                    break;
                case 8:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f8);
                    break;
                case 9:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f9);
                    break;
                case 10:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f10);
                    break;
                case 11:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f11);
                    break;
                case 12:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f12);
                    break;
                case 13:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f13);
                    break;
                case 14:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f14);
                    break;
                case 15:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f15);
                    break;
                case 16:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f16);
                    break;
                case 17:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f17);
                    break;
                case 18:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f18);
                    break;
                case 19:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f19);
                    break;
                case 20:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f20);
                    break;
                case 21:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f21);
                    break;
                case 22:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f22);
                    break;
                case 23:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f23);
                    break;
                case 24:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f24);
                    break;
                case 25:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f25);
                    break;
                case 26:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f26);
                    break;
                case 27:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f27);
                    break;
                case 28:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f28);
                    break;
                case 29:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f29);
                    break;
                case 30:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f30);
                    break;
                case 31:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f31);
                    break;
                case 32:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f32);
                    break;
                case 33:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f33);
                    break;
                case 34:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f34);
                    break;
                case 35:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f35);
                    break;
                case 36:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f36);
                    break;
                case 37:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f37);
                    break;
                case 38:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f38);
                    break;
                case 39:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f39);
                    break;
                case 40:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f40);
                    break;
                case 41:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f41);
                    break;
                case 42:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f42);
                    break;
                case 43:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f43);
                    break;
                case 44:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f44);
                    break;
                case 45:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f45);
                    break;
                case 46:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f46);
                    break;
                case 47:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f47);
                    break;
                case 48:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f48);
                    break;
                case 49:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f49);
                    break;
                case 50:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f50);
                    break;
                case 1010:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f10s);
                    break;
                case 1030:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f30s);
                    break;
                case 1101:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f1m);
                    break;
                case 1110:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f10m);
                    break;
                case 1130:
                    contador = MediaPlayer.create(ExercicioActivity.this, R.raw.f30m);
                    break;
                default:
                    break;
            }
        }
        try {
            contador.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        contador.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                }
            }
        });
    }

    private void largada(int num) {
        switch (num) {
            case 1:
                largada = MediaPlayer.create(ExercicioActivity.this, R.raw.largada1);
                break;
            case 2:
                largada = MediaPlayer.create(ExercicioActivity.this, R.raw.largada2);
                break;
        }
        largada.start();
        largada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                }
            }
        });
    }

    private void bambam(int num) {
        switch (num) {
            case 0:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.birl);
                break;
            case 1:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.bambam1);
                break;
            case 2:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.bambam2);
                break;
            case 3:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.bambam3);
                break;
            case 4:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.bambam4);
                break;
            case 5:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.bambam5);
                break;
            case 6:
                bambam = MediaPlayer.create(ExercicioActivity.this, R.raw.bambam6);
                break;
        }
        bambam.start();
        bambam.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                }
            }
        });
    }

    private void Vibrar(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(milliseconds);
        }
    }

    @SuppressLint("SetTextI18n")
    private void carregarValores(int proximo) {
        SharedPreferences inicioaut = getSharedPreferences(INICIO_AUTOMATICO, ConfiguracoesActivity.MODE_PRIVATE);
        int ini = inicioaut.getInt("InicioAut", 0);
        if (ini == 1) {
            swtInicioAut.setChecked(true);
        } else {
            swtInicioAut.setChecked(false);
        }
        txtTemporizador.setVisibility(View.INVISIBLE);
        txtTemporizador.setText("");

        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + idTreino + " ORDER BY pos ASC", null);
            int indExercicio = cursor.getColumnIndex("exercicio");
            int indSeries = cursor.getColumnIndex("series");
            int indSerieAtual = cursor.getColumnIndex("serieAtual");
            int indTipoRep = cursor.getColumnIndex("tipoRep");
            int indRep1 = cursor.getColumnIndex("rep1");
            int indRep2 = cursor.getColumnIndex("rep2");
            int indRep3 = cursor.getColumnIndex("rep3");
            int indRep4 = cursor.getColumnIndex("rep4");
            int indRep5 = cursor.getColumnIndex("rep5");
            int indRep6 = cursor.getColumnIndex("rep6");
            int indRep7 = cursor.getColumnIndex("rep7");
            int indRep8 = cursor.getColumnIndex("rep8");
            int indTempoExecucao = cursor.getColumnIndex("tempoExecucao");
            int indDescansoM = cursor.getColumnIndex("descansoM");
            int indDescansoS = cursor.getColumnIndex("descansoS");
            int indUnilateral = cursor.getColumnIndex("unilateral");
            int indIdExercicio = cursor.getColumnIndex("idExercicio");
            int indObs = cursor.getColumnIndex("obs");
            int indMusculoSpinner = cursor.getColumnIndex("musculoSpinner");
            int indExercicioSpinner = cursor.getColumnIndex("exercicioSpinner");
            int indCompleto = cursor.getColumnIndex("completo");
            boolean parar = false;
            while (!parar && cursor.moveToNext()) {
                if (cursor.getInt(indIdExercicio) == id) {
                    parar = true;
                    if (proximo == 1) {
                        cursor.moveToNext();
                        if (cursor.isLast()) {
                            antprox = 1;
                        } else {
                            antprox = 0;
                        }
                    } else if (proximo == 2) {
                        cursor.moveToPrevious();
                        if (cursor.isFirst()) {
                            antprox = 2;
                        } else {
                            antprox = 0;
                        }
                    } else {
                        if (cursor.isFirst()) {
                            antprox = 2;
                        } else if (cursor.isLast()) {
                            antprox = 1;
                        } else {
                            antprox = 0;
                        }
                    }
                }
            }
            id = cursor.getInt(indIdExercicio);
            txtExercicio.setText(cursor.getString(indExercicio));
            if (cursor.getString(indObs).equals("")) {
                txtObs.setVisibility(View.INVISIBLE);
                txtObs.setText("");
            } else {
                txtObs.setVisibility(View.VISIBLE);
                txtObs.setText("Obs: " + cursor.getString(indObs));
            }
            repInd[0] = Integer.parseInt(cursor.getString(indRep1));
            repInd[1] = Integer.parseInt(cursor.getString(indRep2));
            repInd[2] = Integer.parseInt(cursor.getString(indRep3));
            repInd[3] = Integer.parseInt(cursor.getString(indRep4));
            repInd[4] = Integer.parseInt(cursor.getString(indRep5));
            repInd[5] = Integer.parseInt(cursor.getString(indRep6));
            repInd[6] = Integer.parseInt(cursor.getString(indRep7));
            repInd[7] = Integer.parseInt(cursor.getString(indRep8));
            series = Integer.parseInt(cursor.getString(indSeries));
            s = cursor.getInt(indSerieAtual);
            completo = cursor.getInt(indCompleto) != 0;
            tipoRep = Integer.parseInt(cursor.getString(indTipoRep));
            for (int i = 0; i < series; i++) {
                if (tipoRep == 0) {
                    rep[i] = Integer.parseInt(cursor.getString(indRep1));
                    repEsq[i] = rep[i];
                } else if (tipoRep == 1) {
                    rep[i] = repInd[i];
                } else if (tipoRep == 3) {
                    rep[i] = 50;
                    repEsq[i] = rep[i];
                } else if (tipoRep == 2) {
                    rep[i] = cursor.getInt(indRep1);
                    seriesDrop = cursor.getInt(indRep5);
                }
            }
            tempoExecucao = cursor.getInt(indTempoExecucao);
            tempoExecucaoInicial = cursor.getInt(indTempoExecucao);
            descanso = (cursor.getInt(indDescansoM) * 60) + cursor.getInt(indDescansoS);
            uni = cursor.getInt(indUnilateral);
            switch (uni) {
                case 0:
                    txtUnilateral.setText("");
                    break;
                case 1:
                    txtUnilateral.setText("Alternado");
                    break;
                case 2:
                    txtUnilateral.setText("Unilateral");
                    break;
            }
            e = cursor.getInt(indExercicioSpinner);
            m = cursor.getInt(indMusculoSpinner);

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tipoRep == 3) {
            txtRep.setText("Até a falha");
        } else {
            txtRep.setText(String.valueOf(rep[0]));
        }
        txtSeries.setText(String.valueOf(s));
        txtSeriesTotal.setText(String.valueOf(series));
        txtTempoExecucao.setText(String.valueOf(tempoExecucao));
        iniciando = 1;
        re = 0;
        Descanso();
    }

    public void verificarProximo() {
        Cursor cursor = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + idTreino + " ORDER BY pos ASC", null);
        int indIdExercicio = cursor.getColumnIndex("idExercicio");
        boolean parar = false;
        while (cursor.moveToNext() && !parar) {
            if (cursor.getInt(indIdExercicio) == id) {
                parar = true;
                if (cursor.isLast()) {
                    antprox = 1;
                } else {
                    antprox = 0;
                }
            }
        }
        cursor.close();
    }

    void escolherImagem() {
        int M = m;
        int E = e;
        switch (M) {
            case 0:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m0e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m0e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m0e2);
                        break;
                }
                break;
            case 1:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m1e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m1e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m1e2);
                        break;
                    case 3:
                        imgExercicio.setImageResource(R.drawable.m1e3);
                        break;
                    case 4:
                        imgExercicio.setImageResource(R.drawable.m1e4);
                        break;
                    case 5:
                        imgExercicio.setImageResource(R.drawable.m1e5);
                        break;
                    case 6:
                        imgExercicio.setImageResource(R.drawable.m1e6);
                        break;
                    case 7:
                        imgExercicio.setImageResource(R.drawable.m1e7);
                        break;
                    case 8:
                        imgExercicio.setImageResource(R.drawable.m1e8);
                        break;
                    case 9:
                        imgExercicio.setImageResource(R.drawable.m1e9);
                        break;
                }
                break;
            case 2:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m2e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m2e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m2e2);
                        break;
                    case 3:
                        imgExercicio.setImageResource(R.drawable.m2e3);
                        break;
                    case 4:
                        imgExercicio.setImageResource(R.drawable.m2e4);
                        break;
                    case 5:
                        imgExercicio.setImageResource(R.drawable.m2e5);
                        break;
                    case 6:
                        imgExercicio.setImageResource(R.drawable.m2e6);
                        break;
                    case 7:
                        imgExercicio.setImageResource(R.drawable.m2e7);
                        break;
                    case 8:
                        imgExercicio.setImageResource(R.drawable.m2e8);
                        break;
                    case 9:
                        imgExercicio.setImageResource(R.drawable.m2e9);
                        break;
                    case 10:
                        imgExercicio.setImageResource(R.drawable.m2e10);
                        break;
                    case 11:
                        imgExercicio.setImageResource(R.drawable.m2e11);
                        break;
                    case 12:
                        imgExercicio.setImageResource(R.drawable.m2e12);
                        break;
                    case 13:
                        imgExercicio.setImageResource(R.drawable.m2e13);
                        break;
                    case 14:
                        imgExercicio.setImageResource(R.drawable.m2e14);
                        break;
                    case 15:
                        imgExercicio.setImageResource(R.drawable.m2e15);
                        break;
                }
                break;

            case 3:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m3e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m3e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m3e2);
                        break;
                    case 3:
                        imgExercicio.setImageResource(R.drawable.m3e3);
                        break;
                    case 4:
                        imgExercicio.setImageResource(R.drawable.m3e4);
                        break;
                    case 5:
                        imgExercicio.setImageResource(R.drawable.m3e5);
                        break;
                    case 6:
                        imgExercicio.setImageResource(R.drawable.m3e6);
                        break;
                    case 7:
                        imgExercicio.setImageResource(R.drawable.m3e7);
                        break;
                    case 8:
                        imgExercicio.setImageResource(R.drawable.m3e8);
                        break;
                    case 9:
                        imgExercicio.setImageResource(R.drawable.m3e9);
                        break;
                    case 10:
                        imgExercicio.setImageResource(R.drawable.m3e10);
                        break;
                    case 11:
                        imgExercicio.setImageResource(R.drawable.m3e11);
                        break;
                    case 12:
                        imgExercicio.setImageResource(R.drawable.m3e12);
                        break;
                }
                break;

            case 4:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m4e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m4e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m4e2);
                        break;
                    case 3:
                        imgExercicio.setImageResource(R.drawable.m4e3);
                        break;
                    case 4:
                        imgExercicio.setImageResource(R.drawable.m4e4);
                        break;
                    case 5:
                        imgExercicio.setImageResource(R.drawable.m4e5);
                        break;
                    case 6:
                        imgExercicio.setImageResource(R.drawable.m4e6);
                        break;
                    case 7:
                        imgExercicio.setImageResource(R.drawable.m4e7);
                        break;
                    case 8:
                        imgExercicio.setImageResource(R.drawable.m4e8);
                        break;
                    case 9:
                        imgExercicio.setImageResource(R.drawable.m4e9);
                        break;
                    case 10:
                        imgExercicio.setImageResource(R.drawable.m4e10);
                        break;
                    case 11:
                        imgExercicio.setImageResource(R.drawable.m4e11);
                        break;
                    case 12:
                        imgExercicio.setImageResource(R.drawable.m4e12);
                        break;
                    case 13:
                        imgExercicio.setImageResource(R.drawable.m4e13);
                        break;
                    case 14:
                        imgExercicio.setImageResource(R.drawable.m4e14);
                        break;
                    case 15:
                        imgExercicio.setImageResource(R.drawable.m4e15);
                        break;
                    case 16:
                        imgExercicio.setImageResource(R.drawable.m4e16);
                        break;
                    case 17:
                        imgExercicio.setImageResource(R.drawable.m4e17);
                        break;
                    case 18:
                        imgExercicio.setImageResource(R.drawable.m4e18);
                        break;
                }
                break;
            case 5:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m5e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m5e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m5e2);
                        break;
                    case 3:
                        imgExercicio.setImageResource(R.drawable.m5e3);
                        break;
                    case 4:
                        imgExercicio.setImageResource(R.drawable.m5e4);
                        break;
                    case 5:
                        imgExercicio.setImageResource(R.drawable.m5e5);
                        break;
                    case 6:
                        imgExercicio.setImageResource(R.drawable.m5e6);
                        break;
                    case 7:
                        imgExercicio.setImageResource(R.drawable.m5e7);
                        break;
                    case 8:
                        imgExercicio.setImageResource(R.drawable.m5e8);
                        break;
                    case 9:
                        imgExercicio.setImageResource(R.drawable.m5e9);
                        break;
                }
                break;
            case 6:
                switch (E) {
                    case 0:
                        imgExercicio.setImageResource(R.drawable.m6e0);
                        break;
                    case 1:
                        imgExercicio.setImageResource(R.drawable.m6e1);
                        break;
                    case 2:
                        imgExercicio.setImageResource(R.drawable.m6e2);
                        break;
                    case 3:
                        imgExercicio.setImageResource(R.drawable.m6e3);
                        break;
                    case 4:
                        imgExercicio.setImageResource(R.drawable.m6e4);
                        break;
                    case 5:
                        imgExercicio.setImageResource(R.drawable.m6e5);
                        break;
                    case 6:
                        imgExercicio.setImageResource(R.drawable.m6e6);
                        break;
                    case 7:
                        imgExercicio.setImageResource(R.drawable.m6e7);
                        break;
                    case 8:
                        imgExercicio.setImageResource(R.drawable.m6e8);
                        break;
                    case 9:
                        imgExercicio.setImageResource(R.drawable.m6e9);
                        break;
                    case 10:
                        imgExercicio.setImageResource(R.drawable.m6e10);
                        break;
                    case 11:
                        imgExercicio.setImageResource(R.drawable.m6e11);
                        break;
                }
                break;
        }
    }

    private void atualizarData() {
        SharedPreferences data = getSharedPreferences(DATA, 0);
        SharedPreferences.Editor editorData = data.edit();
        editorData.putLong("Data", System.currentTimeMillis());
        editorData.apply();
    }

    //FIM DOS MÉTODOS CRIADOS PARA A CLASSE

}



