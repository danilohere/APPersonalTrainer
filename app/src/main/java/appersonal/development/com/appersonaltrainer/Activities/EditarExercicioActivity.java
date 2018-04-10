package appersonal.development.com.appersonaltrainer.Activities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.R;

public class EditarExercicioActivity extends AppCompatActivity {

    private Button btnConfirmar;
    private Button btnCancelar;
    private Button btnMais;
    private Button btnMenos;
    private Spinner spnMusculo;
    private Spinner spnExercicio;
    private Spinner spnTipoRep;
    private Spinner spnUnilateral;
    private EditText edtSeries;
    private EditText edtRep1;
    private EditText edtRep2;
    private EditText edtRep3;
    private EditText edtRep4;
    private EditText edtRep5;
    private EditText edtRep6;
    private EditText edtRep7;
    private EditText edtRep8;
    private EditText edtTempoExecucao;
    private EditText edtDescansoM;
    private EditText edtDescansoS;
    private EditText edtObs;
    private CheckBox chbObs;
    private TextView txtSeriesDrop;
    private TextView txtRepDrop;
    private TextView txtExercicio;
    private TextView txtMusculo;
    private ImageView imgInfoUnilateral;
    private ImageView imgInfoTipoRep;
    private ImageView imgImagem;
    private ImageView imgExercicio;

    private Runnable runRep;
    private Handler handler = new Handler();
    private SQLiteDatabase bancoDados;
    private Bundle extra;
    private String[] setExercicio = new String[20];
    private int[] valorExercicios = new int[18];

    private boolean ver;
    private boolean altEditRep;
    private boolean vazio = false;
    private int exer;
    private int idTreino;
    private int idExercicio = 500000;
    private int carregado;
    private String exercicio;

    private String[] unilateral = {
            "Simultâneo", "Alternado", "Unilateral"
    };

    private String[] tipoRep = {
            "Tradicional", "Pirâmide", "Dropset", "Falha"
    };

    private String[] musculos = {
            "Abdômen", "Bíceps", "Costas", "Peito", "Pernas", "Ombro", "Tríceps"
    };

    private String[] abdomen = {
            "Abdominal Supra", "Abdominal Infra", "Abdominal Oblíquo"
    };

    private String[] biceps = {
            "Scott", "Rosca Direta", "Rosca Simultânea", "Rosca Alternada", "Rosca Concentrada",
            "Rosca Martelo", "Rosca 21", "Unilateral Cross", "Rosca Spider", "Rosca Direta Halter"
    };

    private String[] costas = {
            "Remada Cavalo", "Remada Unilateral", "Puxada Costas", "Puxada Frente", "Remada Sentada", "Remada Curvada",
            "Puxada Unilateral", "Barra Fixa", "Pull Down", "Frente Supinada", "Frente Triângulo",
            "Remada Aparelho", "Remada Polia", "Crucifíxo Invertido", "Encolhimento Halter", "Encolhimento Barra"
    };

    private String[] peito = {
            "Peck Deck", "Cross Over", "Crucifixo", "Pull Over", "Flying", "Flying Inclinado",
            "Supino Reto", "Supino Inclinado", "Supino Declinado", "Supino Reto Halteres",
            "Flexão", "Supino Guiado", "Voador (Paralela)"
    };

    private String[] pernas = {
            "Adução", "Abdução", "Extensora", "Flexora", "Glúteo Máquina", "Smith",
            "Agachamento", "Agachamento com salto", "Stiff", "Levantamento Terra", "Afundo", "Sumô", "Avanço Lateral",
            "Leg Press", "Gêmeos Sentado", "Gêmeos Simultâneo", "Glúteos Quatro Apoios", "Elevação Pélvica", "Sissy Squat"
    };

    private String[] ombro = {
            "Desenvolvimento Frente", "Desenvolvimento Halteres", "Desenvolvimento Alternado",
            "Desenvolvimento Puxada Atrás", "Elevação Lateral", "Elevação Frontal",
            "Remada Alta", "Arnold Press", "Frontal Alternado", "Posterior Cross"
    };

    private String[] triceps = {
            "Tríceps Testa", "Pulley Tríceps", "Pulley Corda", "Pulley Unilateral", "Tríceps Francês",
            "Banco / Mergulho", "Coice / Kick Back", "Francês Cross", "Tríceps Invertido",
            "Tríceps Supinado", "Flexão Fechada", "Paralela"
    };


    @Override
    protected void onResume() {
        super.onResume();
        extra = getIntent().getExtras();
        if (extra != null) {
            idTreino = extra.getInt("idTreino");
            idExercicio = extra.getInt("idExercicio");
            carregarValores();
        }

    }

    @Override
    public void onBackPressed() {
        if (imgExercicio.getVisibility() == View.VISIBLE){
            imgExercicio.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (edtDescansoS.isFocused()) {
                if (edtDescansoS.getText().length() == 0 && vazio == true) {
                    edtDescansoM.requestFocus();
                    vazio = false;
                } else if (edtDescansoS.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtDescansoM.isFocused()) {
                if (edtDescansoM.getText().length() == 0 && vazio == true) {
                    if (edtRep8.getVisibility() == View.VISIBLE) {
                        edtRep8.requestFocus();
                    } else if (edtRep7.getVisibility() == View.VISIBLE) {
                        edtRep7.requestFocus();
                    } else if (edtRep6.getVisibility() == View.VISIBLE) {
                        edtRep6.requestFocus();
                    } else if (edtRep5.getVisibility() == View.VISIBLE) {
                        edtRep5.requestFocus();
                    } else if (edtRep4.getVisibility() == View.VISIBLE) {
                        edtRep4.requestFocus();
                    } else if (edtRep3.getVisibility() == View.VISIBLE) {
                        edtRep3.requestFocus();
                    } else if (edtRep2.getVisibility() == View.VISIBLE) {
                        edtRep2.requestFocus();
                    } else {
                        edtRep1.requestFocus();
                    }
                    vazio = false;
                } else if (edtDescansoM.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep8.isFocused()) {
                if (edtRep8.getText().length() == 0 && vazio == true) {
                    edtRep7.requestFocus();
                    vazio = false;
                } else if (edtRep8.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep7.isFocused()) {
                if (edtRep7.getText().length() == 0 && vazio == true) {
                    edtRep6.requestFocus();
                    vazio = false;
                } else if (edtRep7.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep6.isFocused()) {
                if (edtRep6.getText().length() == 0 && vazio == true) {
                    edtRep5.requestFocus();
                    vazio = false;
                } else if (edtRep6.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep5.isFocused()) {
                if (edtRep5.getText().length() == 0 && vazio == true) {
                    edtRep4.requestFocus();
                    vazio = false;
                } else if (edtRep5.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep4.isFocused()) {
                if (edtRep4.getText().length() == 0 && vazio == true) {
                    edtRep3.requestFocus();
                    vazio = false;
                } else if (edtRep4.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep3.isFocused()) {
                if (edtRep3.getText().length() == 0 && vazio == true) {
                    edtRep2.requestFocus();
                    vazio = false;
                } else if (edtRep3.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep2.isFocused()) {
                if (edtRep2.getText().length() == 0 && vazio == true) {
                    edtRep1.requestFocus();
                    vazio = false;
                } else if (edtRep2.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            } else if (edtRep1.isFocused()) {
                if (edtRep1.getText().length() == 0 && vazio == true) {
                    edtSeries.requestFocus();
                    vazio = false;
                } else if (edtRep1.getText().length() == 0) {
                    vazio = true;
                } else {
                    vazio = false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_exercicio);

        //Implementa o botão voltar na ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView.loadAd(adRequest);

        btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnMais = (Button) findViewById(R.id.btnMais);
        btnMenos = (Button) findViewById(R.id.btnMenos);
        spnMusculo = (Spinner) findViewById(R.id.spnMusculo);
        spnExercicio = (Spinner) findViewById(R.id.spnExercicio);
        spnTipoRep = (Spinner) findViewById(R.id.spnTipoRep);
        spnUnilateral = (Spinner) findViewById(R.id.spnUnilateral);
        edtSeries = (EditText) findViewById(R.id.edtSeries);
        edtRep1 = (EditText) findViewById(R.id.edtRep1);
        edtRep2 = (EditText) findViewById(R.id.edtRep2);
        edtRep3 = (EditText) findViewById(R.id.edtRep3);
        edtRep4 = (EditText) findViewById(R.id.edtRep4);
        edtRep5 = (EditText) findViewById(R.id.edtRep5);
        edtRep6 = (EditText) findViewById(R.id.edtRep6);
        edtRep7 = (EditText) findViewById(R.id.edtRep7);
        edtRep8 = (EditText) findViewById(R.id.edtRep8);
        edtTempoExecucao = (EditText) findViewById(R.id.edtTempoExecucao);
        edtDescansoM = (EditText) findViewById(R.id.edtDescansoM);
        edtDescansoS = (EditText) findViewById(R.id.edtDescansoS);
        edtObs = (EditText) findViewById(R.id.edtObs);
        imgInfoTipoRep = (ImageView) findViewById(R.id.imgInfoTipoRep);
        imgInfoUnilateral = (ImageView) findViewById(R.id.imgInfoUnilateral);
        imgImagem = (ImageView) findViewById(R.id.imgImagem);
        imgExercicio = (ImageView) findViewById(R.id.imgExercicio);
        txtRepDrop = (TextView) findViewById(R.id.txtRepDrop);
        txtSeriesDrop = (TextView) findViewById(R.id.txtSeriesDrop);
        txtExercicio = (TextView) findViewById(R.id.txtExercicio);
        txtMusculo = (TextView) findViewById(R.id.txtMusculo);
        chbObs = (CheckBox) findViewById(R.id.chbObs);
        edtSeries.setSelectAllOnFocus(true);
        edtRep1.setSelectAllOnFocus(true);
        edtRep2.setSelectAllOnFocus(true);
        edtRep3.setSelectAllOnFocus(true);
        edtRep4.setSelectAllOnFocus(true);
        edtRep5.setSelectAllOnFocus(true);
        edtRep6.setSelectAllOnFocus(true);
        edtRep7.setSelectAllOnFocus(true);
        edtRep8.setSelectAllOnFocus(true);
        edtTempoExecucao.setSelectAllOnFocus(true);
        edtDescansoM.setSelectAllOnFocus(true);
        edtDescansoS.setSelectAllOnFocus(true);

        altEditRep = false;

        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, musculos);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnMusculo.setAdapter(adapter);

        ArrayAdapter<String> adapterUni = new ArrayAdapter<>(this, R.layout.spinner_item, unilateral);
        adapterUni.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnUnilateral.setAdapter(adapterUni);

        spnMusculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spnMusculo.setOnItemSelectedListener(this);
                int codPos = position;
                switch (codPos) {
                    case 0:
                        adapter(abdomen);
                        break;
                    case 1:
                        adapter(biceps);
                        break;
                    case 2:
                        adapter(costas);
                        break;
                    case 3:
                        adapter(peito);
                        break;
                    case 4:
                        adapter(pernas);
                        break;
                    case 5:
                        adapter(ombro);
                        break;
                    case 6:
                        adapter(triceps);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spnExercicio.setClickable(false);
            }
        });

        ArrayAdapter<String> adapterRep = new ArrayAdapter<>(this, R.layout.spinner_item, tipoRep);
        adapterRep.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnTipoRep.setAdapter(adapterRep);

        edtSeries.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtSeries.getText().length() == 2) {

                    if (spnTipoRep.getSelectedItemId() == 1) {
                        selecionaSpinnerRepInd();
                    }
                    if (edtRep1.getVisibility() == View.VISIBLE) {
                        edtRep1.requestFocus();
                    } else {
                        edtTempoExecucao.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtRep1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep1.getText().length() == 2) {
                    if (edtRep2.getVisibility() == View.VISIBLE) {
                        edtRep2.requestFocus();
                    } else {
                        if (spnTipoRep.getSelectedItemId() == 2) {
                            edtRep5.requestFocus();
                        } else {
                            edtDescansoM.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtRep2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep2.getText().length() == 2) {
                    if (edtRep3.getVisibility() == View.VISIBLE) {
                        edtRep3.requestFocus();
                    } else {
                        edtDescansoM.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtRep3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep3.getText().length() == 2) {
                    if (edtRep4.getVisibility() == View.VISIBLE) {
                        edtRep4.requestFocus();
                    } else {
                        edtDescansoM.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtRep4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep4.getText().length() == 2) {
                    if (edtRep5.getVisibility() == View.VISIBLE) {
                        edtRep5.requestFocus();
                    } else {
                        edtDescansoM.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtRep5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep5.getText().length() == 2) {
                    if (edtRep6.getVisibility() == View.VISIBLE) {
                        edtRep6.requestFocus();
                    } else {
                        edtDescansoM.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtRep6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep6.getText().length() == 2) {
                    if (edtRep7.getVisibility() == View.VISIBLE) {
                        edtRep7.requestFocus();
                    } else {
                        edtDescansoM.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtRep7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep7.getText().length() == 2) {
                    if (edtRep8.getVisibility() == View.VISIBLE) {
                        edtRep8.requestFocus();
                    } else {
                        edtDescansoM.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtRep8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtRep8.getText().length() == 2) {
                    edtDescansoM.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtDescansoM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtDescansoM.getText().length() == 1 && !edtDescansoM.getText().toString().equals(" ")) {
                    edtDescansoS.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtDescansoS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtDescansoS.getText().length() == 2) {
                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(edtDescansoS.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        spnExercicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spnMusculo.getSelectedItem().toString()) {
                    case "Abdômen":
                        //SÓ UNILATERAL
                        if (spnExercicio.getSelectedItemId() == 2) {
                            spnUnilateral.setSelection(2);
                            spnUnilateral.setEnabled(false);
                        }
                        //SÓ NORMAL
                        else {
                            spnUnilateral.setSelection(0);
                            spnUnilateral.setEnabled(false);
                        }
                        break;
                    case "Bíceps":
                        //Configuraçao Rosca 21
                        if (spnExercicio.getSelectedItemId() == 6) {
                            edtRep1.setText("7");
                            edtRep5.setText("3");
                            spnTipoRep.setEnabled(false);
                            edtRep5.setEnabled(false);
                        } else {
                            if (carregado != 1) {
                                edtRep1.setText("");
                                edtRep5.setText("");
                            }
                            spnTipoRep.setEnabled(true);
                            edtRep5.setEnabled(true);
                        }
                        //PODE TUDO
                        if (spnExercicio.getSelectedItemId() == 5 || spnExercicio.getSelectedItemId() == 8
                                || spnExercicio.getSelectedItemId() == 9) {
                            spnUnilateral.setEnabled(true);
                        }
//                      //SÓ ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 3) {
                            spnUnilateral.setSelection(1);
                            spnUnilateral.setEnabled(false);
                        }
                        //SÓ UNILATERAL
                        else if (spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 7) {
                            spnUnilateral.setSelection(2);
                            spnUnilateral.setEnabled(false);
                        }
                        //NÃO PODE ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 0) {
                            spnUnilateral.setEnabled(true);
                        }
                        //SÓ NORMAL
                        else {
                            spnUnilateral.setSelection(0);
                            spnUnilateral.setEnabled(false);
                        }
                        break;
                    case "Costas":
                        //SÓ UNILATERAL
                        if (spnExercicio.getSelectedItemId() == 1 || spnExercicio.getSelectedItemId() == 6) {
                            spnUnilateral.setSelection(2);
                            spnUnilateral.setEnabled(false);
                        }
                        //NÃO PODE ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 11 || spnExercicio.getSelectedItemId() == 12) {
                            spnUnilateral.setEnabled(true);
                        }
                        //SÓ NORMAL
                        else {
                            spnUnilateral.setSelection(0);
                            spnUnilateral.setEnabled(false);
                        }
                        break;
                    case "Peito":
                        //SÓ NORMAL
                        spnUnilateral.setSelection(0);
                        spnUnilateral.setEnabled(false);
                        break;
                    case "Pernas":
                        //SÓ UNILATERAL
                        if (spnExercicio.getSelectedItemId() == 16) {
                            spnUnilateral.setSelection(2);
                            spnUnilateral.setEnabled(false);
                        }
                        //NÃO PODE NORMAL
                        else if (spnExercicio.getSelectedItemId() == 10 || spnExercicio.getSelectedItemId() == 12) {
                            spnUnilateral.setSelection(1);
                            spnUnilateral.setEnabled(true);
                        }
                        //NÃO PODE ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 0 || spnExercicio.getSelectedItemId() == 1
                                || spnExercicio.getSelectedItemId() == 2 || spnExercicio.getSelectedItemId() == 3
                                || spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 8
                                || spnExercicio.getSelectedItemId() == 13 || spnExercicio.getSelectedItemId() == 14
                                || spnExercicio.getSelectedItemId() == 18) {
                            spnUnilateral.setEnabled(true);
                        }
                        //SÓ NORMAL
                        else {
                            spnUnilateral.setSelection(0);
                            spnUnilateral.setEnabled(false);
                        }
                        break;
                    case "Ombro":
                        //PODE TUDO
                        if (spnExercicio.getSelectedItemId() == 5) {
                            spnUnilateral.setEnabled(true);
                        }
                        //SÓ ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 2 || spnExercicio.getSelectedItemId() == 8) {
                            spnUnilateral.setSelection(1);
                            spnUnilateral.setEnabled(false);
                        }
                        //NÃO PODE ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 9) {
                            spnUnilateral.setEnabled(true);
                        }
                        //NÃO PODE UNILATERAL
                        else if (spnExercicio.getSelectedItemId() == 7) {
                            spnUnilateral.setEnabled(true);
                        }
                        //SÓ NORMAL
                        else {
                            spnUnilateral.setSelection(0);
                            spnUnilateral.setEnabled(false);
                        }
                        break;
                    case "Tríceps":
                        //SÓ PODE UNILATERAL
                        if (spnExercicio.getSelectedItemId() == 3) {
                            spnUnilateral.setSelection(2);
                            spnUnilateral.setEnabled(false);
                        }
                        //NÃO PODE ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 8 || spnExercicio.getSelectedItemId() == 4
                                || spnExercicio.getSelectedItemId() == 6) {
                            spnUnilateral.setEnabled(true);
                        }
                        //SÓ PODE NORMAL
                        else {
                            spnUnilateral.setSelection(0);
                            spnUnilateral.setEnabled(false);
                        }
                        break;
                    default:
                        spnUnilateral.setSelection(0);
                        spnUnilateral.setEnabled(true);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnUnilateral.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spnMusculo.getSelectedItem().toString()) {
                    case "Bíceps":
                        //NÃO PODE ALTERNADO
                        if (spnExercicio.getSelectedItemId() == 0) {
                            if (spnUnilateral.getSelectedItemId() == 1) {
                                spnUnilateral.setSelection(0);
                            }
                        }
                        break;
                    case "Costas":
                        //NÃO PODE ALTERNADO
                        if (spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 11 || spnExercicio.getSelectedItemId() == 12) {
                            if (spnUnilateral.getSelectedItemId() == 1) {
                                spnUnilateral.setSelection(0);
                            }
                        }
                        break;
                    case "Pernas":
                        //NÃO PODE NORMAL
                        if (spnExercicio.getSelectedItemId() == 10 || spnExercicio.getSelectedItemId() == 12) {
                            if (spnUnilateral.getSelectedItemId() == 0) {
                                spnUnilateral.setSelection(1);
                            }
                        }
                        //NÃO PODE ALTERNADO
                        else if (spnExercicio.getSelectedItemId() == 0 || spnExercicio.getSelectedItemId() == 1
                                || spnExercicio.getSelectedItemId() == 2 || spnExercicio.getSelectedItemId() == 3
                                || spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 6
                                || spnExercicio.getSelectedItemId() == 8 || spnExercicio.getSelectedItemId() == 13
                                || spnExercicio.getSelectedItemId() == 14) {
                            if (spnUnilateral.getSelectedItemId() == 1) {
                                spnUnilateral.setSelection(0);
                            }
                        }
                        break;
                    case "Ombro":
                        //NÃO PODE ALTERNADO
                        if (spnExercicio.getSelectedItemId() == 4 || spnExercicio.getSelectedItemId() == 9) {
                            if (spnUnilateral.getSelectedItemId() == 1) {
                                spnUnilateral.setSelection(0);
                            }
                        }
                        //NÃO PODE UNILATERAL
                        else if (spnExercicio.getSelectedItemId() == 7) {
                            if (spnUnilateral.getSelectedItemId() == 2) {
                                spnUnilateral.setSelection(0);
                            }
                        }
                        break;
                    case "Tríceps":
                        //NÃO PODE ALTERNADO
                        if (spnExercicio.getSelectedItemId() == 8 || spnExercicio.getSelectedItemId() == 4) {
                            if (spnUnilateral.getSelectedItemId() == 1) {
                                spnUnilateral.setSelection(0);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnTipoRep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (idExercicio == 500000 || altEditRep == true) {
                    if (position == 0) {
                        resetaEdits();
                    } else if (position == 3) {
                        resetaEdits();
                        edtRep1.setVisibility(View.INVISIBLE);
                    } else if (position == 2) {
                        resetaEdits();
                        edtRep5.setVisibility(View.VISIBLE);
                        txtSeriesDrop.setVisibility(View.VISIBLE);
                        txtRepDrop.setVisibility(View.VISIBLE);

                    } else if (position == 1) {
                        resetaEdits();
                        selecionaSpinnerRepInd();
                    }
                } else {
                    altEditRep = true;
                    if (position == 0) {
                    } else if (position == 3) {
                        edtRep1.setVisibility(View.INVISIBLE);
                    } else if (position == 2) {
                        edtRep5.setVisibility(View.VISIBLE);
                        txtSeriesDrop.setVisibility(View.VISIBLE);
                        txtRepDrop.setVisibility(View.VISIBLE);
                    } else if (position == 1) {
                        selecionaSpinnerRepInd();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgInfoTipoRep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item = (int) spnTipoRep.getSelectedItemId();
                switch (item) {
                    case 0:
                        Toast.makeText(EditarExercicioActivity.this, "Exercícios tradicionais são executados com o mesmo número de repetições em todas as séries", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(EditarExercicioActivity.this, "Exercícios em pirâmide são executados com a possibilidade de alterar o número de repetição em cada série", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(EditarExercicioActivity.this, "Exercícios com mini séries sem pausa com carga diferente em cada série", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(EditarExercicioActivity.this, "Exercício sem limite de repetições", Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

        imgInfoUnilateral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int item = (int) spnUnilateral.getSelectedItemId();
                switch (item) {
                    case 0:
                        Toast.makeText(EditarExercicioActivity.this, "Exercícios simultâneos são executados com apenas uma contagem, exercitando os dois lados simultaneamente", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(EditarExercicioActivity.this, "Exercícios alternados são executados duas vezes por contagem, exercitando cada lado para contar uma repetição", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(EditarExercicioActivity.this, "Exercícios unilaterais são executados um lado de cada vez, primeiro é feita a contagem até o final e depois a contagem é feita novamente para executar o outro lado com um tempo de 5 segundos para a troca", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

        imgImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherImagem(spnMusculo.getSelectedItemId(), spnExercicio.getSelectedItemId());
                imgExercicio.setVisibility(View.VISIBLE);
            }
        });

        imgExercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgExercicio.setVisibility(View.INVISIBLE);
            }
        });

        chbObs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    edtObs.setVisibility(View.VISIBLE);
                } else {
                    edtObs.setVisibility(View.INVISIBLE);
                    edtObs.setText("");
                }
            }
        });

        btnMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempoExecucao = Integer.parseInt(edtTempoExecucao.getText().toString());
                if (tempoExecucao < 9) {
                    tempoExecucao++;
                    edtTempoExecucao.setText("" + tempoExecucao);
                } else {
                    Toast.makeText(getApplicationContext(), "Valor máximo atingido", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempoExecucao = Integer.parseInt(edtTempoExecucao.getText().toString());
                if (tempoExecucao > 1) {
                    tempoExecucao--;
                    edtTempoExecucao.setText("" + tempoExecucao);
                } else {
                    Toast.makeText(getApplicationContext(), "Valor mínimo atingido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValoresExercicio();
                ver = true;

                if (setExercicio[1].equals("") || setExercicio[11].equals("")
                        || setExercicio[12].equals("") || setExercicio[13].equals("")) {
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    ver = false;
                } else {

                    for (int i = 0; i <= 17; i++) {
                        if (!setExercicio[i + 1].equals("") && !setExercicio[i + 1].equals(" ")) {
                            valorExercicios[i] = Integer.parseInt(setExercicio[i + 1]);

                        }

                    }
                    int t = valorExercicios[11] + valorExercicios[12];

                    if (valorExercicios[0] == 0 || valorExercicios[2] == 0 ||
                            valorExercicios[10] == 0 || t == 0) {
                        Toast.makeText(getApplicationContext(), "Preencha os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                        ver = false;
                    } else if (spnTipoRep.getSelectedItemId() == 2) {
                        if (valorExercicios[2] == 0 || setExercicio[3].equals("") || valorExercicios[6] == 0 || setExercicio[7].equals("")) {
                            Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                            ver = false;
                        }
                    } else if (spnTipoRep.getSelectedItemId() == 1) {
                        verificarRepInd();
                    } else if (spnTipoRep.getSelectedItemId() == 0) {
                        if (valorExercicios[2] == 0 || setExercicio[3].equals("")) {
                            Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                            ver = false;
                        }
                    }
                    if (valorExercicios[2] > 50) {
                        valorExercicios[2] = 50;
                        Toast.makeText(EditarExercicioActivity.this, "Valor de repetições ultrapassou o limite e foi alterado para 50", Toast.LENGTH_SHORT).show();
                    }

                }
                if (ver == true) {

                    try {
                        if (idExercicio == 500000) {
                            bancoDados.execSQL("INSERT INTO exercicios (exercicio, series, tipoRep, rep1, rep2," +
                                    "rep3, rep4, rep5, rep6, rep7, rep8, tempoExecucao, descansoM, descansoS, unilateral, idTreino, musculoSpinner, exercicioSpinner, completo, obs, serieAtual) VALUES" +
                                    "('" + setExercicio[0] + "', " + valorExercicios[0] + ", " + valorExercicios[1] + ", " + valorExercicios[2] + "" +
                                    ", " + valorExercicios[3] + ", " + valorExercicios[4] + ", " + valorExercicios[5] + "" +
                                    ", " + valorExercicios[6] + ", " + valorExercicios[7] + ", " + valorExercicios[8] + "" +
                                    ", " + valorExercicios[9] + ", " + valorExercicios[10] + ", " + valorExercicios[11] + "" +
                                    ", " + valorExercicios[12] + ", " + valorExercicios[13] + ", " + valorExercicios[14] + "" +
                                    ", " + valorExercicios[15] + ", " + valorExercicios[16] + ", " + valorExercicios[17] + ", '" + setExercicio[19] + "', 1)");
                            Toast.makeText(getApplicationContext(), "Exercício adicionado", Toast.LENGTH_SHORT).show();
                        } else {
                            bancoDados.execSQL("UPDATE exercicios SET exercicio = '" + setExercicio[0] + "', series = " + valorExercicios[0] + ", tipoRep = " + valorExercicios[1]
                                    + ", rep1 = " + valorExercicios[2] + ", rep2 = " + valorExercicios[3] + ", rep3 = " + valorExercicios[4]
                                    + ", rep4 = " + valorExercicios[5] + ", rep5 = " + valorExercicios[6] + ", rep6 = " + valorExercicios[7]
                                    + ", rep7 = " + valorExercicios[8] + ", rep8 = " + valorExercicios[9] + ", tempoExecucao = " + valorExercicios[10]
                                    + ", descansoM = " + valorExercicios[11] + ", descansoS = " + valorExercicios[12] + ", unilateral = " + valorExercicios[13]
                                    + ", obs = '" + setExercicio[19] + "' WHERE idExercicio = " + idExercicio);
                            Toast.makeText(getApplicationContext(), "Exercício alterado", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onBackPressed();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        extra = getIntent().getExtras();
        if (extra != null) {
            idTreino = extra.getInt("idTreino");
            idExercicio = extra.getInt("idExercicio");
            carregarValores();
        }

        edtSeries.requestFocus();

    }

    void escolherImagem(long m, long e){
        int M = (int) m;
        int E = (int) e;
        switch (M){
            case 0:
                switch (E){
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
                switch (E){
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
                switch (E){
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
                switch (E){
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
                switch (E){
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
                switch (E){
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
                switch (E){
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

    private void resetaEdits() {
        edtRep1.setVisibility(View.VISIBLE);
        edtRep2.setVisibility(View.INVISIBLE);
        edtRep3.setVisibility(View.INVISIBLE);
        edtRep4.setVisibility(View.INVISIBLE);
        edtRep5.setVisibility(View.INVISIBLE);
        edtRep6.setVisibility(View.INVISIBLE);
        edtRep7.setVisibility(View.INVISIBLE);
        edtRep8.setVisibility(View.INVISIBLE);
        edtRep1.setText("");
        edtRep2.setText("");
        edtRep3.setText("");
        edtRep4.setText("");
        edtRep5.setText("");
        edtRep6.setText("");
        edtRep7.setText("");
        edtRep8.setText("");
        edtRep2.setEnabled(true);
        edtRep3.setEnabled(true);
        edtRep4.setEnabled(true);
        edtRep5.setEnabled(true);
        edtRep6.setEnabled(true);
        edtRep7.setEnabled(true);
        edtRep8.setEnabled(true);
        txtRepDrop.setVisibility(View.INVISIBLE);
        txtSeriesDrop.setVisibility(View.INVISIBLE);
    }

    private void setValoresExercicio() {
        if (idExercicio != 500000) {
            setExercicio[0] = txtExercicio.getText().toString().trim();

        } else {
            setExercicio[0] = spnExercicio.getSelectedItem().toString().trim();
        }
        setExercicio[1] = edtSeries.getText().toString().trim();
        setExercicio[2] = "" + spnTipoRep.getSelectedItemId();
        if (setExercicio[0].toString().equals("Rosca 21")) {
            setExercicio[2] = "2";

        }
        if (spnTipoRep.getSelectedItemId() == 3) {
            setExercicio[3] = "50";
        } else {
            setExercicio[3] = edtRep1.getText().toString().trim();
        }
        if (spnTipoRep.getSelectedItemId() == 2) {
            setExercicio[4] = "";
            setExercicio[8] = "";
        } else {
            setExercicio[4] = edtRep2.getText().toString().trim();
            setExercicio[8] = edtRep6.getText().toString().trim();
        }
        setExercicio[5] = edtRep3.getText().toString().trim();
        setExercicio[6] = edtRep4.getText().toString().trim();
        setExercicio[7] = edtRep5.getText().toString().trim();
        setExercicio[9] = edtRep7.getText().toString().trim();
        setExercicio[10] = edtRep8.getText().toString().trim();
        setExercicio[11] = edtTempoExecucao.getText().toString().trim();
        setExercicio[12] = edtDescansoM.getText().toString().trim();
        setExercicio[13] = edtDescansoS.getText().toString().trim();
        if (spnUnilateral.getSelectedItemId() == 1) {
            setExercicio[14] = "1";
        } else if (spnUnilateral.getSelectedItemId() == 0) {
            setExercicio[14] = "0";
        } else if (spnUnilateral.getSelectedItemId() == 2) {
            setExercicio[14] = "2";
        }
        setExercicio[15] = String.valueOf(idTreino);
        setExercicio[16] = "" + spnMusculo.getSelectedItemId();
        setExercicio[17] = "" + spnExercicio.getSelectedItemId();
        setExercicio[18] = "0";
        setExercicio[19] = edtObs.getText().toString().trim();

    }

    private void verificarRepInd() {
        if (valorExercicios[0] == 8) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")
                    || valorExercicios[3] == 0 || setExercicio[4].equals("")
                    || valorExercicios[4] == 0 || setExercicio[5].equals("")
                    || valorExercicios[5] == 0 || setExercicio[6].equals("")
                    || valorExercicios[6] == 0 || setExercicio[7].equals("")
                    || valorExercicios[7] == 0 || setExercicio[8].equals("")
                    || valorExercicios[8] == 0 || setExercicio[9].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 7) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")
                    || valorExercicios[3] == 0 || setExercicio[4].equals("")
                    || valorExercicios[4] == 0 || setExercicio[5].equals("")
                    || valorExercicios[5] == 0 || setExercicio[6].equals("")
                    || valorExercicios[6] == 0 || setExercicio[7].equals("")
                    || valorExercicios[7] == 0 || setExercicio[8].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 6) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")
                    || valorExercicios[3] == 0 || setExercicio[4].equals("")
                    || valorExercicios[4] == 0 || setExercicio[5].equals("")
                    || valorExercicios[5] == 0 || setExercicio[6].equals("")
                    || valorExercicios[6] == 0 || setExercicio[7].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 5) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")
                    || valorExercicios[3] == 0 || setExercicio[4].equals("")
                    || valorExercicios[4] == 0 || setExercicio[5].equals("")
                    || valorExercicios[5] == 0 || setExercicio[6].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 4) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")
                    || valorExercicios[3] == 0 || setExercicio[4].equals("")
                    || valorExercicios[4] == 0 || setExercicio[5].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 3) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")
                    || valorExercicios[3] == 0 || setExercicio[4].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 2) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")
                    || valorExercicios[2] == 0 || setExercicio[3].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        } else if (valorExercicios[0] == 1) {
            if (valorExercicios[1] == 0 || setExercicio[2].equals("")) {
                Toast.makeText(getApplicationContext(), "Preencha todos os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                ver = false;
            }
        }
    }

    private void selecionaSpinnerRepInd() {
        if (!edtSeries.getText().toString().equals("") && !edtSeries.getText().toString().equals(" ")) {
            int s = Integer.parseInt(edtSeries.getText().toString());
            switch (s) {
                case 0:
                    resetaEdits();
                    break;
                case 1:
                    edtRep2.setVisibility(View.INVISIBLE);
                    edtRep3.setVisibility(View.INVISIBLE);
                    edtRep4.setVisibility(View.INVISIBLE);
                    edtRep5.setVisibility(View.INVISIBLE);
                    edtRep6.setVisibility(View.INVISIBLE);
                    edtRep7.setVisibility(View.INVISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep2.setText("");
                    edtRep3.setText("");
                    edtRep4.setText("");
                    edtRep5.setText("");
                    edtRep6.setText("");
                    edtRep7.setText("");
                    edtRep8.setText("");
                    break;
                case 2:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.INVISIBLE);
                    edtRep4.setVisibility(View.INVISIBLE);
                    edtRep5.setVisibility(View.INVISIBLE);
                    edtRep6.setVisibility(View.INVISIBLE);
                    edtRep7.setVisibility(View.INVISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep3.setText("");
                    edtRep4.setText("");
                    edtRep5.setText("");
                    edtRep6.setText("");
                    edtRep7.setText("");
                    edtRep8.setText("");
                    break;
                case 3:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.INVISIBLE);
                    edtRep5.setVisibility(View.INVISIBLE);
                    edtRep6.setVisibility(View.INVISIBLE);
                    edtRep7.setVisibility(View.INVISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep4.setText("");
                    edtRep5.setText("");
                    edtRep6.setText("");
                    edtRep7.setText("");
                    edtRep8.setText("");
                    break;
                case 4:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.VISIBLE);
                    edtRep5.setVisibility(View.INVISIBLE);
                    edtRep6.setVisibility(View.INVISIBLE);
                    edtRep7.setVisibility(View.INVISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep5.setText("");
                    edtRep6.setText("");
                    edtRep7.setText("");
                    edtRep8.setText("");
                    break;
                case 5:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.VISIBLE);
                    edtRep5.setVisibility(View.VISIBLE);
                    edtRep6.setVisibility(View.INVISIBLE);
                    edtRep7.setVisibility(View.INVISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep6.setText("");
                    edtRep7.setText("");
                    edtRep8.setText("");
                    break;
                case 6:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.VISIBLE);
                    edtRep5.setVisibility(View.VISIBLE);
                    edtRep6.setVisibility(View.VISIBLE);
                    edtRep7.setVisibility(View.INVISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep7.setText("");
                    edtRep8.setText("");
                    break;
                case 7:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.VISIBLE);
                    edtRep5.setVisibility(View.VISIBLE);
                    edtRep6.setVisibility(View.VISIBLE);
                    edtRep7.setVisibility(View.VISIBLE);
                    edtRep8.setVisibility(View.INVISIBLE);
                    edtRep8.setText("");
                    break;
                case 8:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.VISIBLE);
                    edtRep5.setVisibility(View.VISIBLE);
                    edtRep6.setVisibility(View.VISIBLE);
                    edtRep7.setVisibility(View.VISIBLE);
                    edtRep8.setVisibility(View.VISIBLE);
                    break;
                default:
                    edtRep2.setVisibility(View.VISIBLE);
                    edtRep3.setVisibility(View.VISIBLE);
                    edtRep4.setVisibility(View.VISIBLE);
                    edtRep5.setVisibility(View.VISIBLE);
                    edtRep6.setVisibility(View.VISIBLE);
                    edtRep7.setVisibility(View.VISIBLE);
                    edtRep8.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Valor máximo 8", Toast.LENGTH_SHORT).show();
                    edtSeries.setText("8");
            }
        }
    }

    private void carregarValores() {
        try {
            if (idExercicio != 500000) {
                Cursor cursor = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idExercicio = " + idExercicio, null);
                int indExercicio = cursor.getColumnIndex("exercicio");
                int indSeries = cursor.getColumnIndex("series");
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
                int indMusculoSpinner = cursor.getColumnIndex("musculoSpinner");
                int indExercicioSpinner = cursor.getColumnIndex("exercicioSpinner");
                int indObs = cursor.getColumnIndex("obs");

                spnExercicio.setVisibility(View.INVISIBLE);
                txtExercicio.setVisibility(View.VISIBLE);
                spnMusculo.setVisibility(View.INVISIBLE);
                txtMusculo.setVisibility(View.VISIBLE);
                carregado = 1;
                cursor.moveToFirst();

                while (cursor != null) {
                    spnMusculo.setSelection(cursor.getInt(indMusculoSpinner));
                    exercicio = (cursor.getString(indExercicio));
                    edtObs.setText(cursor.getString(indObs));
                    if (edtObs.getText().toString().equals("")){
                        chbObs.setChecked(false);
                    } else {
                        chbObs.setChecked(true);
                    }

                    txtExercicio.setText(cursor.getString(indExercicio));
                    exer = cursor.getInt(indExercicioSpinner);
                    switch (cursor.getInt(indMusculoSpinner)) {
                        case 0:
                            txtMusculo.setText("Abdômen");
                            break;
                        case 1:
                            txtMusculo.setText("Bíceps");
                            break;
                        case 2:
                            txtMusculo.setText("Costas");
                            break;
                        case 3:
                            txtMusculo.setText("Peito");
                            break;
                        case 4:
                            txtMusculo.setText("Pernas");
                            break;
                        case 5:
                            txtMusculo.setText("Ombro");
                            break;
                        case 6:
                            txtMusculo.setText("Tríceps");

                            break;
                        default:
                            break;
                    }
                    edtRep1.setText(cursor.getString(indRep1));
                    edtRep2.setText(cursor.getString(indRep2));
                    edtRep3.setText(cursor.getString(indRep3));
                    edtRep4.setText(cursor.getString(indRep4));
                    edtRep5.setText(cursor.getString(indRep5));
                    edtRep6.setText(cursor.getString(indRep6));
                    edtRep7.setText(cursor.getString(indRep7));
                    edtRep8.setText(cursor.getString(indRep8));
                    edtSeries.setText(cursor.getString(indSeries));
                    if (exercicio.equals("Rosca 21")) {
                        spnTipoRep.setSelection(0);
                        spnTipoRep.setEnabled(false);
                        edtRep5.setEnabled(false);
                    } else {
                        spnTipoRep.setSelection(cursor.getInt(indTipoRep));
                    }
                    edtTempoExecucao.setText(cursor.getString(indTempoExecucao));
                    edtDescansoM.setText(cursor.getString(indDescansoM));
                    if (Integer.parseInt(cursor.getString(indDescansoS)) <= 9) {
                        edtDescansoS.setText("0" + cursor.getString(indDescansoS));
                    } else {
                        edtDescansoS.setText(cursor.getString(indDescansoS));
                    }

                    runRep = new Runnable() {
                        @Override
                        public void run() {
                            handler.postDelayed(runRep, 10);
                            spnExercicio.setSelection(exer);
                        }
                    };
                    handler.post(runRep);
                    int uni = cursor.getInt(indUnilateral);
                    if (uni == 1) {
                        spnUnilateral.setSelection(1);
                    } else if (uni == 0) {
                        spnUnilateral.setSelection(0);
                    } else if (uni == 2) {
                        spnUnilateral.setSelection(2);
                    }
                    cursor.moveToNext();
                }
            } else {
                spnExercicio.setVisibility(View.VISIBLE);
                txtExercicio.setVisibility(View.INVISIBLE);
                spnMusculo.setVisibility(View.VISIBLE);
                txtMusculo.setVisibility(View.INVISIBLE);
                edtSeries.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void adapter(String[] musculo){
        ArrayAdapter<String> adapterExer = new ArrayAdapter<>(this, R.layout.spinner_item, musculo);
        adapterExer.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnExercicio.setAdapter(adapterExer);
    }


}