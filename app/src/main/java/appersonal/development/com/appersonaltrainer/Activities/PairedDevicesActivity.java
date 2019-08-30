package appersonal.development.com.appersonaltrainer.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Set;

import appersonal.development.com.appersonaltrainer.Controller.ConnectionThread;
import appersonal.development.com.appersonaltrainer.R;

public class PairedDevicesActivity extends AppCompatActivity {

    private ListView lstDevices;
    public static int SELECT_DISCOVERED_DEVICE = 3;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayAdapter<String> adapter;
    private ArrayList<String> idsDev;
    static ConnectionThread connect;
    private String devName;
    private String devAddress;

    private Handler handler = new Handler();
    private Runnable run;

    @Override
    public void onBackPressed() {
        btAdapter.disable();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_devices);

        //Implementa o botão voltar na ActionBar
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Implementa o ad na activity
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

        Button btnPesq = findViewById(R.id.btnPesq);
        lstDevices = findViewById(R.id.lstDevices);

        adicionarLista();

        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String devName = adapter.getItem(position);
                String devAddress = idsDev.get(position);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("btDevName", devName);
                returnIntent.putExtra("btDevAddress", devAddress);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        btnPesq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverDevices();
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

    public void discoverDevices() {
        Intent searchPairedDevicesIntent = new Intent(this, DiscoveryBTActivity.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_DISCOVERED_DEVICE) {
            if (resultCode == RESULT_OK) {
                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();

                Toast.makeText(this, "Aguarde o pareamento do dispositivo", Toast.LENGTH_LONG).show();
                devName = data.getStringExtra("btDevName");
                devAddress = data.getStringExtra("btDevAddress");
                run = new Runnable() {
                    @Override
                    public void run() {
                        adicionarLista();
                        boolean encontrado = false;
                        for (int i = 0; i < idsDev.size(); i++) {
                            if (devAddress.equals(idsDev.get(i))) {
                                encontrado = true;
                                i = 100000;
                            } else {
                                encontrado = false;
                            }
                        }
                        if (encontrado) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("btDevName", devName);
                            returnIntent.putExtra("btDevAddress", devAddress);
                            setResult(RESULT_OK, returnIntent);
                            handler.removeCallbacks(run);
                            finish();
                        } else {
                            handler.postDelayed(run, 500);
                        }
                    }
                };
                handler.post(run);
            } else {
                Toast.makeText(this, "Nenhum dispositivo selecionado", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void adicionarLista() {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        idsDev = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
        lstDevices.setAdapter(adapter);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device.getName());
                idsDev.add(device.getAddress());
            }
        }
    }

}
