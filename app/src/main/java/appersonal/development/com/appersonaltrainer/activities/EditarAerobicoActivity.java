package appersonal.development.com.appersonaltrainer.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import appersonal.development.com.appersonaltrainer.R;

public class EditarAerobicoActivity extends AppCompatActivity {

    private Spinner spnAerobico;
    private TextView txtSeries;
    private TextView txtKM;
    private TextView txtAerobico;
    private TextView txtDoisPontos;
    private TextView txtDescanso;
    private TextView txtDuracao;
    private EditText edtDuracaoM;
    private EditText edtDuracaoS;
    private EditText edtSeries;
    private EditText edtDuracaoH;
    private EditText edtDescansoM;
    private EditText edtDescansoS;
    private EditText edtDistancia;
    private EditText edtObs;
    private CheckBox chbObs;
    private CheckBox chbDistancia;

    private SQLiteDatabase bancoDados;
    private LinearLayout layoutDescanso;
    private LinearLayout layoutDuracao;

    private Double valorAerobicoKM;
    private int idAerobico;
    private int idTreino;
    private int tempoMedio;
    private boolean ver;
    private boolean vazio = false;

    private final String[] setAerobico = new String[11];
    private final int[] valorAerobico = new int[7];


    private final String[] aerobico = {
            "Esteira", "Elíptico", "Spinning/Bicicleta", "Step", "Corda"
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if (edtDuracaoM.isFocused()) {
                if (edtDuracaoM.getText().length() == 0 && vazio) {
                    edtDuracaoH.requestFocus();
                    vazio = false;
                } else vazio = edtDuracaoM.getText().length() == 0;
            } else if (edtDuracaoS.isFocused()) {
                if (edtDuracaoS.getText().length() == 0 && vazio) {
                    edtDuracaoM.requestFocus();
                    vazio = false;
                } else vazio = edtDuracaoS.getText().length() == 0;
            } else if (edtSeries.isFocused()) {
                if (edtSeries.getText().length() == 0 && vazio) {
                    edtDuracaoM.requestFocus();
                    vazio = false;
                } else vazio = edtSeries.getText().length() == 0;
            } else if (edtDescansoS.isFocused()) {
                if (edtDescansoS.getText().length() == 0 && vazio) {
                    edtDescansoM.requestFocus();
                    vazio = false;
                } else vazio = edtDescansoS.getText().length() == 0;
            } else if (edtDescansoM.isFocused()) {
                if (edtDescansoM.getText().length() == 0 && vazio) {
                    edtSeries.requestFocus();
                    vazio = false;
                } else vazio = edtDescansoM.getText().length() == 0;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            idTreino = extra.getInt("idTreino");
            idAerobico = extra.getInt("idAerobico");
            carregarValores();
        }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_aerobico);

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

        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        Button btnCancelar = findViewById(R.id.btnCancelar);
        spnAerobico = findViewById(R.id.spnAerobico);
        edtSeries = findViewById(R.id.edtSeries);
        edtDuracaoH = findViewById(R.id.edtDuracaoH);
        edtDuracaoM = findViewById(R.id.edtDuracaoM);
        edtDuracaoS = findViewById(R.id.edtDuracaoS);
        edtDescansoM = findViewById(R.id.edtDescansoM);
        edtDescansoS = findViewById(R.id.edtDescansoS);
        edtDistancia = findViewById(R.id.edtDistancia);
        edtObs = findViewById(R.id.edtObs);
        chbDistancia = findViewById(R.id.chbDistancia);
        chbObs = findViewById(R.id.chbObs);
        txtSeries = findViewById(R.id.txtSeries);
        txtDescanso = findViewById(R.id.txtDescanso);
        txtKM = findViewById(R.id.txtKM);
        txtAerobico = findViewById(R.id.txtAerobico);
        txtDoisPontos = findViewById(R.id.txtDoisP);
        txtDuracao = findViewById(R.id.txtDuracao);
        layoutDescanso = findViewById(R.id.layoutDescanso);
        layoutDuracao = findViewById(R.id.layoutDuracao);

        edtDuracaoH.setSelectAllOnFocus(true);
        edtDuracaoM.setSelectAllOnFocus(true);
        edtDuracaoS.setSelectAllOnFocus(true);
        edtDescansoM.setSelectAllOnFocus(true);
        edtDescansoS.setSelectAllOnFocus(true);
        edtSeries.setSelectAllOnFocus(true);
        edtDistancia.setSelectAllOnFocus(true);


        try {
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, aerobico);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnAerobico.setAdapter(adapter);

        spnAerobico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3 || position == 4) {
                    chbDistancia.setVisibility(View.INVISIBLE);
                    chbDistancia.setChecked(false);
                    txtSeries.setVisibility(View.VISIBLE);
                    edtSeries.setVisibility(View.VISIBLE);
                    txtDescanso.setVisibility(View.VISIBLE);
                    layoutDescanso.setVisibility(View.VISIBLE);
                    edtDuracaoS.setVisibility(View.INVISIBLE);
                    txtDoisPontos.setVisibility(View.INVISIBLE);
                    edtDuracaoH.requestFocus();
                } else {
                    chbDistancia.setVisibility(View.VISIBLE);
                    txtSeries.setVisibility(View.INVISIBLE);
                    edtSeries.setVisibility(View.INVISIBLE);
                    txtDescanso.setVisibility(View.INVISIBLE);
                    layoutDescanso.setVisibility(View.INVISIBLE);
                    edtDuracaoS.setVisibility(View.VISIBLE);
                    txtDoisPontos.setVisibility(View.VISIBLE);
                    edtDuracaoH.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        chbDistancia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutDuracao.setVisibility(View.INVISIBLE);
                    edtDuracaoM.setText("");
                    edtDuracaoS.setText("");
                    edtDuracaoH.setText("");
                    txtDuracao.setText("Distância:");
                    edtDistancia.setVisibility(View.VISIBLE);
                    txtKM.setVisibility(View.VISIBLE);

                } else {
                    layoutDuracao.setVisibility(View.VISIBLE);
                    edtDistancia.setVisibility(View.INVISIBLE);
                    txtKM.setVisibility(View.INVISIBLE);
                    txtDuracao.setText("Duração:");
                    edtDuracaoH.requestFocus();
                }
            }
        });

        edtDuracaoH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtDuracaoH.getText().length() == 1) {
                    edtDuracaoM.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtDuracaoM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtDuracaoM.getText().length() == 0 && vazio) {
                    edtDuracaoH.requestFocus();
                    vazio = false;
                } else if (edtDuracaoM.getText().length() == 0) {
                    vazio = true;
                    edtDuracaoM.selectAll();
                } else {
                    vazio = false;
                }

                if (edtSeries.getVisibility() == View.VISIBLE) {
                    if (edtDuracaoM.getText().length() == 2) {
                        edtSeries.requestFocus();
                    }
                } else {
                    if (edtDuracaoM.getText().length() == 2) {
                        edtDuracaoS.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtDuracaoS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtDuracaoS.getText().length() == 0 && vazio) {
                    edtDuracaoM.requestFocus();
                    vazio = false;
                } else if (edtDuracaoS.getText().length() == 0) {
                    vazio = true;
                    edtDuracaoS.selectAll();
                } else {
                    vazio = false;
                }
                if (edtDuracaoS.getText().length() == 2) {
                    //noinspection ConstantConditions
                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(edtDescansoS.getWindowToken(), 0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtSeries.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSeries.getText().length() == 0 && vazio) {
                    edtDuracaoM.requestFocus();
                    vazio = false;
                } else if (edtSeries.getText().length() == 0) {
                    vazio = true;
                    edtSeries.selectAll();
                } else {
                    vazio = false;
                }
                if (edtSeries.getText().length() == 2) {
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
                if (edtDescansoM.getText().length() == 0 && vazio) {
                    edtSeries.requestFocus();
                    vazio = false;
                } else if (edtDescansoM.getText().length() == 0) {
                    vazio = true;
                    edtDescansoM.selectAll();
                } else if (edtDescansoM.getText().length() == 1 && !edtDescansoM.getText().toString().equals(" ")) {
                    edtDescansoS.requestFocus();
                } else {
                    vazio = false;
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
                if (edtDescansoS.getText().length() == 0 && vazio) {
                    edtDescansoM.requestFocus();
                    vazio = false;
                } else if (edtDescansoS.getText().length() == 0) {
                    vazio = true;
                    edtDescansoS.selectAll();
                } else {
                    vazio = false;
                }
                if (edtDescansoS.getText().length() == 2) {
                    //noinspection ConstantConditions
                    ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(edtDescansoS.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chbObs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edtObs.setVisibility(View.VISIBLE);
                } else {
                    edtObs.setVisibility(View.INVISIBLE);
                    edtObs.setText("");
                }
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setValoresExercicio();
                ver = true;

                for (int i = 0; i <= 6; i++) {
                    if (!setAerobico[i + 1].equals("") && !setAerobico[i + 1].equals(" ")) {
                        valorAerobico[i] = Integer.parseInt(setAerobico[i + 1]);
                    }
                }

                if (spnAerobico.getSelectedItemId() == 3 || spnAerobico.getSelectedItemId() == 4) {
                    if (setAerobico[2].equals("")
                            || setAerobico[3].equals("") || setAerobico[4].equals("")
                            || setAerobico[5].equals("") || setAerobico[6].equals("")) {
                        Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        ver = false;
                    } else {
                        if (valorAerobico[2] > 59 || valorAerobico[5] > 59) {
                            if (valorAerobico[2] > 59) {
                                edtDuracaoM.requestFocus();
                            } else {
                                edtDescansoS.requestFocus();
                            }
                            Toast.makeText(getApplicationContext(), "Valor(es) de tempo inválidos", Toast.LENGTH_SHORT).show();
                            ver = false;
                        } else {
                            int du = valorAerobico[1] + valorAerobico[2];
                            int de = valorAerobico[4] + valorAerobico[5];
                            if (du <= 0 || de <= 0 || valorAerobico[3] <= 0) {
                                Toast.makeText(getApplicationContext(), "Preencha os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                                ver = false;
                            } else {
                                int tempoDescanso = valorAerobico[4] * 60 + valorAerobico[5];
                                int tempoAer = valorAerobico[1] * 60 + valorAerobico[2];
                                tempoMedio = (tempoAer * valorAerobico[3]) + (tempoDescanso * (valorAerobico[3] - 1));
                            }
                        }
                    }
                } else {
                    if (chbDistancia.isChecked()) {
                        if (setAerobico[8].equals("")) {
                            Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                            ver = false;
                        } else {
                            valorAerobicoKM = Double.parseDouble(setAerobico[8]);
                            tempoMedio = 99999999;
                            if (valorAerobicoKM <= 0) {
                                Toast.makeText(getApplicationContext(), "Preencha os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                                ver = false;
                            }
                        }
                    } else {
                        if (setAerobico[1].isEmpty())
                            setAerobico[1] = "0";
                        if (setAerobico[2].isEmpty())
                            setAerobico[2] = "0";
                        if (setAerobico[3].isEmpty())
                            setAerobico[3] = "0";
//                        if (setAerobico[1].toString().equals("") || setAerobico[2].toString().equals("") || setAerobico[3].toString().equals("")) {
                        if ((Integer.parseInt(setAerobico[1]) + Integer.parseInt(setAerobico[2]) + Integer.parseInt(setAerobico[3])) <= 0) {
                            Toast.makeText(getApplicationContext(), "Digite um tempo de duração maior que 0", Toast.LENGTH_SHORT).show();
                            ver = false;
                        } else {
                            if (valorAerobico[1] > 59 || valorAerobico[2] > 59) {
                                if (valorAerobico[1] > 59) {
                                    edtDuracaoM.requestFocus();
                                } else {
                                    edtDuracaoS.requestFocus();
                                }
                                Toast.makeText(getApplicationContext(), "Valor(es) de tempo inválidos", Toast.LENGTH_SHORT).show();
                                ver = false;
                            }
                            int du = valorAerobico[0] + valorAerobico[1] + valorAerobico[2];
                            if (du <= 0) {
                                Toast.makeText(getApplicationContext(), "Preencha os campos com valores maiores que 0", Toast.LENGTH_SHORT).show();
                                ver = false;
                            } else {
                                tempoMedio = (valorAerobico[0] * 3600) + (valorAerobico[1] * 60) + valorAerobico[2];
                            }
                        }
                    }
                }
                if (ver) {
                    try {
                        if (idAerobico == 500000) {
                            bancoDados.execSQL("INSERT INTO aerobicos (aerobico, duracaoH, duracaoM, duracaoS, series, " +
                                    "descansoM, descansoS, distancia, km, obs, idTreino, tempoMedio) VALUES " +
                                    "('" + setAerobico[0] + "', " + valorAerobico[0] + ", " + valorAerobico[1] + ", " + valorAerobico[2] + ", "
                                    + valorAerobico[3] + ", " + valorAerobico[4] + ", " + valorAerobico[5] + ", " + valorAerobico[6] + ", "
                                    + valorAerobicoKM + ", '" + setAerobico[9] + "', " + idTreino + ", " + tempoMedio + " )");
                            Toast.makeText(getApplicationContext(), "Aeróbico adicionado", Toast.LENGTH_SHORT).show();
                        } else {
                            bancoDados.execSQL("UPDATE aerobicos SET duracaoH = " + valorAerobico[0] + ", duracaoM = " + valorAerobico[1] +
                                    ", duracaoS = " + valorAerobico[2] + ", series = " + valorAerobico[3] + ", descansoM = " + valorAerobico[4] +
                                    ", descansoS = " + valorAerobico[5] + ", distancia = " + valorAerobico[6] + ", km = " + valorAerobicoKM + ", obs = '" + setAerobico[9] +
                                    "', tempoMedio = " + tempoMedio + " WHERE idAerobico = " + idAerobico);
                            Toast.makeText(getApplicationContext(), "Aeróbico alterado", Toast.LENGTH_SHORT).show();
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

        edtDuracaoH.requestFocus();

    }

    private void setValoresExercicio() {
        if (idAerobico != 500000) {
            setAerobico[0] = txtAerobico.getText().toString();

        } else {
            setAerobico[0] = spnAerobico.getSelectedItem().toString();
        }
        if (spnAerobico.getSelectedItemId() == 3 || spnAerobico.getSelectedItemId() == 4) {
            setAerobico[1] = edtDuracaoS.getText().toString().trim();
            setAerobico[2] = edtDuracaoH.getText().toString().trim();
            setAerobico[3] = edtDuracaoM.getText().toString().trim();
        } else {
            setAerobico[1] = edtDuracaoH.getText().toString().trim();
            setAerobico[2] = edtDuracaoM.getText().toString().trim();
            setAerobico[3] = edtDuracaoS.getText().toString().trim();
        }
        setAerobico[4] = edtSeries.getText().toString().trim();
        setAerobico[5] = edtDescansoM.getText().toString().trim();
        setAerobico[6] = edtDescansoS.getText().toString().trim();
        if (chbDistancia.isChecked()) {
            setAerobico[7] = "1";
        } else if (chbDistancia.getVisibility() == View.INVISIBLE) {
            setAerobico[7] = "0";
        } else {
            setAerobico[7] = "0";
        }
        setAerobico[8] = edtDistancia.getText().toString().trim();
        setAerobico[9] = edtObs.getText().toString().trim();

    }

    @SuppressLint("SetTextI18n")
    private void carregarValores() {
        try {
            if (idAerobico != 500000) {
                spnAerobico.setVisibility(View.INVISIBLE);
                txtAerobico.setVisibility(View.VISIBLE);
                Cursor cursor = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idAerobico = " + idAerobico, null);
                int indAerobico = cursor.getColumnIndex("aerobico");
                int indDuracaoH = cursor.getColumnIndex("duracaoH");
                int indDuracaoM = cursor.getColumnIndex("duracaoM");
                int indDuracaoS = cursor.getColumnIndex("duracaoS");
                int indSeries = cursor.getColumnIndex("series");
                int indDescansoM = cursor.getColumnIndex("descansoM");
                int indDescansoS = cursor.getColumnIndex("descansoS");
                int indDistancia = cursor.getColumnIndex("distancia");
                int indKM = cursor.getColumnIndex("km");
                int indObs = cursor.getColumnIndex("obs");

                while (cursor.moveToNext()) {
                    txtAerobico.setText(cursor.getString(indAerobico));
                    switch (txtAerobico.getText().toString()) {
                        case "Caminhada/Corrida":
                            spnAerobico.setSelection(0);
                            break;
                        case "Elíptico":
                            spnAerobico.setSelection(1);
                            break;
                        case "Spinning/Bicicleta":
                            spnAerobico.setSelection(2);
                            break;
                        case "Step":
                            spnAerobico.setSelection(3);
                            break;
                        case "Corda":
                            spnAerobico.setSelection(4);
                            break;
                    }

                    if (spnAerobico.getSelectedItemId() == 3 || spnAerobico.getSelectedItemId() == 4) {
                        edtDuracaoH.setText(cursor.getString(indDuracaoM));
                        if (Integer.parseInt(cursor.getString(indDuracaoS)) <= 9) {
                            edtDuracaoM.setText("0" + cursor.getString(indDuracaoS));
                        } else {
                            edtDuracaoM.setText(cursor.getString(indDuracaoS));
                        }
                    } else {
                        edtDuracaoH.setText(cursor.getString(indDuracaoH));
                        if (Integer.parseInt(cursor.getString(indDuracaoM)) <= 9) {
                            edtDuracaoM.setText("0" + cursor.getString(indDuracaoM));
                        } else {
                            edtDuracaoM.setText(cursor.getString(indDuracaoM));
                        }
                        if (Integer.parseInt(cursor.getString(indDuracaoS)) <= 9) {
                            edtDuracaoS.setText("0" + cursor.getString(indDuracaoS));
                        } else {
                            edtDuracaoS.setText(cursor.getString(indDuracaoS));
                        }
                    }
                    edtSeries.setText(cursor.getString(indSeries));
                    edtDescansoM.setText(cursor.getString(indDescansoM));
                    if (Integer.parseInt(cursor.getString(indDescansoS)) <= 9) {
                        edtDescansoS.setText("0" + cursor.getString(indDescansoS));
                    } else {
                        edtDescansoS.setText(cursor.getString(indDescansoS));
                    }
                    edtObs.setText(cursor.getString(indObs));
                    if (edtObs.getText().toString().equals("")) {
                        chbObs.setChecked(false);
                    } else {
                        chbObs.setChecked(true);
                    }

                    int distancia = cursor.getInt(indDistancia);
                    if (distancia == 1) {
                        chbDistancia.setChecked(true);
                    } else {
                        chbDistancia.setChecked(false);
                    }
                    edtDistancia.setText(cursor.getString(indKM));
                }
                cursor.close();
            } else {
                spnAerobico.setVisibility(View.VISIBLE);
                txtAerobico.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        edtDuracaoH.requestFocus();
    }
}