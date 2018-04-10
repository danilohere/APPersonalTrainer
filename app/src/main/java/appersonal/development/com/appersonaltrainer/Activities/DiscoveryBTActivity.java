package appersonal.development.com.appersonaltrainer.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import appersonal.development.com.appersonaltrainer.R;

public class DiscoveryBTActivity extends AppCompatActivity {

    private ListView lstDevices;
    private TextView txtPesq;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> idsDev;

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
        setContentView(R.layout.activity_discovery_bt);

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

        lstDevices = (ListView) findViewById(R.id.lstDevices);
        txtPesq = (TextView) findViewById(R.id.txtPesq);

        idsDev = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
        lstDevices.setAdapter(adapter);
        txtPesq.setText("Pesquisando dispositivos...");

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String devName = adapter.getItem(position).toString();
                String devAddress = idsDev.get(position).toString();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("btDevName", devName);
                returnIntent.putExtra("btDevAddress", devAddress);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        /*  Este método é executado sempre que um novo dispositivo for descoberto.
         */
        public void onReceive(Context context, Intent intent) {

            /*  Obtem o Intent que gerou a ação.
                Verifica se a ação corresponde à descoberta de um novo dispositivo.
                Obtem um objeto que representa o dispositivo Bluetooth descoberto.
                Exibe seu nome e endereço na lista.
             */
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                adapter.add(device.getName());
                idsDev.add((device.getAddress()));
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                txtPesq.setText("");
            }


        }
    };

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(receiver);
    }


}
