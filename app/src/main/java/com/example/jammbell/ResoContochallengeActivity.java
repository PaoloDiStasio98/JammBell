package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
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

    HashMap<String, String> Datamap = new HashMap<>();


    TextView nomeGaraTextView;


    Double KmTot = 0.0;
    Double CalorieTot = 0.0;
    Double Velocitamedia = 0.0;
    int countdocument = 0;

    ArrayList<String> StatisticheCreatore = new ArrayList<>();
    ArrayList<String> StatistichePartecipante = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reso_contochallenge);

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

                  Log.d("document", String.valueOf(document));
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
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    Datamap = (HashMap<String, String>) document.get("Data");

                                    //Statistiche Totali
                                    KmTot = KmTot + (double) document.get("Km");
                                    CalorieTot = CalorieTot + (long) document.get("Calorie");
                                    Velocitamedia = (Velocitamedia + (double) document.get("Velocita")) ;

                                    Log.d("statisticheKm", String.valueOf(KmTot));
                                    countdocument++;
                                }

                                Statistiche.add(String.valueOf(KmTot));
                                Statistiche.add(String.valueOf(CalorieTot));
                                Statistiche.add(String.valueOf(Velocitamedia / countdocument));

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

    private String PullIDUtente(String username) {

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

        return UsernameID[0];

    }


    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}