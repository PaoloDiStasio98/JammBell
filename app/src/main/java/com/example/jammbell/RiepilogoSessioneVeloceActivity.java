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


public class RiepilogoSessioneVeloceActivity extends AppCompatActivity {

     TextView TempoTextView;
     TextView KmTextView;
     TextView CalorieTextView;
     TextView PassiTextView;
     TextView VelocitaMediaTextView;

     ImageView Stella1;
     ImageView Stella2;
     ImageView Stella3;
     ImageView Stella4;
     ImageView Stella5;

     Button buttonConferma;

    Map<String, Object> SessioneVeloce = new HashMap<>();

    int valutazione = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riepilogo_sessione_veloce);

        TempoTextView = (TextView) findViewById(R.id.TempoTextView);
        KmTextView = (TextView) findViewById(R.id.KmPercorsiTextView);
        CalorieTextView = (TextView) findViewById(R.id.CalorieBruciateTextView);
        PassiTextView = (TextView) findViewById(R.id.PassiTextView);
        VelocitaMediaTextView = (TextView) findViewById(R.id.VelocitaMediaTextView);

        Stella1 = findViewById(R.id.Stella1);
        Stella2 = findViewById(R.id.Stella2);
        Stella3 = findViewById(R.id.Stella3);
        Stella4 = findViewById(R.id.Stella4);
        Stella5 = findViewById(R.id.Stella5);

        buttonConferma = findViewById(R.id.buttonConferma);


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
        velocitamedia = Km / ((float) Tempo / 3600);

        Log.d("calcolo", String.valueOf( (float) Tempo/3600));


        int tempoint = (int) Tempo;
        int calorieint = (int) Calorie;

      String tempostringformat = formatSecondDateTime(tempoint);



        TempoTextView.setText(tempostringformat);
        KmTextView.setText(String.valueOf(df.format(Km)) + " Km" );
        CalorieTextView.setText(String.valueOf(calorieint) + " Kcal");
        PassiTextView.setText(String.valueOf(passi));
        VelocitaMediaTextView.setText(String.valueOf(df1.format(velocitamedia)) + " Km/h");

        buttonConferma.setOnClickListener(new View.OnClickListener() {
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

                db.collection("SessioneVeloce").add(SessioneVeloce).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                {
                    @Override
                    public void onSuccess(DocumentReference documentReference)
                    {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        startActivity(new Intent(RiepilogoSessioneVeloceActivity.this, Main2Activity.class));
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.w("TAG", "Error adding document", e);
                    }
                });
            }
        });

    }

    public void clickImage(View v) {

        switch (v.getId()) {
            case R.id.Stella1:
                valutazione = 1;
                Stella1.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella2.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                Stella3.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                Stella4.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                Stella5.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                break;

            case R.id.Stella2:
                valutazione = 2;
                Stella1.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella2.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella3.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                Stella4.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                Stella5.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                break;

            case R.id.Stella3:
                valutazione = 3;
                Stella1.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella2.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella3.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella4.setBackgroundResource(R.drawable.ic_baseline_star_border_24);
                Stella5.setBackgroundResource(R.drawable.ic_baseline_star_border_24);

                break;

            case R.id.Stella4:
                valutazione = 4;
                Stella1.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella2.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella3.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella4.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella5.setBackgroundResource(R.drawable.ic_baseline_star_border_24);



                break;
            case R.id.Stella5:
                valutazione = 5;
                Stella1.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella2.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella3.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella4.setBackgroundResource(R.drawable.ic_baseline_star_24);
                Stella5.setBackgroundResource(R.drawable.ic_baseline_star_24);


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