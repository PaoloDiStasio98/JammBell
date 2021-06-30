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
    TextView risultatoTextView;

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
    int countrisultatocreatore = 0;
    int countrisultatopartecipante = 0;

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
        risultatoTextView = findViewById(R.id.RisultatoTextView);
        username1TextView = findViewById(R.id.Username1TextView);
        username2TextView = findViewById(R.id.Username2TextView);

        String IDgara = getIntent().getStringExtra("IDGara");

        Log.d("gara", IDgara);

        nomeGaraTextView = findViewById(R.id.NomeGaraTextView);

        PullGara(IDgara);





    }

    public interface FirestoreCallback {
        void onPullCreatoreCallback();
    }

    public interface FirestoreCallback1 {
        void onPullSessioniCallback();
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


                  PullSessioniCreatore(new FirestoreCallback() {
                      @Override
                      public void onPullCreatoreCallback() {

                          Log.d("gara3 countdocument", String.valueOf(countdocument));

                          StatisticheCreatore.add(String.valueOf(KmTot1));
                          StatisticheCreatore.add(String.valueOf(CalorieTot1));
                          StatisticheCreatore.add(String.valueOf(Velocitamedia1 / countdocument));

                          Log.d("STATISTICHE CREATORE", String.valueOf(StatisticheCreatore));



                          PullSessioniPartecipante(new FirestoreCallback1() {
                              @RequiresApi(api = Build.VERSION_CODES.N)
                              @Override
                              public void onPullSessioniCallback() {

                                  Log.d("STATISTICHE DATI", String.valueOf(KmTot2) + " " + String.valueOf(CalorieTot2));


                                  StatistichePartecipante.add(String.valueOf(KmTot2));
                                  StatistichePartecipante.add(String.valueOf(CalorieTot2));
                                  StatistichePartecipante.add(String.valueOf(Velocitamedia2 / countdocument));

                                  Log.d("STATISTICHE PARTECIPANT", String.valueOf(StatistichePartecipante));

                                  popolaProgressBar();
                              }
                          });
                      }
                  });

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

    public void PullSessioniCreatore(FirestoreCallback firestoreCallback){

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
                                firestoreCallback.onPullCreatoreCallback();

                            }

                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
    }

    public void PullSessioniPartecipante(FirestoreCallback1 firestoreCallback){
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
                            firestoreCallback.onPullSessioniCallback();





                        }

                        else
                        {
                            Log.d("database", "Error getting documents: ", task.getException());
                        }


                    }


                });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void popolaProgressBar(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KmProgressBar.setMin(0);
            CalorieProgressBar.setMin(0);
            VelocitaProgessBar.setMin(0);
        }

        String KmCreatoreString = StatisticheCreatore.get(0);
        float KmCreatore = Float.parseFloat(KmCreatoreString);

        String KmPartecipanteString = StatistichePartecipante.get(0);
        float KmPartecipante = Float.parseFloat(KmPartecipanteString);

        if((int) (KmCreatore + KmPartecipante) == 0) {
            KmProgressBar.setMax(100);
            KmProgressBar.setProgress(50, true);
        }
        else{
            KmProgressBar.setMax((int) (KmCreatore + KmPartecipante));
            KmProgressBar.setProgress((int) KmCreatore, true);

        }

        String VelocitaCreatoreString = StatisticheCreatore.get(2);
        float VelocitaCreatore = Float.parseFloat(VelocitaCreatoreString);

        String VelocitaPartecipanteString = StatistichePartecipante.get(2);
        float VelocitaPartecipante = Float.parseFloat(VelocitaPartecipanteString);

        if((int) (VelocitaCreatore + VelocitaPartecipante) == 0) {
            VelocitaProgessBar.setMax(100);
            VelocitaProgessBar.setProgress(50, true);
        }
        else{
            VelocitaProgessBar.setMax((int) (VelocitaCreatore + VelocitaPartecipante));
            VelocitaProgessBar.setProgress((int) VelocitaCreatore, true);

        }

        String CalorieCreatoreString = StatisticheCreatore.get(1);
        float CalorieCreatore = Float.parseFloat(CalorieCreatoreString);

        String CaloriePartecipanteString = StatistichePartecipante.get(1);
        float CaloriePartecipante = Float.parseFloat(CaloriePartecipanteString);

        if((int) (CalorieCreatore + CaloriePartecipante) == 0) {
            CalorieProgressBar.setMax(100);
            CalorieProgressBar.setProgress(50, true);
            CalorieProgressBar.animate().setDuration(10000000);
        }
        else{
            CalorieProgressBar.setMax((int) (CalorieCreatore + CaloriePartecipante));
            CalorieProgressBar.setProgress((int) CalorieCreatore, true);

        }

        if((int) KmCreatore > KmPartecipante) {
            countrisultatocreatore++;
        }
        else if((int) KmCreatore < KmPartecipante)
           countrisultatopartecipante++;

        if((int) VelocitaCreatore > VelocitaPartecipante) {
            countrisultatocreatore++;
        }
        else if((int) VelocitaCreatore < VelocitaPartecipante)
            countrisultatopartecipante++;

        if((int) CalorieCreatore > CaloriePartecipante) {
            countrisultatocreatore++;
        }
        else if((int) CalorieCreatore < CaloriePartecipante)
            countrisultatopartecipante++;

        risultatoTextView.setText(countrisultatocreatore + "-" + countrisultatopartecipante);

    }



    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}