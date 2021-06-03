package com.example.jammbell;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MapFragment extends Fragment {

    LatLng sydney;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

         supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

         client = LocationServices.getFusedLocationProviderClient(getActivity());

         if (ContextCompat.checkSelfPermission(getActivity(),
                 Manifest.permission.ACCESS_FINE_LOCATION)
                 == PackageManager.PERMISSION_GRANTED &&
                 ContextCompat.checkSelfPermission(getActivity(),
                         Manifest.permission.ACCESS_COARSE_LOCATION) 
         ==PackageManager.PERMISSION_GRANTED){
             //permessi concessi
             Log.d("prova", "entrato nell'if");

             getCurrentLocation();
         } else {
             //quando i permessi non sono concessi
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
         }


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng);

                        markerOptions.title(latLng.latitude + ":" + latLng.longitude);

                        map.clear();

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));

                        map.addMarker(markerOptions);

                        Log.d("posizione1", String.valueOf(sydney));


                        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    }
                });



            }
        });


        Log.d("posizione1", String.valueOf(sydney));

        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

            getCurrentLocation();
        } else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        Log.d("prova", "entrato nella funzione");

        //Inizializzazione location manager
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);

        //Controllo condizioni
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            //quando il location service è abilitato prende l'ultima posizione
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
            //inizializzazione location
                    Location location = task.getResult();
                    //controllo condizione
                    if(location != null){
                        Log.d("prova", "Latitudine" + String.valueOf(location.getLatitude()));
                        Log.d("prova", "Longitudine" + String.valueOf(location.getLongitude()));

                        sydney = new LatLng(location.getLatitude(), location.getLongitude());

                        Log.d("posizione", String.valueOf(sydney));

                  //          map.clear();
                   //    map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    //    map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


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