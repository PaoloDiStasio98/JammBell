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

    ProgressBar KmProgressBar;
    ProgressBar CalorieProgressBar;
    ProgressBar VelocitaProgessBar;


    Double KmTot = 0.0;
    Double CalorieTot = 0.0;
    Double Velocitamedia = 0.0;
    int countdocument = 0;

    ArrayList<String> StatisticheCreatore = new ArrayList<>();
    ArrayList<String> StatistichePartecipante = new ArrayList<>();
    int countStatistiche = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reso_contochallenge);

        KmProgressBar = findViewById(R.id.KmProgressBar);
        VelocitaProgessBar = findViewById(R.id.velocitaProgressBar);
        CalorieProgressBar = findViewById(R.id.CalorieProgressBar);

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
                  Log.d("nomegara", nomegara);
                  usernameCreatore = String.valueOf(document.get("UsernameCreatore"));
                  usernamePartecipante = String.valueOf(document.get("UsernamePartecipante"));
                  stato = String.valueOf(document.get("Stato"));

                  nomeGaraTextView.setText("Nome gara: " + nomegara);

                  PullIDUtente(usernameCreatore);
                  PullIDUtente(usernamePartecipante);
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

    public ArrayList<String> PullSessioni(String username){

        countdocument = 0;
        KmTot = 0.0;
        CalorieTot = 0.0;
        Velocitamedia = 0.0;


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ArrayList<String> Statistiche = new ArrayList<>();
        Statistiche.clear();
        if(currentUser != null){
            db.collection("SessioneVeloce")
                    .whereEqualTo("UserID", username)
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
                                            Log.d("date", "entrato");
                                            Log.d("dateorario", orarioFineSessione);
                                            Log.d("dateorario", orarioInizioGara);
                                            if (orarioFineSessione.compareTo(orarioInizioGara) >= 0) {
                                                Log.d("date", "entrato nache qui");
                                                //Statistiche Totali
                                                KmTot = KmTot + (double) document.get("Km");
                                                CalorieTot = CalorieTot + (long) document.get("Calorie");
                                                Velocitamedia = (Velocitamedia + (double) document.get("Velocita"));

                                                Log.d("statisticheKm", String.valueOf(KmTot));
                                                countdocument++;


                                            }
                                        }
                                        else {

                                            KmTot = KmTot + (double) document.get("Km");
                                            CalorieTot = CalorieTot + (long) document.get("Calorie");
                                            Velocitamedia = (Velocitamedia + (double) document.get("Velocita"));

                                            Log.d("statisticheKm", String.valueOf(KmTot));
                                            countdocument++;
                                        }
                                    }

                                }

                                Statistiche.add(String.valueOf(KmTot));
                                Statistiche.add(String.valueOf(CalorieTot));
                                Statistiche.add(String.valueOf(Velocitamedia / countdocument));


                                if(countStatistiche == 0) {
                                    StatistichePartecipante.clear();
                                    StatisticheCreatore.clear();

                                    StatisticheCreatore = Statistiche;
                                    Log.d("STATcreatore1", String.valueOf(StatisticheCreatore));

                                    countStatistiche++;
                                }
                                else {
                                    countStatistiche = 0;
                                    StatistichePartecipante.clear();
                                    StatistichePartecipante = Statistiche;

                                    Log.d("STATcreatore", String.valueOf(StatisticheCreatore));
                                    Log.d("STATpartecipante", String.valueOf(StatistichePartecipante));
                                    Log.d("STATcreatore", String.valueOf(StatisticheCreatore.get(0)));
                                    Log.d("STATpartecipante", String.valueOf(StatistichePartecipante.get(0)));


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



                                    countStatistiche = 0;
                                }







                                countdocument = 0;
                                KmTot = 0.0;
                                CalorieTot = 0.0;
                                Velocitamedia = 0.0;

                                Log.d("statistiche", Statistiche.toString());


                            }

                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }






                        }
                    });


        }
        else {
            Log.d("utenteid", "niente vuoto");
        }

        return Statistiche;
    }


    private void PullIDUtente(String username) {
        mAuth = FirebaseAuth.getInstance();
        final String[] UsernameID = new String[1];
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            db.collection("Utente")
                    .whereEqualTo("Username", username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {


                                        UsernameID[0] = String.valueOf(document.get("IDUtente"));
                                        Log.d("statisticheUsernamer", UsernameID[0]);

                                        PullSessioni(UsernameID[0]);


                                }

                            }

                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }






                        }
                    });


        }
        else {
            Log.d("utenteid", "niente vuoto");
        }

    }


    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}