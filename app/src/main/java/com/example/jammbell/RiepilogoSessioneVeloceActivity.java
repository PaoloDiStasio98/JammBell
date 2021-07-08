package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jammbell.Controller.SessioneVeloce;
import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Sessione;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RiepilogoSessioneVeloceActivity extends AppCompatActivity
{
     private TextView TempoTextView;
     private TextView KmTextView;
     private TextView CalorieTextView;
     private TextView PassiTextView;
     private TextView VelocitaMediaTextView;

     private ImageView Stella1;
     private ImageView Stella2;
     private ImageView Stella3;
     private ImageView Stella4;
     private ImageView Stella5;

     private Button buttonConferma;

     private Map<String, Object> SessioneVeloce = new HashMap<>();

     private int valutazione = 0;

     private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onBackPressed() {
        return;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_sessione_veloce);

        TempoTextView         =  findViewById(R.id.TempoTextView);
        KmTextView            =  findViewById(R.id.KmPercorsiTextView);
        CalorieTextView       =  findViewById(R.id.CalorieBruciateTextView);
        PassiTextView         =  findViewById(R.id.PassiTextView);
        VelocitaMediaTextView =  findViewById(R.id.VelocitaMediaTextView);

        Stella1 = findViewById(R.id.Stella1);
        Stella2 = findViewById(R.id.Stella2);
        Stella3 = findViewById(R.id.Stella3);
        Stella4 = findViewById(R.id.Stella4);
        Stella5 = findViewById(R.id.Stella5);

        buttonConferma = findViewById(R.id.buttonConferma);


        getSupportActionBar().setTitle("Riepilogo sessione");

        String userId = getIntent().getStringExtra("USER_ID");
        long Tempo = getIntent().getLongExtra("TEMPO", 0);
        float Km = getIntent().getFloatExtra("KM", 0);
        double Calorie = getIntent().getDoubleExtra("CALORIE", 0);

        Log.d("riepologo", String.valueOf(Tempo));
        Log.d("riepologo", String.valueOf(Km));
        Log.d("riepologo", String.valueOf(Calorie));
        Log.d("riepologo", String.valueOf(userId));

        DecimalFormat df = new DecimalFormat("###.###");
        df.setRoundingMode(RoundingMode.DOWN);

        DecimalFormat df1 = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);

        int passi;
        passi = (int) ((Km * 1000) / 0.6);


        double velocitamedia;

        if(Tempo > 0)
            velocitamedia = Km / ((float) Tempo / 3600);
        else
            velocitamedia = 0.0;

        Log.d("calcolo", String.valueOf( (float) Tempo/3600));


        int tempoint = (int) Tempo;
        int calorieint = (int) Calorie;

        String tempostringformat = formatSecondDateTime(tempoint);

        TempoTextView.setText(tempostringformat);
        KmTextView.setText(String.valueOf(df.format(Km)) + " Km" );
        CalorieTextView.setText(String.valueOf(calorieint) + " Kcal");
        PassiTextView.setText(String.valueOf(passi));
        VelocitaMediaTextView.setText(String.valueOf(df1.format(velocitamedia)) + " Km/h");

        buttonConferma.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                DateTimeFormatter dtf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    Log.d("data", dtf.format(now));
                    SessioneVeloce.put("Data", now);
                }

                SessioneVeloce.put("Tempo", tempoint);
                SessioneVeloce.put("Calorie", calorieint);
                SessioneVeloce.put("Km", Km);
                SessioneVeloce.put("Passi", passi);
                SessioneVeloce.put("Velocita", velocitamedia);
                SessioneVeloce.put("Feedback", valutazione);
                SessioneVeloce.put("UserID", userId);

                Log.d("SessioneVeloce", String.valueOf(SessioneVeloce));
                SessioneVeloce sessioneVeloce = new SessioneVeloce();
                sessioneVeloce.pushSessione(SessioneVeloce, new FirestoreCallback()
                {
                    @Override
                    public void onCallback()
                    {
                        startActivity(new Intent(RiepilogoSessioneVeloceActivity.this, Main2Activity.class));
                    }
                });
            }
        });

    }

    public void clickImage(View v) {

        switch (v.getId()) {
            case R.id.Stella1:
                valutazione = 1;
                Stella1.setBackgroundResource(R.drawable.stella_bianca);
                Stella2.setBackgroundResource(R.drawable.stella_bordo_bianca);
                Stella3.setBackgroundResource(R.drawable.stella_bordo_bianca);
                Stella4.setBackgroundResource(R.drawable.stella_bordo_bianca);
                Stella5.setBackgroundResource(R.drawable.stella_bordo_bianca);
                break;

            case R.id.Stella2:
                valutazione = 2;
                Stella1.setBackgroundResource(R.drawable.stella_bianca);
                Stella2.setBackgroundResource(R.drawable.stella_bianca);
                Stella3.setBackgroundResource(R.drawable.stella_bordo_bianca);
                Stella4.setBackgroundResource(R.drawable.stella_bordo_bianca);
                Stella5.setBackgroundResource(R.drawable.stella_bordo_bianca);
                break;

            case R.id.Stella3:
                valutazione = 3;
                Stella1.setBackgroundResource(R.drawable.stella_bianca);
                Stella2.setBackgroundResource(R.drawable.stella_bianca);
                Stella3.setBackgroundResource(R.drawable.stella_bianca);
                Stella4.setBackgroundResource(R.drawable.stella_bordo_bianca);
                Stella5.setBackgroundResource(R.drawable.stella_bordo_bianca);

                break;

            case R.id.Stella4:
                valutazione = 4;
                Stella1.setBackgroundResource(R.drawable.stella_bianca);
                Stella2.setBackgroundResource(R.drawable.stella_bianca);
                Stella3.setBackgroundResource(R.drawable.stella_bianca);
                Stella4.setBackgroundResource(R.drawable.stella_bianca);
                Stella5.setBackgroundResource(R.drawable.stella_bordo_bianca);



                break;
            case R.id.Stella5:
                valutazione = 5;
                Stella1.setBackgroundResource(R.drawable.stella_bianca);
                Stella2.setBackgroundResource(R.drawable.stella_bianca);
                Stella3.setBackgroundResource(R.drawable.stella_bianca);
                Stella4.setBackgroundResource(R.drawable.stella_bianca);
                Stella5.setBackgroundResource(R.drawable.stella_bianca);


                break;

        }
    }



    public static String formatSecondDateTime(int scound) {
        if(scound <= 0)return "";
        int h = scound / 3600;
        int m = scound % 3600 / 60;
        int s = scound % 60; // Less than 60 is the second, enough 60 is the minute
        if(m<10 && s<10)
            return h+":0"+m+":0"+s+"";
        if(m < 10)
            return h+":0"+m+":"+s+"";
        if(s < 10)
            return h+":"+m+":0"+s+"";

        return h+":"+m+":"+s+"";

    }


}