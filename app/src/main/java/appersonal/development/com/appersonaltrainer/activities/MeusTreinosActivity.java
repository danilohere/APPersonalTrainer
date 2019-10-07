package appersonal.development.com.appersonaltrainer.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import appersonal.development.com.appersonaltrainer.controller.Compress;
import appersonal.development.com.appersonaltrainer.R;

public class MeusTreinosActivity extends AppCompatActivity {

    private final static int QRcodeWidth = 500;

    private ListView lstTreinos;
    private RelativeLayout lytCompartilhar;
    private ImageView imgQR;
    private ArrayList<String> treinos;
    private ArrayList<Integer> ids;
    private String nomeTreino;
    private SQLiteDatabase bancoDados;
    private int lstposition;

    private final Handler handler = new Handler();

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

                    imgQR.setImageDrawable(getResources().getDrawable(R.drawable.qrcode));
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
        lstTreinos = findViewById(R.id.lstTreinos);
        registerForContextMenu(lstTreinos);
        Button btnNovo = findViewById(R.id.btnNovo);
        Button btnCopiar = findViewById(R.id.btnCopiar);
        lytCompartilhar = findViewById(R.id.lytCompartilhar);
        Button btnFechar = findViewById(R.id.btnFechar);
        imgQR = findViewById(R.id.imgQR);
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

        btnCopiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        Toast.makeText(MeusTreinosActivity.this, "Escaneie o QRCodeActivity de outro dispositivo", Toast.LENGTH_SHORT).show();
                        IntentIntegrator integrator = new IntentIntegrator(MeusTreinosActivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setOrientationLocked(true);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(false);
                        integrator.initiateScan();

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
        AlertDialog alerta = builder.create();
        alerta.show();
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
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
            Cursor cursorE = bancoDados.rawQuery("SELECT * FROM exercicios WHERE idTreino = " + ids.get(position) + " ORDER BY pos ASC", null);
            cursorE.moveToFirst();
            int count = cursorE.getCount();
            for (int i=0; i <= count; i++){
                int indExercicio = cursorE.getColumnIndex("exercicio");
                int indSeries = cursorE.getColumnIndex("series");
                int indTipoRep = cursorE.getColumnIndex("tipoRep");
                int indRep1 = cursorE.getColumnIndex("rep1");
                int indRep2 = cursorE.getColumnIndex("rep2");
                int indRep3 = cursorE.getColumnIndex("rep3");
                int indRep4 = cursorE.getColumnIndex("rep4");
                int indRep5 = cursorE.getColumnIndex("rep5");
                int indRep6 = cursorE.getColumnIndex("rep6");
                int indRep7 = cursorE.getColumnIndex("rep7");
                int indRep8 = cursorE.getColumnIndex("rep8");
                int indTempoExecucao = cursorE.getColumnIndex("tempoExecucao");
                int indDescansoM = cursorE.getColumnIndex("descansoM");
                int indDescansoS = cursorE.getColumnIndex("descansoS");
                int indUnilateral = cursorE.getColumnIndex("unilateral");
                int indMusculoSpinner = cursorE.getColumnIndex("musculoSpinner");
                int indExercicioSpinner = cursorE.getColumnIndex("exercicioSpinner");
                int indObs = cursorE.getColumnIndex("obs");
                int indTempoMedio = cursorE.getColumnIndex("tempoMedio");

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
            Cursor cursorA = bancoDados.rawQuery("SELECT * FROM aerobicos WHERE idTreino = " + ids.get(position) + " ORDER BY pos ASC", null);
            cursorA.moveToFirst();
            int count = cursorA.getCount();
            for (int i = 0; i <= count; i++) {
                int indAerobico = cursorA.getColumnIndex("aerobico");
                int indDuracaoH = cursorA.getColumnIndex("duracaoH");
                int indDuracaoM = cursorA.getColumnIndex("duracaoM");
                int indDuracaoS = cursorA.getColumnIndex("duracaoS");
                int indASeries = cursorA.getColumnIndex("series");
                int indADescansoM = cursorA.getColumnIndex("descansoM");
                int indADescansoS = cursorA.getColumnIndex("descansoS");
                int indDistancia = cursorA.getColumnIndex("distancia");
                int indATempoMedio = cursorA.getColumnIndex("tempoMedio");
                int indKM = cursorA.getColumnIndex("km");

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
            Bitmap bitmap = TextToImageEncode(mensagem);

            imgQR.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
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

    static class MyAdapter extends BaseAdapter {
        private final List<String> mItems;

        private static class ViewHolder {
            private final TextView text;

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
