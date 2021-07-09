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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jammbell.View.IMap;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class MapFragment extends Fragment implements IMap
{
    //mappa
    private LatLng PosizioneCorrente;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;

    //sessione veloce
    FloatingActionButton buttonSessioneVeloce;

    //sessione guidata
    FloatingActionButton buttonSessioneGuidata;

    @Override
    public void onResume() {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull  GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    getCurrentLocation(googleMap);

                }
            }
        });
        super.onResume();
    }

    @Override
    public void onStart() {

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    getCurrentLocation(map);

                }
            }
        });

        super.onStart();
    }


    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        buttonSessioneVeloce = (FloatingActionButton) getView().findViewById(R.id.ButtonSessioneVeloce);
        buttonSessioneVeloce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SessioneVeloceActivity.class);
                startActivity(intent);
            }
        });

        buttonSessioneGuidata = (FloatingActionButton) getView().findViewById(R.id.ButtonSessioneGuidata);
        buttonSessioneGuidata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessioneGuidataDialog();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull  Context context) {
        checkPermission();
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        setHasOptionsMenu(true);

        supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //permessi concessi


            //getCurrentLocation();
        } else {
            //quando i permessi non sono concessi
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        return view;
    }

    private void SessioneGuidataDialog()
    {
        SessioneGuidataDialog createSessioneGuidata = new SessioneGuidataDialog();
        createSessioneGuidata.show(getFragmentManager(), "createsessioneguidata");
        createSessioneGuidata.setTargetFragment(MapFragment.this, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

        }
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){//Can add more as per requirement

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(GoogleMap map) {
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

                        PosizioneCorrente = new LatLng(location.getLatitude(), location.getLongitude());

                        Log.d("posizione", String.valueOf(PosizioneCorrente));

                        map.setMyLocationEnabled(true);
                        map.moveCamera(CameraUpdateFactory.newLatLng(PosizioneCorrente));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(PosizioneCorrente, 16.6f));
                        map.setMinZoomPreference(6.6f);
                        map.setMaxZoomPreference(20.20f);

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

                                PosizioneCorrente = new LatLng(location1.getLatitude(), location1.getLongitude());

                                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(GoogleMap map) {
                                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                                ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                                            getCurrentLocation(map);
                                        }
                                    }
                                });
                                map.setMyLocationEnabled(true);
                                map.moveCamera(CameraUpdateFactory.newLatLng(PosizioneCorrente));
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(PosizioneCorrente, 16.6f));
                                map.setMinZoomPreference(6.6f);
                                map.setMaxZoomPreference(20.20f);
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