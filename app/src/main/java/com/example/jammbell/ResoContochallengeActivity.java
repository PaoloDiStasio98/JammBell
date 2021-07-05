package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    TextView contoRovesciaTextView;
    TextView messaggioVincitoreTextView;

    ProgressBar KmProgressBar;
    ProgressBar CalorieProgressBar;
    ProgressBar VelocitaProgessBar;


    Double KmTot1 = 0.0;
    Double CalorieTot1 = 0.0;
    Double Velocitamedia1 = 0.0;

    Double KmTot2 = 0.0;
    Double CalorieTot2 = 0.0;
    Double Velocitamedia2 = 0.0;

    int countdocument2 = 0;
    int countdocument = 0;
    int countrisultatocreatore = 0;
    int countrisultatopartecipante = 0;

    ArrayList<String> StatisticheCreatore = new ArrayList<>();
    ArrayList<String> StatistichePartecipante = new ArrayList<>();
    int countStatistiche = 0;

    String IDcreatore;
    String IDpartecipante;

    Button SessioneVeloceButton;

    String StatoGara;
    String IDDocumentoGara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reso_contochallenge);

        getSupportActionBar().setTitle("Resoconto gara");

        KmProgressBar = findViewById(R.id.KmProgressBar);
        VelocitaProgessBar = findViewById(R.id.velocitaProgressBar);
        CalorieProgressBar = findViewById(R.id.CalorieProgressBar);
        risultatoTextView = findViewById(R.id.RisultatoTextView);
        username1TextView = findViewById(R.id.Username1TextView);
        username2TextView = findViewById(R.id.Username2TextView);
        contoRovesciaTextView = findViewById(R.id.ContoRovesciaGaraTextView);
        SessioneVeloceButton = findViewById(R.id.SessioneVeloceButton);
        messaggioVincitoreTextView = findViewById(R.id.MessaggioFineTextView);

        String IDgara = getIntent().getStringExtra("IDGara");
        StatoGara = getIntent().getStringExtra("STATO");


        Log.d("gara", IDgara);

        nomeGaraTextView = findViewById(R.id.NomeGaraTextView);

        PullGara(IDgara);



        SessioneVeloceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SessioneVeloceActivity.class);
                startActivity(intent);
            }
        });

        IDDocumentoGara = IDgara;


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
        countdocument2 = 0;
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
                                            countdocument2++;


                                        }
                                    }
                                    else {

                                        KmTot2 = KmTot2 + (double) document.get("Km");
                                        CalorieTot2 = CalorieTot2 + (long) document.get("Calorie");
                                        Velocitamedia2 = (Velocitamedia2 + (double) document.get("Velocita"));

                                        Log.d("gara2", "km: " + String.valueOf(KmTot2) + "Cal " + String.valueOf(CalorieTot2));
                                        countdocument2++;
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

        Log.d("PunteggioBarra", String.valueOf((int) (VelocitaCreatore + VelocitaPartecipante)));

        if((int) (VelocitaCreatore + VelocitaPartecipante) == 0) {
            VelocitaProgessBar.setMax(100);
            VelocitaProgessBar.setProgress(50, true);
        }
        else{
            Log.d("PunteggioBarra", String.valueOf((int) (VelocitaCreatore + VelocitaPartecipante)));
            Log.d("PunteggioBarra", String.valueOf((int) (VelocitaCreatore)));

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

        if((int) KmCreatore > (int) KmPartecipante) {
            countrisultatocreatore++;
            Log.d("Punteggio", "punto km creatore");
        }
        else if((int) KmCreatore < (int) KmPartecipante) {
            countrisultatopartecipante++;
            Log.d("Punteggio", "punto km paretipante");
        }


        if((int) VelocitaCreatore > (int) VelocitaPartecipante) {
            countrisultatocreatore++;
            Log.d("Punteggio", "punto vel creatore");

        }
        else if((int) VelocitaCreatore < (int) VelocitaPartecipante) {
            countrisultatopartecipante++;
            Log.d("Punteggio", "punto vel partecipante");
        }

        if((int) CalorieCreatore > (int) CaloriePartecipante) {
            countrisultatocreatore++;
            Log.d("Punteggio", "punto cal creatore");

        }
        else if((int) CalorieCreatore < (int) CaloriePartecipante) {
            countrisultatopartecipante++;
            Log.d("Punteggio", "punto cal partecipante");

        }
        risultatoTextView.setText(countrisultatocreatore + "-" + countrisultatopartecipante);

        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime now = LocalDateTime.now();
            Log.d("data", dtf.format(now));

            datainizio = datainizio + " 00:00";
            Log.d("datainiziomodificata", datainizio);

            datafine = datafine + " 23:59";
            Log.d("datafinemodificata", datainizio);

            if(StatoGara.matches("Attiva")) {
                if (datainizio.compareTo(dtf.format(now)) < 0 && datafine.compareTo(dtf.format(now)) > 0) {
                    Date datefine = null;
                    try {
                        datefine = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(datafine);
                        Log.d("datafinemodificata2", String.valueOf(datefine));
                        int milliseconds = (int) (datefine.getTime() - System.currentTimeMillis());
                        Log.d("datafine", String.valueOf(datefine.getTime() - System.currentTimeMillis()));
                        Log.d("millisecondi", String.valueOf(milliseconds));

                        int days = milliseconds / (1000 * 60 * 60 * 24);
                        int hour = (milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);

                        if (milliseconds < 0) {
                            contoRovesciaTextView.setText("Manca ancora molto al termine della gara");
                        } else {
                            if (days > 1 && hour > 1)
                                contoRovesciaTextView.setText(String.valueOf(days) + " giorni e " + String.valueOf(hour) + " ore");
                            if (days == 1)
                                contoRovesciaTextView.setText(String.valueOf(days) + " giorno e " + String.valueOf(hour) + " ore");
                            if (days < 1 && hour == 1)
                                contoRovesciaTextView.setText(String.valueOf(hour) + " ora");
                            if (days < 1 && hour > 1)
                                contoRovesciaTextView.setText(String.valueOf(hour) + " ore");
                            if (days < 1 && hour < 1)
                                contoRovesciaTextView.setText("Manca meno di un'ora");


                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } else {
                    contoRovesciaTextView.setText("La gara ancora deve iniziare");
                }
            }
            else {
                contoRovesciaTextView.setText("La gara è finita");


                if(countrisultatocreatore > countrisultatopartecipante) {
                    messaggioVincitoreTextView.setText("Il vincitore della gara è " + usernameCreatore + "!");
                    String risultato = countrisultatocreatore + "-" + countrisultatopartecipante;
                    pushRisultato(risultato, usernameCreatore);
                }
                if(countrisultatocreatore < countrisultatopartecipante) {
                    messaggioVincitoreTextView.setText("Il vincitore della gara è " + usernamePartecipante + "!");
                    String risultato = countrisultatopartecipante + "-" + countrisultatocreatore;
                    pushRisultato(risultato, usernamePartecipante);
                }
                if(countrisultatocreatore == countrisultatopartecipante) {
                    messaggioVincitoreTextView.setText("La gara è finita in pareggio!");
                    String risultato = countrisultatopartecipante + "-" + countrisultatocreatore;
                    pushRisultato(risultato, "Pareggio");
                }

                SessioneVeloceButton.setText("Torna alla home");
                SessioneVeloceButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                        startActivity(intent);
                    }
                });
            }
        }


    }

    public void pushRisultato(String risultato, String vincitore){

        db.collection("Gara")
                .document(IDDocumentoGara)
                .update(
                        "Risultato", risultato,
                        "UsernameVincitore", vincitore
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });

    }



    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}