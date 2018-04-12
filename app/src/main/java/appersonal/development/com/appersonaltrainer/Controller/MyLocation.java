package appersonal.development.com.appersonaltrainer.Controller;


import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MyLocation {
    private Timer timer1;
    private LocationManager lm;
    LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private Context context1;


    public void getLocation(Context context, LocationResult result) {
        context1 = context;
        //É usado o callback LocationResult para passar as coordenadas para o codigo do usuario.
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //se o provedor de localizacao nao estiver habilitado, teremos uma excecao.
        try {
            if (lm != null) {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
        } catch (Exception ignored) {
        }
        try {
            if (lm != null) {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception ignored) {
        }

        //Codigo para nao tentar fazer a leitura sem provedor de localizacao disponivel
        if (!gps_enabled && !network_enabled)
            return;

            if (gps_enabled) {
                if (ContextCompat.checkSelfPermission(context1,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    VerificarPermissao();
                } else {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                }
            }
            if (network_enabled) {
                if (ContextCompat.checkSelfPermission(context1,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    VerificarPermissao();
                } else {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
                }
            }
            timer1 = new Timer();
            timer1.schedule(new GetLastLocation(), 10000);



//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context1, "Aqui", Toast.LENGTH_SHORT).show();
//                // add your code here
//                lm.removeUpdates(locationListenerGps);
//                lm.removeUpdates(locationListenerNetwork);
//                Location net_loc=null, gps_loc=null;
//
//                if (gps_enabled) {
//                    if (ContextCompat.checkSelfPermission(context1,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        VerificarPermissao();
//                    } else {
//                        gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    }
//                }
//                if (network_enabled) {
//                    if (ContextCompat.checkSelfPermission(context1,
//                            Manifest.permission.ACCESS_FINE_LOCATION)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        VerificarPermissao();
//                    } else {
//                        net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    }
//                }
//
//                //se tiver os dois valores, usar o mais atualizado
//                if(gps_loc!=null && net_loc!=null){
//                    if(gps_loc.getTime()>net_loc.getTime())
//                        locationResult.gotLocation(gps_loc);
//                    else {
//                        try{
//                            locationResult.gotLocation(net_loc);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                    return;
//                }
//
//                if(gps_loc!=null){
//                    locationResult.gotLocation(gps_loc);
//                    return;
//                }
//                if(net_loc!=null){
//                    try{
//                        locationResult.gotLocation(net_loc);
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                    return;
//                }
//                locationResult.gotLocation(null);
//            }
//        }, 3000);

    }


    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
            lm.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "Passou aqui");

//            Toast.makeText(context1, "Aqui", Toast.LENGTH_SHORT).show();
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);
            Location net_loc=null, gps_loc=null;

                if (gps_enabled) {
                    Log.i(TAG, "Entrou no GPS");
                    if (ContextCompat.checkSelfPermission(context1,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        VerificarPermissao();
                    } else {
                        gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Log.i(TAG, "Pegou localização");
                    }
                }
                if (network_enabled) {
                    Log.i(TAG, "Entrou no Network");
                    if (ContextCompat.checkSelfPermission(context1,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        VerificarPermissao();
                    } else {
                        net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.i(TAG, "Pegou localização");
                    }
                }

            //se tiver os dois valores, usar o mais atualizado
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime()) {
                    locationResult.gotLocation(gps_loc);
                    Log.i(TAG, "usou o gps");
                } else {
                    try{
                        locationResult.gotLocation(net_loc);
                        Log.i(TAG, "Usou a net");
                    } catch (Exception e){
                        Log.i(TAG, "Deu erro");
                        e.printStackTrace();
                    }
                }
                return;
            } else if(gps_loc!=null){
                Log.i(TAG, "Pegou só gps");
                locationResult.gotLocation(gps_loc);
                return;
            } else if(net_loc!=null){
                try{
                    Log.i(TAG, "Pegou só net");
                    locationResult.gotLocation(net_loc);
                } catch (Exception e){
                    e.printStackTrace();
                }
                return;
            } else {
                Log.i(TAG, "Pegou nada");
                locationResult.gotLocation(null);
            }
        }
    }

//    class GetLastLocation extends Thread {
//        public Handler h;
//
//        public void run() {
//            lm.removeUpdates(locationListenerGps);
//            lm.removeUpdates(locationListenerNetwork);
//            Location net_loc=null, gps_loc=null;
//
//            if (gps_enabled) {
//                if (ContextCompat.checkSelfPermission(context1,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    VerificarPermissao();
//                } else {
//                    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                }
//            }
//            if (network_enabled) {
//                if (ContextCompat.checkSelfPermission(context1,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    VerificarPermissao();
//                } else {
//                    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                }
//            }
//
//            //se tiver os dois valores, usar o mais atualizado
//            if(gps_loc!=null && net_loc!=null){
//                if(gps_loc.getTime()>net_loc.getTime())
//                    locationResult.gotLocation(gps_loc);
//                else {
//                    try{
//                        locationResult.gotLocation(net_loc);
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                return;
//            }
//
//            if(gps_loc!=null){
//                locationResult.gotLocation(gps_loc);
//                return;
//            }
//            if(net_loc!=null){
//                try{
//                    locationResult.gotLocation(net_loc);
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//                return;
//            }
//            locationResult.gotLocation(null);
//        }
//    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }

    private void VerificarPermissao(){
        // Should we show an explanation?
        if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context1,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions((Activity) context1,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
    }

}