package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SplashScreenActivity extends Activity {

    FusedLocationProviderClient client;
    LatLng PosizioneCorrente;


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        getCurrentLocation();
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                Intent intent = new Intent(SplashScreenActivity.this, Main2Activity.class);
                intent.putExtra("POSIZIONE", PosizioneCorrente);
                startActivity(intent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Log.d("prova", "entrato nella funzione");

        //Inizializzazione location manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Log.d("prova1", "entrato nella funzione");

        //Controllo condizioni
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.d("prova2", "entrato nella funzione");

            //quando il location service è abilitato prende l'ultima posizione
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Log.d("prova3", "entrato nella funzione");

                    //inizializzazione location
                    Location location = task.getResult();
                    //controllo condizione
                    if(location != null){
                        Log.d("prova", "Latitudine" + String.valueOf(location.getLatitude()));
                        Log.d("prova", "Longitudine" + String.valueOf(location.getLongitude()));

                        PosizioneCorrente = new LatLng(location.getLatitude(), location.getLongitude());

                        Log.d("posizione", String.valueOf(PosizioneCorrente));




                    }
                    else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                Log.d("prova", "Latitudine" + String.valueOf(location1.getLatitude()));
                                Log.d("prova", "Longitudine" + String.valueOf(location1.getLongitude()));

                            }
                        };

                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            //quando il location service non è abilitato, apri location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }


    }

}