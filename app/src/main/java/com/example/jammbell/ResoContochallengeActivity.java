package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;



public class ResoContochallengeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String datainizio;
    String datafine;
    String nomegara;
    String usernameCreatore;
    String usernamePartecipante;
    String stato;

    HashMap<String, String> DatamapSessione = new HashMap<>();
    HashMap<String, String> DatamapGara = new HashMap<>();


    TextView nomeGaraTextView;
    TextView username1TextView;
    TextView username2TextView;

    ProgressBar KmProgressBar;
    ProgressBar CalorieProgressBar;
    ProgressBar VelocitaProgessBar;


    Double KmTot1 = 0.0;
    Double CalorieTot1 = 0.0;
    Double Velocitamedia1 = 0.0;

    Double KmTot2 = 0.0;
    Double CalorieTot2 = 0.0;
    Double Velocitamedia2 = 0.0;

    int countdocument = 0;

    ArrayList<String> StatisticheCreatore = new ArrayList<>();
    ArrayList<String> StatistichePartecipante = new ArrayList<>();
    int countStatistiche = 0;

    String IDcreatore;
    String IDpartecipante;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reso_contochallenge);

        KmProgressBar = findViewById(R.id.KmProgressBar);
        VelocitaProgessBar = findViewById(R.id.velocitaProgressBar);
        CalorieProgressBar = findViewById(R.id.CalorieProgressBar);

        username1TextView = findViewById(R.id.Username1TextView);
        username2TextView = findViewById(R.id.Username2TextView);

        String IDgara = getIntent().getStringExtra("IDGara");

        Log.d("gara", IDgara);

        nomeGaraTextView = findViewById(R.id.NomeGaraTextView);

        PullGara(IDgara);





    }


    public void PullGara(String idgara) {



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

      db.collection("Gara").document(idgara).get()
              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
              if (task.isSuccessful()) {

                  DocumentSnapshot document = task.getResult();


                  DatamapGara = (HashMap<String, String>) document.get("Data");
                  datainizio = String.valueOf(document.get("Datainizio"));
                  datafine = String.valueOf(document.get("Datafine"));

                  nomegara = String.valueOf(document.get("Nome"));
                  usernameCreatore = String.valueOf(document.get("UsernameCreatore"));
                  usernamePartecipante = String.valueOf(document.get("UsernamePartecipante"));
                  stato = String.valueOf(document.get("Stato"));
                  IDcreatore = String.valueOf(document.get("IDcreatore"));
                  IDpartecipante = String.valueOf(document.get("IDpartecipante"));


                  Log.d("STATISTICHE NOMI", IDcreatore + " " + IDpartecipante);

                  username1TextView.setText(usernameCreatore);
                  username2TextView.setText(usernamePartecipante);
                  nomeGaraTextView.setText("Nome gara: " + nomegara);

                  PullSessioniCreatore();
                  PullSessioniPartecipante();

              }
              else {

              }

          }
      });
    }

    public String ConversioneDate(HashMap Data){

        String dataSessione = null;

        String stringmese = String.valueOf(Data.get("monthValue"));
        int mese = Integer.parseInt(stringmese);

        String stringgiorno = String.valueOf(Data.get("dayOfMonth"));
        int giorno = Integer.parseInt(stringgiorno);

        if(mese < 10)
            dataSessione = String.valueOf(Data.get("year")) + "-0" + String.valueOf(Data.get("monthValue")) + "-" +  String.valueOf(Data.get("dayOfMonth"));
        if(giorno < 10)
            dataSessione = String.valueOf(Data.get("year")) + "-" + String.valueOf(Data.get("monthValue")) + "-0" +  String.valueOf(Data.get("dayOfMonth"));
        if(mese < 10 && giorno < 10)
            dataSessione = String.valueOf(Data.get("year")) + "-0" + String.valueOf(Data.get("monthValue")) + "-0" +  String.valueOf(Data.get("dayOfMonth"));
        if(mese > 10 && giorno > 10)
            dataSessione = String.valueOf(Data.get("year")) + "-" + String.valueOf(Data.get("monthValue")) + "-" +  String.valueOf(Data.get("dayOfMonth"));

        return dataSessione;
    }

    public String getOrario(HashMap Data){
        String orarioMH = null;

        String Stringora = String.valueOf(Data.get("hour"));
        int ora = Integer.parseInt(Stringora);

        String Stringminuto = String.valueOf(Data.get("minute"));
        int minuto = Integer.parseInt(Stringminuto);

        if(minuto < 10)
            orarioMH = Stringora + ":0" + Stringminuto;
        if(ora < 10)
            orarioMH = "0" + Stringora + ":" + Stringminuto;
        if(ora < 10 && minuto < 10)
            orarioMH = "0" + Stringora + ":0" + Stringminuto;
        if(ora > 10 && minuto > 10)
            orarioMH = Stringora + ":" + Stringminuto;

        Log.d("orario", "prova funzione" + orarioMH);
        return orarioMH;
    }

    public void PullSessioniCreatore(){

        countdocument = 0;
        KmTot1 = 0.0;
        CalorieTot1 = 0.0;
        Velocitamedia1 = 0.0;

        mAuth = FirebaseAuth.getInstance();
            db.collection("SessioneVeloce")
                    .whereEqualTo("UserID", IDcreatore)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    DatamapSessione = (HashMap<String, String>) document.get("Data");
                                    Log.d("DatamapSessione", DatamapSessione.toString());
                                    Log.d("DatamapGara", DatamapGara.toString());

                                    String dataSessione = ConversioneDate(DatamapSessione);


                                    if(dataSessione.compareTo(datainizio) >= 0 && dataSessione.compareTo(datafine) <= 0) {

                                        String orarioFineSessione = getOrario(DatamapSessione);
                                        String orarioInizioGara = getOrario(DatamapGara);


                                        Log.d("date", dataSessione + " poi  " + datainizio);
                                        if (dataSessione.equals(datainizio)) {
                                            if (orarioFineSessione.compareTo(orarioInizioGara) >= 0) {

                                                //Statistiche Totali
                                                KmTot1 = KmTot1 + (double) document.get("Km");
                                                CalorieTot1 = CalorieTot1 + (long) document.get("Calorie");
                                                Velocitamedia1 = (Velocitamedia1 + (double) document.get("Velocita"));

                                                Log.d("gara2", "km: " + String.valueOf(KmTot1) + "Cal " + String.valueOf(CalorieTot1));
                                                countdocument++;


                                            }
                                        }
                                        else {

                                            KmTot1 = KmTot1 + (double) document.get("Km");
                                            CalorieTot1 = CalorieTot1 + (long) document.get("Calorie");
                                            Velocitamedia1 = (Velocitamedia1 + (double) document.get("Velocita"));

                                            Log.d("gara2", "km: " + String.valueOf(KmTot1) + "Cal " + String.valueOf(CalorieTot1));
                                            countdocument++;
                                        }
                                    }

                                }

                                Log.d("gara3 countdocument", String.valueOf(countdocument));

                                StatisticheCreatore.add(String.valueOf(KmTot1));
                                StatisticheCreatore.add(String.valueOf(CalorieTot1));
                                StatisticheCreatore.add(String.valueOf(Velocitamedia1 / countdocument));

                                Log.d("STATISTICHE CREATORE", String.valueOf(StatisticheCreatore));


                            }

                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
    }

    public void PullSessioniPartecipante(){
        countdocument = 0;
        KmTot2 = 0.0;
        CalorieTot2 = 0.0;
        Velocitamedia2 = 0.0;

        mAuth = FirebaseAuth.getInstance();
        db.collection("SessioneVeloce")
                .whereEqualTo("UserID", IDpartecipante)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                DatamapSessione = (HashMap<String, String>) document.get("Data");
                                Log.d("DatamapSessione", DatamapSessione.toString());
                                Log.d("DatamapGara", DatamapGara.toString());

                                String dataSessione = ConversioneDate(DatamapSessione);


                                if(dataSessione.compareTo(datainizio) >= 0 && dataSessione.compareTo(datafine) <= 0) {

                                    String orarioFineSessione = getOrario(DatamapSessione);
                                    String orarioInizioGara = getOrario(DatamapGara);


                                    Log.d("date", dataSessione + " poi  " + datainizio);
                                    if (dataSessione.equals(datainizio)) {
                                        if (orarioFineSessione.compareTo(orarioInizioGara) >= 0) {

                                            //Statistiche Totali
                                            KmTot2 = KmTot2 + (double) document.get("Km");
                                            CalorieTot2 = CalorieTot2 + (long) document.get("Calorie");
                                            Velocitamedia2 = (Velocitamedia2 + (double) document.get("Velocita"));

                                            Log.d("gara2", "km: " + String.valueOf(KmTot2) + "Cal " + String.valueOf(CalorieTot2));
                                            countdocument++;


                                        }
                                    }
                                    else {

                                        KmTot2 = KmTot2 + (double) document.get("Km");
                                        CalorieTot2 = CalorieTot2 + (long) document.get("Calorie");
                                        Velocitamedia2 = (Velocitamedia2 + (double) document.get("Velocita"));

                                        Log.d("gara2", "km: " + String.valueOf(KmTot2) + "Cal " + String.valueOf(CalorieTot2));
                                        countdocument++;
                                    }
                                }

                            }

                            Log.d("STATISTICHE DATI", String.valueOf(KmTot2) + " " + String.valueOf(CalorieTot2));


                            StatistichePartecipante.add(String.valueOf(KmTot2));
                            StatistichePartecipante.add(String.valueOf(CalorieTot2));
                            StatistichePartecipante.add(String.valueOf(Velocitamedia2 / countdocument));

                            Log.d("STATISTICHE PARTECIPANTE", String.valueOf(StatistichePartecipante));

                        }

                        else
                        {
                            Log.d("database", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}