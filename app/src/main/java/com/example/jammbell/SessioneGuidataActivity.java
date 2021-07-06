package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SessioneGuidataActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    int minutiCamminata, minutiCorsa, numeroRipetizioni;

    TextView cronometroTextView;
    TextView spiegazioneLivello;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    Button ButtonStart;
    Button ButtonStop;
    int flag;

    Boolean CronometroRunning = false;
    long pauseOffset;


    Chronometer Cronometro;

    private GoogleMap mMap;

    private Polyline gpsTrack;
    private SupportMapFragment mapFragment;
    private GoogleApiClient googleApiClient;
    private LatLng lastKnownLatLng;
    private float distanza;
    private float risultato = 0;

    private TextView KmtextView;
    double calorie;
    String peso;

    TextView indicazioniSessione;



    private LocationCallback locationCallback;

    NotificationManagerCompat notificationManager;

    NotificationCompat.Builder builderNotification;

    @Override
    public void onBackPressed() {
        notificationManager.cancel(100);
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessione_guidata);

        getSupportActionBar().setTitle("Sessione guidata");
        minutiCamminata = getIntent().getIntExtra("Minuti_camminata", 3);
        minutiCorsa = getIntent().getIntExtra("Minuti_corsa", 3);
        numeroRipetizioni = getIntent().getIntExtra("Numero_ripetizioni", 3);

        indicazioniSessione = findViewById(R.id.indicazioniSessione);
        spiegazioneLivello = findViewById(R.id.spiegazioneLivello);

        spiegazioneLivello.setText("Ripetizioni: " + numeroRipetizioni + "     Corsa: " + minutiCorsa + "'     Camminata: " + minutiCamminata +"'");

        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        builderNotification = new NotificationCompat.Builder(this, "lembuit")
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentTitle("Sessione Guidata")
                .setContentText("Sessione in corso...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builderNotification.setOngoing(true);


        ButtonStart = findViewById(R.id.ButtonStart);
        ButtonStop = findViewById(R.id.ButtonStop);
       // Cronometro = findViewById(R.id.Cronometro);
        cronometroTextView = findViewById(R.id.cronometroTextView);


        KmtextView = findViewById(R.id.KmTextView);

        mAuth = FirebaseAuth.getInstance();
        accessodatabase();



        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }






    }

    private void createNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

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

    private void accessodatabase() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("utenteid", currentUser.getUid());

            db.collection("Utente")
                    .whereEqualTo("IDUtente", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    peso = document.get("Peso").toString();
                                    Log.d("database", document.getId() + " => " + document.getData() + " "  + " " + document.get("Altezza"));
                                }
                            } else {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
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
                .addOnSuccessListener(new OnSuccessListener<Location>() {
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
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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
        updateTrack();
    }



    protected void startLocationUpdates() {
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

    protected void stopLocationUpdates() {
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

        if(risultato != 0) {

            DecimalFormat df = new DecimalFormat("##.###");
            df.setRoundingMode(RoundingMode.DOWN);
            KmtextView.setText(String.valueOf(df.format(risultato / 1000)) + " " + "Km");

            DecimalFormat df1 = new DecimalFormat("##");
            df1.setRoundingMode(RoundingMode.DOWN);
            calorie = 0.75 * Integer.parseInt(peso) * (risultato / 1000);
            Log.d("pesocalorie", String.valueOf(peso));
            Log.d("pesocalo", String.valueOf(calorie));


        }

        points.add(lastKnownLatLng);
        gpsTrack.setPoints(points);
    }

    public void Vibrazione(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void setCountDown(){
        final int[] numRip = {0};

        new CountDownTimer(minutiCamminata*60*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String minutiCamminataRimenenti = formatSecondDateTime((int) (millisUntilFinished/1000));
                indicazioniSessione.setText("Cammina! " + minutiCamminataRimenenti);
            }

            public void onFinish() {
                Vibrazione();
                indicazioniSessione.setText("Corri!");
                new CountDownTimer(minutiCorsa*60*1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        String minutiCorsaRimenenti = formatSecondDateTime((int) (millisUntilFinished/1000));
                        indicazioniSessione.setText("Corri! " + minutiCorsaRimenenti);
                    }

                    public void onFinish() {
                        Vibrazione();
                        indicazioniSessione.setText("");
                        numRip[0]++;
                        int numeroRip = numeroRipetizioni - 1;
                        spiegazioneLivello.setText("Ripetizioni: " + numeroRip + "     Corsa: " + minutiCorsa + "'     Camminata: " + minutiCamminata +"'");


                        if (numRip[0] == numeroRipetizioni){
                            indicazioniSessione.setText("finito");

                             FirebaseUser user = mAuth.getCurrentUser();
                             float km = risultato/1000;

                            long tempoTotale = (minutiCamminata + minutiCorsa) * numeroRipetizioni;
                            notificationManager.cancel(100);
                            Log.d("buttonstop", String.valueOf(tempoTotale*60));
                            Log.d("buttonstop", String.valueOf(km));
                            Log.d("buttonstop", String.valueOf(calorie));
                            Log.d("buttonstop", String.valueOf(user.getUid()));


                            Intent intent = new Intent(getBaseContext(), RiepilogoSessioneVeloceActivity.class);
                            intent.putExtra("USER_ID", user.getUid());
                            intent.putExtra("TEMPO", tempoTotale*60);
                            intent.putExtra("KM", km);
                            intent.putExtra("CALORIE", calorie);

                            startActivity(intent);

                        }
                        else{
                            setCountDown();
                        }
                    }
                }.start();
            }
        }.start();
    }

    public static String formatSecondDateTime(int scound) {
        if(scound <= 0)return "";
        int h = scound / 3600;
        int m = scound % 3600 / 60;
        int s = scound % 60; // Less than 60 is the second, enough 60 is the minute
        if(m<10 && s<10)
            return "0"+m+":0"+s+"";
        if(m < 10)
            return "0"+m+":"+s+"";
        if(s < 10)
            return m+":0"+s+"";

        return m+":"+s+"";

    }

    public void sessioneGuidata(){

        setCountDown();

        int tempoTotale = (minutiCamminata + minutiCorsa) * numeroRipetizioni;

        new CountDownTimer(tempoTotale*60*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String TempoRimanente = formatSecondDateTime((int) (millisUntilFinished/1000));
                cronometroTextView.setText(TempoRimanente);
            }

            public void onFinish() {
                cronometroTextView.setText("done!");
            }
        }.start();


    }

    public void clickButton(View v) {

        switch (v.getId()) {
            case R.id.ButtonStart:

                startLocationUpdates();


                sessioneGuidata();

                notificationManager.notify(100, builderNotification.build());

                ButtonStart.setAlpha(0);
                ButtonStop.setVisibility(View.VISIBLE);

                break;

            case R.id.ButtonStop:

                new AlertDialog.Builder(this)
                        .setTitle("Attenzione")
                        .setMessage("Se termini la sessione prima del previsto i tuoi dati non saranno salvati. \n Sei sicuro di uscire?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                                startActivity(intent);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("No", null)
                        .show();


                break;

        }
    }
}