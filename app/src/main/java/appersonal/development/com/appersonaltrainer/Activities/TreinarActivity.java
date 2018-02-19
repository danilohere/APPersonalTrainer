package appersonal.development.com.appersonaltrainer.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.R;
import appersonal.development.com.appersonaltrainer.Model.Treinos;

public class TreinarActivity extends AppCompatActivity {

    private ListView lstTreinos;
    private Button btnNovoTreino;
    private Button btnCorrer;
    private TextView txtAviso;
    private ArrayList<Treinos> treinos;
    private ArrayList<Integer> ids;
    private SQLiteDatabase bancoDados;

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
        setContentView(R.layout.activity_treinar);

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

        try{
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnNovoTreino = (Button) findViewById(R.id.btnNovoTreino);
        btnCorrer = (Button) findViewById(R.id.btnCorrer);
        txtAviso = (TextView) findViewById(R.id.txtAviso);
        lstTreinos = (ListView) findViewById(R.id.lstTreinos);

        lstTreinos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TreinarActivity.this,TreinoXActivity.class);
                intent.putExtra("idTreino", ids.get(position));
                startActivity(intent);
            }
        });

        btnNovoTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TreinarActivity.this, EditarTreinoActivity.class);
                startActivity(intent);
            }
        });

        btnCorrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TreinarActivity.this, AerobicoActivity.class);
                intent.putExtra("nome", "Caminhada/Corrida");
                intent.putExtra("tipoAe", 0);
                intent.putExtra("distancia", 2);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        recuperarTreinos();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ids.isEmpty()) {
                    lstTreinos.setVisibility(View.INVISIBLE);
                    btnNovoTreino.setVisibility(View.VISIBLE);
                    txtAviso.setVisibility(View.VISIBLE);
                } else {
                    lstTreinos.setVisibility(View.VISIBLE);
                    btnNovoTreino.setVisibility(View.INVISIBLE);
                    txtAviso.setVisibility(View.INVISIBLE);
                }
            }
        }, 10);
        super.onResume();
    }

    private void recuperarTreinos() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM treinos ORDER BY data ASC", null);

            int indTreino = cursor.getColumnIndex("treino");
            int indId = cursor.getColumnIndex("idTreino");
            int indData = cursor.getColumnIndex("data");

            treinos = new ArrayList<Treinos>();
            ids = new ArrayList<Integer>();

            AdapterTreinosPersonalizado adaptador = new AdapterTreinosPersonalizado(treinos, this);
            lstTreinos.setAdapter(adaptador);

            cursor.moveToFirst();

            while (cursor!=null){
                Treinos treino = new Treinos();
                treino.setNome(cursor.getString(indTreino));
                treino.setData(cursor.getLong(indData));
                treinos.add(treino);
                ids.add(Integer.parseInt(cursor.getString(indId)));
                cursor.moveToNext();
            }

        } catch (Exception e) {
        e.printStackTrace();
    }


    }

    public class AdapterTreinosPersonalizado extends BaseAdapter {

        private final List<Treinos> treinos;
        private final Activity activity;

        public AdapterTreinosPersonalizado(List<Treinos> treinos, Activity activity) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_treinos, parent, false);

            Treinos treino = treinos.get(position);

            TextView txtNomeTreino = (TextView)
                    view.findViewById(R.id.txtNome);
            TextView txtData = (TextView)
                    view.findViewById(R.id.txtData);

            txtNomeTreino.setText(treino.getNome());
            long data = treino.getData();
            SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM");
            String date = formatarData.format(data);
            if (data == 0){
                txtData.setText(" - ");
            } else {
                txtData.setText(date);
            }
            return view;
        }
    }


}
