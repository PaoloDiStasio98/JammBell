package com.appiedi.jammbell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Utente;
import com.appiedi.jammbell.View.ISessioneVeloce;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class SessioneVeloceActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ISessioneVeloce
{
    private FirebaseAuth mAuth;
    private Button ButtonStart;
    private Button ButtonStop;
    private Button ButtonPause;
    private Boolean CronometroRunning = false;
    private long pauseOffset;
    private Chronometer Cronometro;
    private GoogleMap mMap;
    private Polyline gpsTrack;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LatLng lastKnownLatLng;
    private float distanza;
    private float risultato = 0;
    private TextView KmtextView;
    private TextView CalorieTextView;
    private TextView Velocit??TextView;
    double calorie;
    private int peso_utente;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builderNotification;

    @Override
    public void onBackPressed()
    {
        notificationManager.cancel(100);
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessione_veloce);

        getSupportActionBar().setTitle("Sessione veloce");

        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        builderNotification = new NotificationCompat.Builder(this, "lembuit")
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentTitle("Sessione Veloce")
                .setContentText("Sessione in corso...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builderNotification.setOngoing(true);

        ButtonStart = findViewById(R.id.ButtonStart);
        ButtonPause = findViewById(R.id.ButtonPausa);
        ButtonStop = findViewById(R.id.ButtonStop);
        Cronometro = findViewById(R.id.Cronometro);

        KmtextView = findViewById(R.id.KmTextView);
        CalorieTextView = findViewById(R.id.CalorieTextView);
        Velocit??TextView = findViewById(R.id.VelocitaTextView);

        mAuth = FirebaseAuth.getInstance();

        //Prelevo peso utente
        Utente utente = new Utente();
        utente.getDatiUtenteDatabase(new FirestoreCallback()
        {
            @Override
            public void onCallback()
            {
                peso_utente = utente.getPeso();
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void createNotificationChannel()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            CharSequence name = "CIAOOOO";
            String description = "DESCRIZIONEEEEEE";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = null;
            channel = new NotificationChannel("lembuit", name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(12);
        gpsTrack = mMap.addPolyline(polylineOptions);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap = googleMap;

        mMap.setMinZoomPreference(6.6f);
        mMap.setMaxZoomPreference(20.20f);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null){
                            Log.i("teste", "deu");

                            LatLng atual = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atual, 18));

                        }else {
                            Log.i("teste", "n deu");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("crash", "stop");
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("crash", "pause");
        //stopLocationUpdates();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        LatLng atual = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atual, 18));

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);
        Velocit??TextView.setText(String.valueOf(df.format(location.getSpeed()*3600/1000)) + " " + "Km/h");
        updateTrack();
    }



    protected void startLocationUpdates()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                this);
    }

    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }

    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();

        for(int i = 0; i < points.size() - 1; i++)
        {
            Location loc1 = new Location("");
            loc1.setLatitude(points.get(i).latitude);
            loc1.setLongitude(points.get(i).longitude);
            Location loc2 = new Location("");
            loc2.setLatitude(points.get(i+1).latitude);
            loc2.setLongitude(points.get(i+1).longitude);
            distanza = loc1.distanceTo(loc2);
        }
        Log.d("distanza", String.valueOf(distanza));
        risultato = risultato + distanza;
        Log.d("risultato", String.valueOf(risultato));

        if(risultato != 0)
        {
            DecimalFormat df = new DecimalFormat("##.###");
            df.setRoundingMode(RoundingMode.DOWN);
            KmtextView.setText(String.valueOf(df.format(risultato / 1000)) + " " + "Km");

            DecimalFormat df1 = new DecimalFormat("##");
            df1.setRoundingMode(RoundingMode.DOWN);
            calorie = 0.75 * peso_utente * (risultato / 1000);

            CalorieTextView.setText(String.valueOf(df1.format(calorie)) + " " + "Kcal");
        }
        
        points.add(lastKnownLatLng);
        gpsTrack.setPoints(points);
    }

    public void clickButton(View v)
    {
        switch (v.getId())
        {
            case R.id.ButtonStart:

                startLocationUpdates();

                if(!CronometroRunning)
                {
                    Cronometro.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    Cronometro.start();
                    CronometroRunning = true;
                }

                notificationManager.notify(100, builderNotification.build());

                ButtonStart.setAlpha(0);
                ButtonPause.setAlpha(1);
                ButtonStop.setAlpha(1);

                break;

            case R.id.ButtonPausa:

                if(CronometroRunning)
                {
                    Cronometro.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - Cronometro.getBase();
                    CronometroRunning = false;
                }

                Log.d("cronometro1", String.valueOf((SystemClock.elapsedRealtime() - Cronometro.getBase())/1000));

                Velocit??TextView.setText("0.0 Km/h");
                stopLocationUpdates();
                ButtonPause.setAlpha(0);
                ButtonStart.setAlpha(1);
                break;

            case R.id.ButtonStop:
                Cronometro.stop();

                FirebaseUser user = mAuth.getCurrentUser();
                long tempo = (SystemClock.elapsedRealtime() - Cronometro.getBase())/1000;
                float km = risultato/1000;

               notificationManager.cancel(100);
                Log.d("buttonstop", String.valueOf(tempo));
                Log.d("buttonstop", String.valueOf(km));
                Log.d("buttonstop", String.valueOf(calorie));
                Log.d("buttonstop", String.valueOf(user.getUid()));

                Intent intent = new Intent(getBaseContext(), RiepilogoSessioneVeloceActivity.class);
                intent.putExtra("USER_ID", user.getUid());
                intent.putExtra("TEMPO", tempo);
                intent.putExtra("KM", km);
                intent.putExtra("CALORIE", calorie);

                startActivity(intent);
                break;
        }
    }
}