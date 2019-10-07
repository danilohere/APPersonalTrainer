package appersonal.development.com.appersonaltrainer.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.R;

public class EditarTreinoActivity extends AppCompatActivity {

    private ListView lstExercicios;
    private ArrayList<String> exercicios;
    private ArrayList<Integer> idsEx;
    private ArrayList<Integer> idsAer;
    private SQLiteDatabase bancoDados;
    private Button btnNovoExercicio;
    private Button btnNovoAerobico;
    private EditText edtTreino;
    private TextView txtTreino;
    private TextView txtTempoMedio;
    private RelativeLayout rl;
    private int numIdEx;
    private int idTreino;
    private int posIni;
    private int idEscolhido;
    private boolean exercEscolhido;
    private boolean semTempo = false;
    private String maisAerobico = "";

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
        if (!edtTreino.getText().toString().equals("")) {
            salvar();
        }

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_treino);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Implementa o ad na activity
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .addTestDevice("6993B0696AE064D27BBDC28B90575368")
                .build();
        adView.loadAd(adRequest);

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
        }

        lstExercicios = findViewById(R.id.lstExercicios);
        btnNovoExercicio = findViewById(R.id.btnNovoExercicio);
        btnNovoAerobico = findViewById(R.id.btnNovoAerobico);
        edtTreino = findViewById(R.id.edtTreino);
        txtTreino = findViewById(R.id.txtTreino);
        txtTempoMedio = findViewById(R.id.txtTempoMedio);
        rl = findViewById(R.id.activity_editar_treino);

        recuperarExercicios();
        lstExercicios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (edtTreino.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Digite um nome para o treino", Toast.LENGTH_SHORT).show();
                } else {
                    salvar();
                    Intent exercicio = new Intent(EditarTreinoActivity.this, EditarExercicioActivity.class);
                    Intent aerobico = new Intent(EditarTreinoActivity.this, EditarAerobicoActivity.class);
                    if (position < numIdEx) {
                        exercicio.putExtra("idExercicio", idsEx.get(position));
                        exercicio.putExtra("idTreino", idTreino);
                        startActivity(exercicio);
                    } else {
                        aerobico.putExtra("idAerobico", idsAer.get(position - numIdEx));
                        aerobico.putExtra("idTreino", idTreino);
                        startActivity(aerobico);
                    }
                }

            }
        });

        lstExercicios.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {

                if (event.getAction() == DragEvent.ACTION_DROP) {
                    //
                    // finish dragging

                    int posNova = lstExercicios.pointToPosition((int) event.getX(), (int) event.getY());

                    Cursor cursorExercicio = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);
                    Cursor cursorAerobico = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);
                    int indIdEx = cursorExercicio.getColumnIndex("idExercicio");
                    int indPos = cursorExercicio.getColumnIndex("pos");
                    bancoDados.execSQL("UPDATE exercicios SET pos = " + posNova + " WHERE idExercicio = " + idEscolhido);
                    int indIdAer = cursorAerobico.getColumnIndex("idAerobico");
                    int indPosA = cursorAerobico.getColumnIndex("pos");
                    bancoDados.execSQL("UPDATE aerobicos SET pos = " + posNova + " WHERE idAerobico = " + idEscolhido);
                    if (posNova == -1) {
                        posNova = exercicios.size() - 1;
                    }
                    if (exercEscolhido) {
                        try {
                            while (cursorExercicio.moveToNext()) {
                                if (cursorExercicio.getInt(indIdEx) != idEscolhido) {
                                    if (posNova < posIni) {
                                        if (cursorExercicio.getInt(indPos) >= posNova) {
                                            int pos = (cursorExercicio.getInt(indPos) + 1);
                                            bancoDados.execSQL("UPDATE exercicios SET pos = " + pos + " WHERE idExercicio = " + cursorExercicio.getInt(indIdEx));
                                        }
                                        if (cursorExercicio.getInt(indPos) > posIni) {
                                            int pos = (cursorExercicio.getInt(indPos) - 1);
                                            bancoDados.execSQL("UPDATE exercicios SET pos = " + pos + " WHERE idExercicio = " + cursorExercicio.getInt(indIdEx));
                                        }
                                    }
                                    if (posNova > posIni) {
                                        if (cursorExercicio.getInt(indPos) <= posNova) {
                                            int pos = (cursorExercicio.getInt(indPos) - 1);
                                            bancoDados.execSQL("UPDATE exercicios SET pos = " + pos + " WHERE idExercicio = " + cursorExercicio.getInt(indIdEx));
                                        }
                                        if (cursorExercicio.getInt(indPos) < posIni) {
                                            int pos = (cursorExercicio.getInt(indPos) + 1);
                                            bancoDados.execSQL("UPDATE exercicios SET pos = " + pos + " WHERE idExercicio = " + cursorExercicio.getInt(indIdEx));
                                        }
                                    }
                                }
                            }
                        } finally {
                            cursorExercicio.close();
                        }
                    } else {
                        try {
                            if (posNova == -1) {
                                posNova = exercicios.size() - 1;
                            } else if (posNova < numIdEx) {
                                posNova = numIdEx;
                            }
                            while (cursorAerobico.moveToNext()) {
                                if (cursorAerobico.getInt(indIdAer) != idEscolhido) {
                                    if (posNova < posIni) {
                                        if (cursorAerobico.getInt(indPosA) >= posNova) {
                                            int pos = (cursorAerobico.getInt(indPosA) + 1);
                                            bancoDados.execSQL("UPDATE aerobicos SET pos = " + pos + " WHERE idAerobico = " + cursorAerobico.getInt(indIdAer));
                                        }
                                        if (cursorAerobico.getInt(indPosA) > posIni) {
                                            int pos = (cursorAerobico.getInt(indPosA) - 1);
                                            bancoDados.execSQL("UPDATE aerobicos SET pos = " + pos + " WHERE idAerobico = " + cursorAerobico.getInt(indIdAer));
                                        }
                                    }
                                    if (posNova > posIni) {
                                        if (cursorAerobico.getInt(indPosA) <= posNova) {
                                            int pos = (cursorAerobico.getInt(indPosA) - 1);
                                            bancoDados.execSQL("UPDATE aerobicos SET pos = " + pos + " WHERE idAerobico = " + cursorAerobico.getInt(indIdAer));
                                        }
                                        if (cursorAerobico.getInt(indPosA) < posIni) {
                                            int pos = (cursorAerobico.getInt(indPosA) + 1);
                                            bancoDados.execSQL("UPDATE aerobicos SET pos = " + pos + " WHERE idAerobico = " + cursorAerobico.getInt(indIdAer));
                                        }
                                    }
                                }
                            }
                        } finally {
                            cursorAerobico.close();
                        }
                    }
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg2));
                    txtTreino.setVisibility(View.VISIBLE);
                    txtTempoMedio.setVisibility(View.VISIBLE);
                    edtTreino.setVisibility(View.VISIBLE);
                    btnNovoAerobico.setVisibility(View.VISIBLE);
                    btnNovoExercicio.setVisibility(View.VISIBLE);
                    recuperarExercicios();
                } else if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg3));
                    txtTreino.setVisibility(View.INVISIBLE);
                    txtTempoMedio.setVisibility(View.INVISIBLE);
                    edtTreino.setVisibility(View.INVISIBLE);
                    btnNovoAerobico.setVisibility(View.INVISIBLE);
                    btnNovoExercicio.setVisibility(View.INVISIBLE);
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                } else if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    excluirExercicio(posIni);
                }
                return true;
            }
        });

        lstExercicios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                posIni = position;
                try {
                    idEscolhido = idsEx.get(position);
                    exercEscolhido = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    idEscolhido = idsAer.get(position - numIdEx);
                    exercEscolhido = false;
                }

                MyAdapter.ViewHolder vh = (MyAdapter.ViewHolder) view.getTag();

                final int touchedX = (int) (vh.lastTouchedX + 0.5f);
                final int touchedY = (int) (vh.lastTouchedY + 0.5f);

                view.startDrag(null, new View.DragShadowBuilder(view) {
                    @Override
                    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
                        shadowTouchPoint.x = touchedX;
                        shadowTouchPoint.y = touchedY;
                    }

                    @Override
                    public void onDrawShadow(Canvas canvas) {
                        super.onDrawShadow(canvas);
                    }
                }, view, 0);

                view.setVisibility(View.INVISIBLE);

                return true;
            }
        });

        btnNovoExercicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTreino.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Digite um nome para o treino", Toast.LENGTH_SHORT).show();
                } else {
                    salvar();
                    Cursor cursorIdTreino = bancoDados.rawQuery("SELECT idTreino FROM treinos WHERE treino LIKE '" + edtTreino.getText().toString() + "'", null);
                    int indIdTreino = cursorIdTreino.getColumnIndex("idTreino");
                    try {
                        while (cursorIdTreino.moveToNext()) {
                            idTreino = cursorIdTreino.getInt(indIdTreino);
                        }
                    } finally {
                        cursorIdTreino.close();
                    }
                    Intent intent = new Intent(EditarTreinoActivity.this, EditarExercicioActivity.class);
                    intent.putExtra("idExercicio", 500000);
                    intent.putExtra("idTreino", idTreino);
                    startActivity(intent);
                }

            }
        });

        btnNovoAerobico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTreino.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Digite um nome para o treino", Toast.LENGTH_SHORT).show();
                } else {
                    salvar();
                    Cursor cursorIdTreino = bancoDados.rawQuery("SELECT idTreino FROM treinos WHERE treino LIKE '" + edtTreino.getText().toString() + "'", null);
                    int indIdTreino = cursorIdTreino.getColumnIndex("idTreino");
                    try {
                        while (cursorIdTreino.moveToNext()) {
                            idTreino = cursorIdTreino.getInt(indIdTreino);
                        }
                    } finally {
                        cursorIdTreino.close();
                    }
                    Intent intent = new Intent(EditarTreinoActivity.this, EditarAerobicoActivity.class);
                    intent.putExtra("idAerobico", 500000);
                    intent.putExtra("idTreino", idTreino);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        recuperarExercicios();
    }

    @SuppressLint("SetTextI18n")
    private void recuperarExercicios() {
        numIdEx = 0;
        int tempoMedio = 0;
        exercicios = new ArrayList<>();
        idsEx = new ArrayList<>();
        idsAer = new ArrayList<>();

        Cursor cursorTreino;
        Cursor cursorExercicio;
        Cursor cursorAerobico;

        try {
            cursorTreino = bancoDados.rawQuery("SELECT treino FROM treinos WHERE idTreino =" + idTreino, null);
            int indTreino = cursorTreino.getColumnIndex("treino");
            cursorTreino.moveToFirst();
            edtTreino.setText(cursorTreino.getString(indTreino));
            cursorTreino.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            cursorExercicio = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino =" + idTreino + " ORDER BY idExercicio ASC", null);
            int indIdEx = cursorExercicio.getColumnIndex("idExercicio");
            int indPos = cursorExercicio.getColumnIndex("pos");
            while (cursorExercicio.moveToNext()) {
                if (cursorExercicio.getString(indPos) == null) {
                    bancoDados.execSQL("UPDATE exercicios SET pos = " + cursorExercicio.getInt(indIdEx) + " WHERE idExercicio = " + cursorExercicio.getInt(indIdEx));
                }
            }
            cursorExercicio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            cursorExercicio = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);
            int indExercicio = cursorExercicio.getColumnIndex("exercicio");
            int indIdEx = cursorExercicio.getColumnIndex("idExercicio");
            int indTempo = cursorExercicio.getColumnIndex("tempoMedio");
            lstExercicios.setAdapter(new MyAdapter(exercicios));
            int position = 0;
            while (cursorExercicio.moveToNext()) {
                exercicios.add(cursorExercicio.getString(indExercicio));
                bancoDados.execSQL("UPDATE exercicios SET pos = " + position + " WHERE idExercicio = " + cursorExercicio.getInt(indIdEx));
                idsEx.add(Integer.parseInt(cursorExercicio.getString(indIdEx)));
                numIdEx++;
                position++;
                int tempo = cursorExercicio.getInt(indTempo);
                if (tempo == 0) {
                    semTempo = true;
                } else {
                    if (tempoMedio == 0)
                        tempoMedio += cursorExercicio.getInt(indTempo);
                    else
                        tempoMedio += cursorExercicio.getInt(indTempo) + 150;
                }
            }
            cursorExercicio.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            cursorAerobico = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino =" + idTreino + " ORDER BY idAerobico ASC", null);
            int indIdAer = cursorAerobico.getColumnIndex("idAerobico");
            int indPos = cursorAerobico.getColumnIndex("pos");
            lstExercicios.setAdapter(new MyAdapter(exercicios));
            while (cursorAerobico.moveToNext()) {
                if (cursorAerobico.getString(indPos) == null) {
                    bancoDados.execSQL("UPDATE aerobicos SET pos = " + cursorAerobico.getInt(indIdAer) + " WHERE idAerobico = " + cursorAerobico.getInt(indIdAer));
                }
            }
            cursorAerobico.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            cursorAerobico = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino =" + idTreino + " ORDER BY pos ASC", null);
            int indAerobico = cursorAerobico.getColumnIndex("aerobico");
            int indIdAer = cursorAerobico.getColumnIndex("idAerobico");
            int indTempo = cursorAerobico.getColumnIndex("tempoMedio");

            lstExercicios.setAdapter(new MyAdapter(exercicios));
            int position = numIdEx;
            while (cursorAerobico.moveToNext()) {
                exercicios.add(cursorAerobico.getString(indAerobico));
                bancoDados.execSQL("UPDATE aerobicos SET pos = " + position + " WHERE idAerobico = " + cursorAerobico.getInt(indIdAer));
                idsAer.add(Integer.parseInt(cursorAerobico.getString(indIdAer)));
                position++;
                int tempo = cursorAerobico.getInt(indTempo);
                if (tempo == 0) {
                    semTempo = true;
                } else if (tempo == 99999999) {
                    maisAerobico = " + aeróbico";
                } else {
                    if (tempoMedio == 0)
                        tempoMedio += cursorAerobico.getInt(indTempo);
                    else
                        tempoMedio += cursorAerobico.getInt(indTempo) + 150;
                }
            }
            cursorAerobico.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (semTempo) {
            txtTempoMedio.setText("Tempo médio não definido");
            Toast.makeText(this, "Por favor, abrir e salvar os exercícios desse treino em Editar treinos para gravar o tempo médio", Toast.LENGTH_LONG).show();
        } else {
            int h = tempoMedio / 3600;
            int m = (tempoMedio - (h * 3600)) / 60;
            if (m > 9) {
                if (m >= 60) {
                    txtTempoMedio.setText("Tempo médio de treino: " + h + "h00" + maisAerobico);
                } else {
                    txtTempoMedio.setText("Tempo médio de treino: " + h + "h" + m + maisAerobico);
                }
            } else {
                txtTempoMedio.setText("Tempo médio de treino: " + h + "h0" + m + maisAerobico);
            }
        }
    }

    static class MyAdapter extends BaseAdapter implements View.OnTouchListener {
        private final List<String> mItems;

        private static class ViewHolder {
            private final TextView text;
            float lastTouchedX;
            float lastTouchedY;

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
                view.setOnTouchListener(this);
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

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ViewHolder vh = (ViewHolder) v.getTag();

            vh.lastTouchedX = event.getX();
            vh.lastTouchedY = event.getY();

            return false;
        }
    }

    private void salvar() {
        String nomeTreino = edtTreino.getText().toString();
        if (idTreino == 500000) {
            bancoDados.execSQL("INSERT INTO treinos (treino) VALUES ('" + nomeTreino + "')");
        } else {
            bancoDados.execSQL("UPDATE treinos SET treino = '" + nomeTreino + "' WHERE idTreino = " + idTreino);
        }
    }

    private void excluirExercicio(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarTreinoActivity.this);
        builder.setTitle("Excluir Exercício");
        builder.setMessage("Deseja excluir " + exercicios.get(position) + "?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    bancoDados.execSQL("DELETE FROM exercicios WHERE idExercicio =" + idsEx.get(position));
                } catch (Exception e) {
                    bancoDados.execSQL("DELETE FROM aerobicos WHERE idAerobico =" + idsAer.get(position - numIdEx));
                }
                rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg2));
                txtTreino.setVisibility(View.VISIBLE);
                edtTreino.setVisibility(View.VISIBLE);
                txtTempoMedio.setVisibility(View.VISIBLE);
                btnNovoAerobico.setVisibility(View.VISIBLE);
                btnNovoExercicio.setVisibility(View.VISIBLE);
                recuperarExercicios();
                Toast.makeText(getApplicationContext(), "Exercício excluído", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg2));
                txtTreino.setVisibility(View.VISIBLE);
                edtTreino.setVisibility(View.VISIBLE);
                txtTempoMedio.setVisibility(View.VISIBLE);
                btnNovoAerobico.setVisibility(View.VISIBLE);
                btnNovoExercicio.setVisibility(View.VISIBLE);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                rl.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg2));
                txtTreino.setVisibility(View.VISIBLE);
                edtTreino.setVisibility(View.VISIBLE);
                txtTempoMedio.setVisibility(View.VISIBLE);
                btnNovoAerobico.setVisibility(View.VISIBLE);
                btnNovoExercicio.setVisibility(View.VISIBLE);
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }
}


