package appersonal.development.com.appersonaltrainer.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import appersonal.development.com.appersonaltrainer.Controller.MyLocation;
import appersonal.development.com.appersonaltrainer.R;

public class AerobicoActivity extends AppCompatActivity{

    //Shared Preferences
    private static final String VOZ = "Voz";
    private static final String BOTAOFONE = "BotaoFone";
    private static final String SONS = "Sons";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    //Componentes de tela
    private ToggleButton btnIniciar;
    private Button btnAnterior;
    private Button btnProximo;
    private Button btnFinalizar;
    private LinearLayout layoutBotoes;
    private TextView txtAerobico;
    private TextView txtDuracao;
    private TextView txtDur;
    private TextView txtSeries;
    private TextView txtSeriesTotal;
    private TextView txtSer;
    private TextView txtDescanso;
    private TextView txtDesca;
    private TextView txtTemporizador;
    private TextView txtBarra;
    private TextView txtAviso;
    private TextView txtObs;
    private Switch swtInicioAut;
    private Switch swtContarKM;
    private AlertDialog alerta;
    private Location atualLocation;
    ShareDialog shareDialog = new ShareDialog(this);

    //Variáveis
    private int tipoAe;
    private int series;
    private int duracao;
    private int descanso;
    private int id;
    private int idTreino;
    private int s = 1;
    private int temporizador;
    private int preparo = 0;
    private int antprox;
    private int distancia;
    private long tempo = 0;
    private long data = 0;
    private Double km;
    private Double distpercorrida = 0.0;
    private boolean completo = false;
    private int pausado;
    private int som;
    private int bf;
    private int vozselecionada;
    private boolean corrida = false;
    private String nome;

    //Players de áudio
    private MediaPlayer largada;
    private MediaPlayer bambam;
    private MediaPlayer contador;

    //Thread
    private Handler handlerLocation = new Handler();
    private Runnable runLocation;
    private Handler handler = new Handler();
    private Runnable runnablePause;
    private CountDownTimer cdTimer;

    //Banco de Dados
    private SQLiteDatabase bancoDados;

    //Botão voltar
    @Override
    public void onBackPressed() {
        //Verifica se o aeróbico está completo
        //se estiver incompleto, abre um AlertDialog perguntando se deseja cancelar o exercício sem terminar
        if (completo == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AerobicoActivity.this);
            builder.setTitle("Aeróbico Incompleto");
            builder.setMessage("O exercício será zerado, deseja mesmo cancelar o exercício?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //para o aeróbico, para todos os áudios e fecha a tela
                    btnIniciar.setChecked(false);
                    handler.removeCallbacks(runnablePause);
                    if (largada != null)
                        largada.release();
//                    if (bambam !=null)
//                        bambam.release();
                    if (contador != null)
                        contador.release();
                    AerobicoActivity.this.finish();
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //fecha o AlertDialog
                    dialog.cancel();
                }
            });
            alerta = builder.create();
            alerta.show();

        }
        //se estiver completo, ele deixa voltar para a tela anterior e salva como completo
        else {
            if (distancia == 2){
                AlertDialog.Builder builder = new AlertDialog.Builder(AerobicoActivity.this);
                builder.setTitle("Cancelar corrida");
                builder.setMessage("Deseja cancelar a corrida?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //para o aeróbico, para todos os áudios e fecha a tela
                        btnIniciar.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        if (largada != null)
                            largada.release();
                        if (contador != null)
                            contador.release();
                        AerobicoActivity.this.finish();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fecha o AlertDialog
                        dialog.cancel();
                    }
                });
                alerta = builder.create();
                alerta.show();
            } else {
                bancoDados.execSQL("UPDATE aerobicos SET completo = 1 WHERE idAerobico =" + id);
                //para o aeróbico, para todos os áudios e fecha a tela
                handler.removeCallbacks(runnablePause);
                swtInicioAut.setChecked(false);
                btnIniciar.setChecked(false);
                if (largada != null)
                    largada.release();
//            if (bambam !=null)
//                bambam.release();
                if (contador != null)
                    contador.release();
                super.onBackPressed();
            }
        }
    }

    //cria o botão no canto superior esquerdo para voltar
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
        setContentView(R.layout.activity_aerobico);

        //configura a Activity para controlar o volume de mídia
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Implementa o botão voltar na ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurações da tela

        //Conecta com o banco
        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Recebe valores da Activity TreinoX para identificar qual aeróbico deve carregar
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getInt("idAerobico");
            try{
                distancia = extra.getInt("distancia");
                tipoAe = extra.getInt("tipoAe");
                nome = extra.getString("nome");
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if (nome == null)
            corrida = false;

        //Implementa o ad na activity
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .build();
        adView.loadAd(adRequest);

        //Encontra componentes declarados na tela
        btnIniciar = (ToggleButton) findViewById(R.id.btnIniciar);
        btnAnterior = (Button) findViewById(R.id.btnAnterior);
        btnProximo = (Button) findViewById(R.id.btnProximo);
        btnFinalizar = (Button) findViewById(R.id.btnFinalizar);
        txtDuracao = (TextView) findViewById(R.id.txtDuracao);
        txtDur = (TextView) findViewById(R.id.txtDur);
        txtSeries = (TextView) findViewById(R.id.txtSeries);
        txtSeriesTotal = (TextView) findViewById(R.id.txtSeriesTotal);
        txtDescanso = (TextView) findViewById(R.id.txtDescanso);
        txtSer = (TextView) findViewById(R.id.txtSer);
        txtDesca = (TextView) findViewById(R.id.txtDesca);
        txtTemporizador = (TextView) findViewById(R.id.txtTemporizador);
        txtAerobico = (TextView) findViewById(R.id.txtAerobico);
        txtBarra = (TextView) findViewById(R.id.txtBarra);
        txtAviso = (TextView) findViewById(R.id.txtAviso);
        txtObs = (TextView) findViewById(R.id.txtObs);
        swtInicioAut = (Switch) findViewById(R.id.swtInicioAut);
        swtContarKM = (Switch) findViewById(R.id.swtContarKM);
        layoutBotoes = (LinearLayout) findViewById(R.id.layoutBotoes);


        //usa o shared preferences para acessar as configurações de voz e botão do fone
        SharedPreferences voz = getSharedPreferences(VOZ, ConfiguracoesActivity.MODE_PRIVATE);
        vozselecionada = voz.getInt("Voz", 0);

        SharedPreferences botaofone = getSharedPreferences(BOTAOFONE, ConfiguracoesActivity.MODE_PRIVATE);
        SharedPreferences sons = getSharedPreferences(SONS, ConfiguracoesActivity.MODE_PRIVATE);
        som = sons.getInt("Sons", 0);
        bf = botaofone.getInt("BotaoFone", 0);

        //Chama o método carregarValores()
        if(!corrida) {
            carregarValores(0);
        } else {
            carregarText();
        }

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnIniciar.isChecked()){
                    btnIniciar.setChecked(false);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AerobicoActivity.this);
                builder.setTitle("Encerrando a corrida");
                builder.setMessage("Deseja salvar sua corrida? Clique fora ou pressione voltar para continuar a corrida");
                builder.setNeutralButton("Salvar e compartilhar corrida com os amigos", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kmS = txtDescanso.getText().toString().replaceAll("KM","");
                        kmS = kmS.replaceAll(",",".");
                        Double km = Double.parseDouble(kmS);
                        int hora = Integer.parseInt(txtSeries.getText().toString().substring(0, 1));
                        int min = Integer.parseInt(txtSeries.getText().toString().substring(2, 4));
                        int seg = Integer.parseInt(txtSeries.getText().toString().substring(5, 7));
                        int tempoA = (hora*3600) + (min*60) + seg;
                        Double kmph;
                        if (km < 0.01){
                            kmph = 0.0;
                        } else {
                            kmph = ((km * 1000) / tempoA);
                        }
                        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                                .putString("og:type", "fitness.course")
                                .putString("og:title", "Corrida")
                                .putString("og:description", "Acabei de correr com o")
                                .putInt("fitness:duration:value", tempoA)
                                .putString("fitness:duration:units", "s")
                                .putDouble("fitness:distance:value", km )
                                .putString("fitness:distance:units", "km")
                                .putDouble("fitness:speed:value", kmph)
                                .putString("fitness:speed:units", "m/s")
                                .build();
                        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                                .setActionType("fitness.runs")
                                .putObject("fitness:course", object)
                                .build();
                        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                                .setPreviewPropertyName("fitness:course")
                                .setAction(action)
                                .build();

//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("http://www.facebook.com/appersonaltrainer1"))
//                        .build();
                        shareDialog.show(content);
                        String nome = txtAerobico.getText().toString();
                        String Skm = txtDescanso.getText().toString();
                        String Stempo = txtSeries.getText().toString();
                        bancoDados.execSQL("INSERT INTO historicokm (nomeCorrida, km, tempo, data) VALUES" +
                                " ('"+nome+"', '"+Skm+"', '"+Stempo+"', "+data+")");
                        //para o aeróbico, para todos os áudios e fecha a tela
                        handler.removeCallbacks(runnablePause);
                        swtInicioAut.setChecked(false);
                        btnIniciar.setChecked(false);
                        if (largada != null)
                            largada.release();
                        if (contador != null)
                            contador.release();
                        finish();
                        Toast.makeText(AerobicoActivity.this, "Salvo no histórico de corridas!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AerobicoActivity.this, "Você ainda pode compartilhar sua corrida" +
                                " clicando nela no histórico de corridas", Toast.LENGTH_LONG).show();
                        String nome = txtAerobico.getText().toString();
                        String km = txtDescanso.getText().toString();
                        String tempoS = txtSeries.getText().toString();
                        bancoDados.execSQL("INSERT INTO historicokm (nomeCorrida, km, tempo, data) VALUES" +
                                " ('"+nome+"', '"+km+"', '"+tempoS+"', "+data+")");
                        //para o aeróbico, para todos os áudios e fecha a tela
                        handler.removeCallbacks(runnablePause);
                        swtInicioAut.setChecked(false);
                        btnIniciar.setChecked(false);
                        if (largada != null)
                            largada.release();
                        if (contador != null)
                            contador.release();
                        finish();
                        Toast.makeText(AerobicoActivity.this, "Salvo no histórico de corridas!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Não salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //fecha o AlertDialog
                        //para o aeróbico, para todos os áudios e fecha a tela
                        btnIniciar.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        if (largada != null)
                            largada.release();
                        if (contador != null)
                            contador.release();
                        finish();
                    }
                });

                alerta = builder.create();
                alerta.show();
            }
        });

        //Ação ao alterar o estado do Botão Iniciar
        btnIniciar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Se for alterado para Checked
                if (isChecked) {
                    if (swtContarKM.isChecked()){
                        if (ContextCompat.checkSelfPermission(AerobicoActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            VerificarPermissao();
                            btnIniciar.setChecked(false);
                            Toast.makeText(AerobicoActivity.this, "Permita que o aplicativo acesse a localização para continuar. " +
                                    "Caso negue, será necessário permitir em Configurações > Aplicativos > APPersonal Trainer > Permissões", Toast.LENGTH_LONG).show();
                        } else {
                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            if (!GPSEnabled && swtContarKM.isChecked()) {
                                Toast.makeText(AerobicoActivity.this, "Ative a localização para continuar", Toast.LENGTH_SHORT).show();
                                btnIniciar.setChecked(false);
                            } else {
                                handler.removeCallbacks(runnablePause);
                                executarAerobico();
                            }
                        }
                    } else {
                        //Inicia o temporizador com 3
                        temporizador = 3;
                            //Inicia uma Thread para fazer a contagem antes de iniciar o exercício
                            runnablePause = new Runnable() {
                                @Override
                                public void run() {
                                    //Se o preparo for igual a 0, altera o txtTemporizador para mostrar "Preparar" por 2 segundos
                                    if (temporizador == 3 && preparo == 0) {
                                        txtTemporizador.setTextSize(48);
                                        txtTemporizador.setText("Preparar!");
                                        handler.postDelayed(runnablePause, 2000);
                                        //altera o preparo para 1 para não passar novamente aqui
                                        preparo = 1;
                                    }
                                    // a cada 1 segundo ele altera o valor de temporizador e mostra na tela e executa o som da contagem
                                    else if (temporizador > 0) {
                                        txtTemporizador.setTextSize(60);
                                        handler.postDelayed(runnablePause, 1000);
                                        txtTemporizador.setText("" + temporizador);
                                        contagem(temporizador);
                                        temporizador--;
                                    }
                                    //quando chega a 0, ele tira o temporizador da tela, executa o som de início, para a thread e executa o exercício
                                    else {
                                        handler.removeCallbacks(runnablePause);
                                        txtTemporizador.setText("");
                                        executarAerobico();
                                        if (som == 0) {
                                            largada(2);
                                        } else {
                                            bambam(1);
                                        }
                                    }
                                }
                            };
                        //faz a thread executar repetidamente
                        handler.post(runnablePause);
                        temporizador = 3;
                    }
                }
                //Se o Checked for alterado para false
                else {
                    //para a Thread
                    handler.removeCallbacks(runnablePause);
                    handlerLocation.removeCallbacks(runLocation);
                    swtContarKM.setEnabled(true);
                    //se a contagem regressiva estiver executando, ele para
                    if (cdTimer != null) {
                        cdTimer.cancel();
                        //Se o tipo de exercício existir séries, executa o método Descanso()
                        if (tipoAe == 1) {
                            Descanso();
                        }
                    }

                    if (distancia == 1){
                        completo = true;
                    }

                }
            }
        });

        //Ação ao pressionar o botão próximo
        btnProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se o antprox for igual a um, significa que é o último aeróbico, ele faz a alteração quando carrega um novo aeróbico
                if (antprox == 1){
                    Toast.makeText(getApplicationContext(), "Último exercício", Toast.LENGTH_SHORT).show();
                } else {
                    //Se o exercício estiver incompleto, abre um AlertDialog perguntando se quer mudar para o próximo exercício sem salvar como completo
                    if (completo == false) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AerobicoActivity.this);
                        builder.setTitle("Exercício Incompleto");
                        builder.setMessage("Caso mude, o exercício será zerado, deseja mesmo mudar para o próximo?");
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Para o aeróbico e carrega o próximo
                                btnIniciar.setChecked(false);
                                handler.removeCallbacks(runnablePause);
                                carregarValores(1);
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
                        bancoDados.execSQL("UPDATE aerobicos SET completo = 1 WHERE idAerobico ="+id);
                        btnIniciar.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        carregarValores(1);
                    }
                }

            }
        });

        //função do botão Anterior
        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se for o primeiro aeróbico, ele apenas informa que é o primeiro
                if (antprox == 2){
                    Toast.makeText(getApplicationContext(), "Primeiro aeróbico", Toast.LENGTH_SHORT).show();
                }
                //se não for e não estiver completo, ele pergunta se deseja mesmo cancelar antes de mudar para o anterior
                else {
                    if (completo == false) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AerobicoActivity.this);
                        builder.setTitle("Exercício Incompleto");
                        builder.setMessage("Caso mude, o exercício será zerado, deseja mesmo mudar para o anterior?");
                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnIniciar.setChecked(false);
                                handler.removeCallbacks(runnablePause);
                                carregarValores(2);
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
                    }
                    //se estiver completo, ele salva e muda para o próximo sem perguntar
                    else {
                        bancoDados.execSQL("UPDATE aerobicos SET completo = 1 WHERE idAerobico ="+id);
                        btnIniciar.setChecked(false);
                        handler.removeCallbacks(runnablePause);
                        carregarValores(2);
                    }
                }
            }
        });

        swtContarKM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (!verificaConexao()) {
                        Toast.makeText(AerobicoActivity.this, "Os resultados dessa função não são garantidos sem o uso dos Dados Móveis" +
                                ". Por favor, ative-os.", Toast.LENGTH_LONG).show();
                    }
                    txtDesca.setVisibility(View.VISIBLE);
                    txtDesca.setText("Distância aproximada:");
                    txtDescanso.setVisibility(View.VISIBLE);
                    txtDescanso.setText("0,00 KM");
                    txtAviso.setVisibility(View.VISIBLE);
                    ativarGPS();
                } else {
                    txtDesca.setVisibility(View.INVISIBLE);
                    txtDesca.setText("Descanso:");
                    txtDescanso.setVisibility(View.INVISIBLE);
                    txtDescanso.setText("");
                    txtAviso.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //método para editar o campo duração de acordo com o tipo de exercício
    private void Duracao() {
        long h = duracao / 3600;
        long m = duracao / 60;
        long s = duracao % 60;

        //se o tipo de aeróbico não tiver séries, ele pode precisar da contagem de horas
        if (tipoAe == 0) {
            if (m > 9 && s > 9) {
                if (m >= 60) {
                    txtDuracao.setText(h + ":00:" + s);
                } else {
                    txtDuracao.setText(h + ":" + m + ":" + s);
                }
            } else {
                if (m < 10 && s > 9)
                    txtDuracao.setText(h + ":0" + m + ":" + s);
                else if (m > 9 && s < 10)
                    if (m >= 60) {
                        txtDuracao.setText(h + ":00:0" + s);
                    } else {
                        txtDuracao.setText(h + ":" + m + ":0" + s);
                    }
                else
                    txtDuracao.setText(h + ":0" + m + ":0" + s);
            }
        }
        //se for com séries, ele precisa apenas da contagem de minutos e segundos
        else {
            if (s > 9) {
                txtDuracao.setText(m + ":" + s);
            } else {
                txtDuracao.setText(m + ":0" + s);
            }
        }
    }

    //método para editar o campo do tempo de descanso
    private void Descanso() {
        long m = descanso / 60;
        long s = descanso % 60;
        if (s > 9) {
            txtDescanso.setText(m + ":" + s);
        } else {
            txtDescanso.setText(m + ":0" + s);
        }
    }

    //executando o aeróbico
    private void executarAerobico() {
        if (data == 0){
            data = System.currentTimeMillis();
        }
        txtSeries.setText("" + s);
        //se séries for maior que 0, significa que é um exercício com series
        if (series > 0){
            //inicia a contagem regressiva do tempo do aeróbico
            cdTimer = new CountDownTimer((duracao+1) * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long t = (millisUntilFinished / 1000) - 1;
                    long m = t / 60;
                    long s = t % 60;
                    if (m == 1 && s == 0) {
                        if (vozselecionada == 0) {
                            contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m1m);
                        } else {
                            contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f1m);
                        }
                        contador.start();
                    }
                    if (m == 0 && s == 30) {
                        if (vozselecionada == 0) {
                            contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m30s);
                        } else {
                            contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f30s);
                        }
                        contador.start();
                    }
                    if (m == 0 && s == 10) {
                        if (vozselecionada == 0) {
                            contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m10s);
                        } else {
                            contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f10s);
                        }
                        contador.start();
                    }
                    if (s > 9) {
                        txtDuracao.setText(m + ":" + s);
                    } else {
                        txtDuracao.setText(m + ":0" + s);
                    }

                }
                public void onFinish() {
                    //ao terminar, ele verifica se a série atual é menor que a quantidade de séries
                    //se for menor, ele executa o fim de série
                    if (s < series) {
                        Vibrar(300);
                        Duracao();
                        if (som == 0) {
                            largada(1);
                        } else {
                            bambam(0);
                        }
                        timer(descanso+1);
                        s++;
                    }
                    //se não for, ele executa o fim do aeróbico
                    else {
                        Vibrar(700);
                        if (bambam != null)
                        bambam.release();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (som == 0) {
                                    largada(2);
                                } else {
                                    Random al = new Random();
                                    int i = al.nextInt(4) + 2;
                                    bambam(i);
                                }
                            }
                        }, 300);
                        Duracao();
                        swtInicioAut.setChecked(false);
                        timer(descanso+1);
                        completo = true;
                        btnIniciar.setChecked(false);
                        txtTemporizador.setTextSize(35);
                        txtTemporizador.setText("Exercício Concluído");
                    }
                }
            }.start();
        }
        //se não tiver séries, então ele executa esse tipo de aeróbico
        else {
            //aqui ele verifica se o aeróbico é medido por distância ou tempo
            //se distância for igual a 0, então é medido por tempo
            if (distancia==0) {
                if (pausado != 0) {
                    duracao = pausado;
                }
                //inicia uma contagem regressiva com o tempo do aeróbico
                cdTimer = new CountDownTimer((duracao + 1) * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        long t = (millisUntilFinished / 1000);
                        long h = t / 3600;
                        long m = t / 60;
                        long s = t % 60;
                        pausado = (int) t;

                        if (h == 0 && m == 30 && s == 0) {
                            if (vozselecionada == 0) {
                                contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m30m);
                            } else {
                                contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f30m);
                            }
                            contador.start();
                        }
                        if (h == 0 && m == 10 && s == 0) {
                            if (vozselecionada == 0) {
                                contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m10m);
                            } else {
                                contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f10m);
                            }
                            contador.start();
                        }


                        if (m > 9 && s > 9) {
                            if (m >= 60) {
                                txtDuracao.setText(h + ":00:" + s);
                            } else {
                                txtDuracao.setText(h + ":" + m + ":" + s);
                            }
                        } else {
                            if (m < 10 && s > 9)
                                txtDuracao.setText(h + ":0" + m + ":" + s);
                            else if (m > 9 && s < 10)
                                if (m >= 60) {
                                    txtDuracao.setText(h + ":00:0" + s);
                                } else {
                                    txtDuracao.setText(h + ":" + m + ":0" + s);
                                }
                            else
                                txtDuracao.setText(h + ":0" + m + ":0" + s);
                        }

                    }

                    public void onFinish() {
                        if (bambam != null)
                            bambam.release();
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (som == 0) {
                                    largada(2);
                                } else {
                                    Random al = new Random();
                                    int i = al.nextInt(4) + 2;
                                    bambam(i);
                                }
                            }
                        }, 300);
                        Vibrar(700);
                        Duracao();
                        pausado = 0;
                        completo = true;
                        swtInicioAut.setChecked(false);
                        btnIniciar.setChecked(false);
                        txtTemporizador.setTextSize(35);
                        txtTemporizador.setText("Exercício Concluído");

                    }
                }.start();
            } else {

                runnablePause = new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(runnablePause, 1000);
                        long h = tempo / 3600;
                        long m = tempo / 60;
                        long s = tempo % 60;
                        if (m > 9 && s > 9) {
                            if (m >= 60) {
                                txtSeries.setText(h + ":00:" + s);
                            } else {
                                txtSeries.setText(h + ":" + m + ":" + s);
                            }
                        } else {
                            if (m < 10 && s > 9)
                                txtSeries.setText(h + ":0" + m + ":" + s);
                            else if (m > 9 && s < 10)
                                if (m >= 60) {
                                    txtSeries.setText(h + ":00:0" + s);
                                } else {
                                    txtSeries.setText(h + ":" + m + ":0" + s);
                                }
                            else
                                txtSeries.setText(h + ":0" + m + ":0" + s);
                        }
                        tempo++;
                    }
                };
                handler.post(runnablePause);

            }

            if (swtContarKM.isChecked()){
                runLocation = new Runnable() {
                    @Override
                    public void run() {
                        localizacao();
                        handlerLocation.postDelayed(this, 15000);
                    }
                };
                handlerLocation.post(runLocation);

            }
        }

    }

    private void contagem(int num) {

        if (vozselecionada == 0) {
            switch (num) {
                case 0:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m0);
                    break;
                case 1:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m1);
                    break;
                case 2:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m2);
                    break;
                case 3:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m3);
                    break;
                default:
                    break;
            }
        } else {
            switch (num) {
                case 0:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m0);
                    break;
                case 1:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f1);
                    break;
                case 2:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f2);
                    break;
                case 3:
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.f3);
                    break;
                default:
                    break;
            }
        }
        contador.start();
        contador.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                    mp = null;
                }
            }
        });
    }

    private void Vibrar(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    private void timer(int time) {
        cdTimer = new CountDownTimer(time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                long t = (millisUntilFinished / 1000) - 1;
                long m = t / 60;
                long s = t % 60;
                if (m == 0 && s == 30) {
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m30);
                    contador.start();
                }
                if (m == 0 && s == 10) {
                    contador = MediaPlayer.create(AerobicoActivity.this, R.raw.m10);
                    contador.start();
                }
                if (s > 9) {
                    txtDescanso.setText(m + ":" + s);
                } else {
                    txtDescanso.setText(m + ":0" + s);
                }
            }
            public void onFinish() {
                btnIniciar.setChecked(false);
                if (swtInicioAut.isChecked()) {
                    btnIniciar.setChecked(true);
                }
                Descanso();
            }
        }.start();
    }

    private void largada(int num) {
        switch (num) {
            case 1:
                largada = MediaPlayer.create(AerobicoActivity.this, R.raw.largada1);
                break;
            case 2:
                largada = MediaPlayer.create(AerobicoActivity.this, R.raw.largada2);
                break;
        }
        largada.start();
        largada.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                    mp = null;
                }
            }
        });
    }

    private void bambam(int num) {
        switch(num){
            case 0:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.birl);
                break;
            case 1:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.bambam1);
                break;
            case 2:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.bambam2);
                break;
            case 3:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.bambam3);
                break;
            case 4:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.bambam4);
                break;
            case 5:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.bambam5);
                break;
            case 6:
                bambam = MediaPlayer.create(AerobicoActivity.this, R.raw.bambam6);
                break;
        }
        bambam.start();
        bambam.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                    mp = null;
                }
            }
        });
    }

    private void carregarValores(int proximo) {
        txtTemporizador.setText("");
        completo = false;
        try {

            if (proximo == 0) {
                Cursor cursor = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idAerobico = " + id, null);
                int indIdTreino = cursor.getColumnIndex("idTreino");
                cursor.moveToFirst();
                idTreino = cursor.getInt(indIdTreino);
            }
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino = " + idTreino + "ORDER BY pos", null);
            int indAerobico = cursor.getColumnIndex("aerobico");
            int indDuracaoH = cursor.getColumnIndex("duracaoH");
            int indDuracaoM = cursor.getColumnIndex("duracaoM");
            int indDuracaoS = cursor.getColumnIndex("duracaoS");
            int indSeries = cursor.getColumnIndex("series");
            int indDescansoM = cursor.getColumnIndex("descansoM");
            int indDescansoS = cursor.getColumnIndex("descansoS");
            int indDistancia = cursor.getColumnIndex("distancia");
            int indKM = cursor.getColumnIndex("km");
            int indIdAerobico = cursor.getColumnIndex("idAerobico");
            int indObs = cursor.getColumnIndex("obs");
            cursor.moveToFirst();
            boolean parar = false;
            while (cursor != null && parar == false) {
                if (cursor.getInt(indIdAerobico) == id) {
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
                } else {
                    cursor.moveToNext();
                }
            }
            id = cursor.getInt(indIdAerobico);
            txtAerobico.setText(cursor.getString(indAerobico));
            if (txtAerobico.getText().equals("Step") || txtAerobico.getText().equals("Corda")){
                tipoAe = 1;
            } else {
                tipoAe = 0;
            }
            if (cursor.getString(indObs).toString().equals("")){
                txtObs.setVisibility(View.INVISIBLE);
                txtObs.setText("");
            } else {
                txtObs.setVisibility(View.VISIBLE);
                txtObs.setText("Obs: "+cursor.getString(indObs).toString());
            }
            duracao = (cursor.getInt(indDuracaoH) * 3600) + (cursor.getInt(indDuracaoM) * 60) + cursor.getInt(indDuracaoS);
            series = Integer.parseInt(cursor.getString(indSeries));
            descanso = (cursor.getInt(indDescansoM) * 60) + cursor.getInt(indDescansoS);
            distancia = cursor.getInt(indDistancia);
            km = cursor.getDouble(indKM);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (distancia==0) {
            Duracao();
        } else {
            txtDuracao.setText(km + " KM");
        }

        carregarText();


    }

    void carregarText(){
        if (tipoAe == 0) {
            txtDesca.setVisibility(View.INVISIBLE);
            txtDescanso.setVisibility(View.INVISIBLE);

            txtBarra.setVisibility(View.INVISIBLE);
            if (distancia==0) {
                txtSer.setVisibility(View.INVISIBLE);
                txtSeries.setVisibility(View.INVISIBLE);
            } else if (distancia == 2) {
                layoutBotoes.setVisibility(View.INVISIBLE);
                btnFinalizar.setVisibility(View.VISIBLE);
                txtAerobico.setText(nome);
                txtSer.setVisibility(View.VISIBLE);
                txtSeries.setVisibility(View.VISIBLE);
                txtSer.setText("Tempo:");
                txtSeries.setText("0:00:00");
                txtDuracao.setVisibility(View.INVISIBLE);
                txtDur.setVisibility(View.INVISIBLE);
                completo = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swtContarKM.setChecked(true);
                    }
                }, 100);

            } else {
                txtDur.setText("Objetivo:");
                txtSer.setVisibility(View.VISIBLE);
                txtSeries.setVisibility(View.VISIBLE);
                txtSer.setText("Tempo:");
                txtSeries.setText("0:00:00");
            }
            swtInicioAut.setChecked(false);
            swtInicioAut.setVisibility(View.INVISIBLE);
        } else {
            txtDesca.setVisibility(View.VISIBLE);
            txtDescanso.setVisibility(View.VISIBLE);
            txtSer.setVisibility(View.VISIBLE);
            txtSeries.setVisibility(View.VISIBLE);
            txtSeries.setText("" + series);
            txtSeriesTotal.setText("" + series);
            Descanso();
            swtInicioAut.setVisibility(View.VISIBLE);
            swtInicioAut.setChecked(true);
        }
    }

    private void ativarGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean  GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!GPSEnabled) {
            Toast.makeText(this, "Ative a Localização para calcular a distância", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public boolean verificaConexao() {
        boolean conectado = false;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    public void localizacao(){
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Usar a localizacao aqui!
                if (atualLocation == null){
                    atualLocation = location;
                    btnFinalizar.setEnabled(true);
                } else {
                    Float a = atualLocation.distanceTo(location);
                    a = a/1000;
                    if (a >= 0.003 && a < 1.0){
                        distpercorrida += (a);
                        String dist = String.format("%.2f", distpercorrida);
                        txtDescanso.setText(dist + " KM");
                        atualLocation = location;
                    }
                }
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);
    }

    void VerificarPermissao(){
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(AerobicoActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(AerobicoActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

}
