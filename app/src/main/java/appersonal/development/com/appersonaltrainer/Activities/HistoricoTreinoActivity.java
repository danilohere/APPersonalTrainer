package appersonal.development.com.appersonaltrainer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import appersonal.development.com.appersonaltrainer.R;
import appersonal.development.com.appersonaltrainer.Model.Treinos;

public class HistoricoTreinoActivity extends AppCompatActivity {

    private ListView lstHistorico;
    private ListView lstMeses;
    private int lstposition;
    private int positionMes;
    private ArrayList<Treinos> treinos;
    private ArrayList<String> meses;
    private ArrayList<Integer> ids;
    private SQLiteDatabase bancoDados;
    private AlertDialog alerta;
    private String treino;
    ShareDialog shareDialog = new ShareDialog(this);

    @Override
    public void onBackPressed() {
        if (lstHistorico.getVisibility() == View.VISIBLE) {
            lstHistorico.setVisibility(View.INVISIBLE);
            lstMeses.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem compartilhar = menu.add("Compartilhar");
        final MenuItem excluir = menu.add("Excluir treino");

        compartilhar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=appersonal.development.com.appersonaltrainer"))
//                        .setContentUrl(Uri.parse("https://www.facebook.com/appersonaltrainer1"))
                        .setQuote("Acabei de fazer " + treino + " com o APPersonal Trainer!")
                        .build();
                shareDialog.show(content);
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
        setContentView(R.layout.activity_historico_treino);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("5E35E760A0E16547F564991F0C23CAC9")
                .addTestDevice("A74671A8A3250600B0E5121898AC7400")
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

        lstHistorico = findViewById(R.id.lstHistorico);
        lstMeses = findViewById(R.id.lstMeses);
        Button btnApagar = findViewById(R.id.btnApagar);
        lstHistorico.setVisibility(View.INVISIBLE);

        btnApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoricoTreinoActivity.this);
                builder.setTitle("Apagar Histórico");
                builder.setMessage("Deseja apagar o histórico de todos os meses?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (lstMeses.getVisibility() == View.VISIBLE) {
                            bancoDados.execSQL("DELETE FROM historico");
                        } else {
                            bancoDados.execSQL("DELETE FROM historico");
                            lstMeses.setVisibility(View.VISIBLE);
                            lstHistorico.setVisibility(View.INVISIBLE);
                        }
                        recuperarMeses();
                        Toast.makeText(HistoricoTreinoActivity.this, "Histórico apagado", Toast.LENGTH_SHORT).show();
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

        lstMeses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lstHistorico.setVisibility(View.VISIBLE);
                lstMeses.setVisibility(View.INVISIBLE);
                positionMes = position;
                recuperarTreinos(meses.get(position));
            }
        });

        lstHistorico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lstposition = position;
                treino = treinos.get(position).getNome();
                registerForContextMenu(parent);
                openContextMenu(parent);
            }
        });

    }

    @Override
    protected void onResume() {
        recuperarMeses();
        super.onResume();
    }

    private void recuperarMeses() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM historico ORDER BY data DESC", null);
            int indTreino = cursor.getColumnIndex("treinoH");
            int indData = cursor.getColumnIndex("data");
            meses = new ArrayList<>();
            lstMeses.setAdapter(new MyAdapter(meses));
            while (cursor.moveToNext()) {
                Treinos treino = new Treinos();
                treino.setNome(cursor.getString(indTreino));
                treino.setData(cursor.getLong(indData));
                long data = treino.getData();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");
                String date;
                switch (formatarMes.format(data)) {
                    case "01":
                        date = "Janeiro/" + formatarAno.format(data);
                        break;
                    case "02":
                        date = "Fevereiro/" + formatarAno.format(data);
                        break;
                    case "03":
                        date = "Março/" + formatarAno.format(data);
                        break;
                    case "04":
                        date = "Abril/" + formatarAno.format(data);
                        break;
                    case "05":
                        date = "Maio/" + formatarAno.format(data);
                        break;
                    case "06":
                        date = "Junho/" + formatarAno.format(data);
                        break;
                    case "07":
                        date = "Julho/" + formatarAno.format(data);
                        break;
                    case "08":
                        date = "Agosto/" + formatarAno.format(data);
                        break;
                    case "09":
                        date = "Setembro/" + formatarAno.format(data);
                        break;
                    case "10":
                        date = "Outubro/" + formatarAno.format(data);
                        break;
                    case "11":
                        date = "Novembro/" + formatarAno.format(data);
                        break;
                    default:
                        date = "Dezembro/" + formatarAno.format(data);
                        break;
                }
                int i = 0;
                int igual = 0;
                while (i < meses.size()) {
                    if (meses.get(i).equals(date)) {
                        igual = 1;
                    }
                    i++;
                }
                if (igual == 0) {
                    meses.add(date);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recuperarTreinos(String mesTreino) {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM historico ORDER BY data DESC", null);

            int indTreino = cursor.getColumnIndex("treinoH");
            int indData = cursor.getColumnIndex("data");
            int indIdHistorico = cursor.getColumnIndex("idHistorico");

            treinos = new ArrayList<>();
            ids = new ArrayList<>();
            AdapterTreinosPersonalizado adaptador = new AdapterTreinosPersonalizado(treinos, this);
            lstHistorico.setAdapter(adaptador);

            while (cursor.moveToNext()) {
                long data = cursor.getLong(indData);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarMes = new SimpleDateFormat("MM");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarAno = new SimpleDateFormat("yyyy");
                String date;
                switch (formatarMes.format(data)) {
                    case "01":
                        date = "Janeiro/" + formatarAno.format(data);
                        break;
                    case "02":
                        date = "Fevereiro/" + formatarAno.format(data);
                        break;
                    case "03":
                        date = "Março/" + formatarAno.format(data);
                        break;
                    case "04":
                        date = "Abril/" + formatarAno.format(data);
                        break;
                    case "05":
                        date = "Maio/" + formatarAno.format(data);
                        break;
                    case "06":
                        date = "Junho/" + formatarAno.format(data);
                        break;
                    case "07":
                        date = "Julho/" + formatarAno.format(data);
                        break;
                    case "08":
                        date = "Agosto/" + formatarAno.format(data);
                        break;
                    case "09":
                        date = "Setembro/" + formatarAno.format(data);
                        break;
                    case "10":
                        date = "Outubro/" + formatarAno.format(data);
                        break;
                    case "11":
                        date = "Novembro/" + formatarAno.format(data);
                        break;
                    default:
                        date = "Dezembro/" + formatarAno.format(data);
                        break;
                }
                if (date.equals(mesTreino)) {
                    Treinos treino = new Treinos();
                    treino.setNome(cursor.getString(indTreino));
                    treino.setData(cursor.getLong(indData));
                    ids.add(cursor.getInt(indIdHistorico));
                    treinos.add(treino);
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void excluirTreino(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir Treino do histórico");
        builder.setMessage("Deseja excluir " + treinos.get(position).getNome() + " do histórico?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bancoDados.execSQL("DELETE FROM historico WHERE idHistorico =" + ids.get(position));
                recuperarTreinos(meses.get(positionMes));
                Toast.makeText(getApplicationContext(), "Treino excluído do histórico", Toast.LENGTH_SHORT).show();
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

    static class MyAdapter extends BaseAdapter implements View.OnTouchListener {
        private List<String> mItems;

        private static class ViewHolder {
            public TextView text;
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

    public class AdapterTreinosPersonalizado extends BaseAdapter {

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder") View view = activity.getLayoutInflater()
                    .inflate(R.layout.lista_historico, parent, false);

            Treinos treino = treinos.get(position);

            TextView txtNomeTreino =
                    view.findViewById(R.id.txtNome);
            TextView txtData =
                    view.findViewById(R.id.txtData);
            TextView txtHora =
                    view.findViewById(R.id.txtHora);

            txtNomeTreino.setText(treino.getNome());
            long data = treino.getData();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM");
            String date = formatarData.format(data);
            txtData.setText(date);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatarHora = new SimpleDateFormat("HH:mm");
            String hora = formatarHora.format(data);
            txtHora.setText(hora);
            return view;
        }
    }

}
