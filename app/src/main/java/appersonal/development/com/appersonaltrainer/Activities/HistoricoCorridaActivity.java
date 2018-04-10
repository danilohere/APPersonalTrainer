package appersonal.development.com.appersonaltrainer.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.Model.Corrida;
import appersonal.development.com.appersonaltrainer.R;

public class HistoricoCorridaActivity extends AppCompatActivity {

    private ListView lstHistorico;
    private ArrayList<Corrida> corridas;
    private Button btnApagar;
    private SQLiteDatabase bancoDados;
    private AlertDialog alerta;
    ShareDialog shareDialog = new ShareDialog(this);

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
        setContentView(R.layout.activity_historico_corrida);

        //Implementa o botão voltar na ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView.loadAd(adRequest);

        AdView adView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
                .build();
        adView2.loadAd(adRequest2);

        try{
            bancoDados = openOrCreateDatabase("appersonal", MODE_PRIVATE,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lstHistorico = (ListView) findViewById(R.id.lstHistorico);
        btnApagar = (Button) findViewById(R.id.btnApagar);

        btnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoricoCorridaActivity.this);
                builder.setTitle("Apagar Histórico");
                builder.setMessage("Tem certeza que deseja apagar o histórico?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bancoDados.execSQL("DELETE FROM historicokm");
                        recuperarTreinos();
                        Toast.makeText(HistoricoCorridaActivity.this, "Histórico apagado", Toast.LENGTH_SHORT).show();
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
        });

        lstHistorico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoricoCorridaActivity.this);
                builder.setTitle("Compartilhar no Facebook");
                builder.setMessage("Deseja compartilhar essa corrida no Facebook?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String kmS = corridas.get(position).getKm().toString().replaceAll("KM","");
                        kmS = kmS.replaceAll(",",".");
                        Double km = Double.parseDouble(kmS);
                        int hora = Integer.parseInt(corridas.get(position).getTempo().toString().substring(0, 1));
                        int min = Integer.parseInt(corridas.get(position).getTempo().toString().substring(2, 4));
                        int seg = Integer.parseInt(corridas.get(position).getTempo().toString().substring(5, 7));
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
        });
    }

    @Override
    protected void onResume() {
        recuperarTreinos();
        super.onResume();
    }

    private void recuperarTreinos() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM historicokm ORDER BY data DESC", null);

            int indKM = cursor.getColumnIndex("km");
            int indtempo = cursor.getColumnIndex("tempo");
            int indData = cursor.getColumnIndex("data");

            corridas = new ArrayList<Corrida>();

            AdapterTreinosPersonalizado adaptador = new AdapterTreinosPersonalizado(corridas, this);

            lstHistorico.setAdapter(adaptador);

            cursor.moveToFirst();
            while (cursor!=null){
                Corrida corrida = new Corrida();
                corrida.setKm(cursor.getString(indKM));
                corrida.setTempo(cursor.getString(indtempo));
                corrida.setData(cursor.getLong(indData));
                corridas.add(corrida);
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public class AdapterTreinosPersonalizado extends BaseAdapter {

        private final List<Corrida> corridas;
        private final Activity activity;

        public AdapterTreinosPersonalizado(ArrayList<Corrida> corridas, Activity activity) {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_historico_corrida, parent, false);

            Corrida corrida = corridas.get(position);

            TextView txtKMTempo = (TextView)
                    view.findViewById(R.id.txtKMTempo);
            TextView txtData = (TextView)
                    view.findViewById(R.id.txtData);
            TextView txtVelocidade = (TextView)
                    view.findViewById(R.id.txtVelocidade);
            TextView txtHora = (TextView)
                    view.findViewById(R.id.txtHora);

            txtKMTempo.setText(corrida.getKm()+" em "+corrida.getTempo());
            long data = corrida.getData();
            SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM");
            String date = formatarData.format(data);
            txtData.setText(date);
            SimpleDateFormat formatarHora = new SimpleDateFormat("HH:mm");
            String horario = formatarHora.format(data);
            txtHora.setText(horario);
            int hora = Integer.parseInt(corrida.getTempo().toString().substring(0, 1));
            int min = Integer.parseInt(corrida.getTempo().toString().substring(2, 4));
            int seg = Integer.parseInt(corrida.getTempo().toString().substring(5, 7));
            int tempoA = (hora*3600) + (min*60) + seg;
            String skm = corrida.getKm().toString().replaceAll(" KM", "");
            Double km = Double.parseDouble(skm.replaceAll(",","."));
            Double kmph;
            if (km < 0.01){
                kmph = 0.0;
            } else {
                kmph = ((km * 1000) / tempoA) * 3.6;
            }
            txtVelocidade.setText("Velocidade média: "+formatar(kmph)+" km/h");

            return view;
        }
    }

    Double formatar (Double a){
        NumberFormat format = new DecimalFormat("#.##");
        String num = format.format(a).replaceAll(",",".");
        Double b = Double.parseDouble(num);
        return b;
    }

}
