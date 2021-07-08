package com.example.jammbell.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Sessione
{
    private ArrayList<Long>                   Passi            = new ArrayList<>();
    private ArrayList<Long>                   Calorie_Bruciate = new ArrayList<>();
    private ArrayList<Long>                   Tempo            = new ArrayList<>();
    private ArrayList<Long>                   Feedback         = new ArrayList<>();
    private ArrayList<Double>                 Km_Percorsi      = new ArrayList<>();
    private ArrayList<Double>                 Velocita_Media   = new ArrayList<>();
    private ArrayList<HashMap<String,String>> Data             = new ArrayList<>();
    private ArrayList<String>                 IDUtente         = new ArrayList<>();

    public ArrayList<Long> getPassi() {
        return Passi;
    }

    public ArrayList<Long> getCalorie_Bruciate() {
        return Calorie_Bruciate;
    }

    public ArrayList<Long> getTempo() {
        return Tempo;
    }

    public ArrayList<Long> getFeedback() {
        return Feedback;
    }

    public ArrayList<Double> getKm_Percorsi() {
        return Km_Percorsi;
    }

    public ArrayList<Double> getVelocita_Media() {
        return Velocita_Media;
    }

    public ArrayList<HashMap<String, String>> getData() {
        return Data;
    }

    public ArrayList<String> getIDUtente() {
        return IDUtente;
    }

    public void getDatiSessioneDatabase()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            db.collection("SessioneVeloce")
                    .whereEqualTo("UserID", currentUser.getUid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable  FirebaseFirestoreException error)
                        {
                            if (error != null)
                            {
                                Log.w("TAG", "Listen failed.", error);
                                return;
                            }

                            for(QueryDocumentSnapshot doc : value)
                            {
                                Calorie_Bruciate.add((Long) doc.get("Calorie"));
                                Feedback.add((Long) doc.get("Feedback"));
                                Passi.add((Long) doc.get("Passi"));
                                Tempo.add((Long) doc.get("Tempo"));
                                Km_Percorsi.add((Double) doc.get("Km"));
                                Velocita_Media.add((Double) doc.get("Velocita"));
                                Data.add((HashMap) doc.get("Data"));
                                IDUtente.add((String) doc.get("UserID"));
                            }
                        }
                    });
        }
        else
        {
            Log.d("utenteid", "niente vuoto");
        }
    }

    public ArrayList<Double> StatisticheTotali()
    {
        ArrayList<Double> Statistiche_Totali = new ArrayList<>();
        getDatiSessioneDatabase();

        double km_totali      = 0.0;
        double passi_totali   = 0.0;
        double calorie_totali = 0.0;
        double tempo_totale   = 0.0;

        for(int i = 0; i < Km_Percorsi.size(); i++)
        {
            km_totali      += Km_Percorsi.get(i);
            passi_totali   += Passi.get(i);
            calorie_totali += Calorie_Bruciate.get(i);
            tempo_totale   += Tempo.get(i);
        }

        Statistiche_Totali.add(km_totali);
        Statistiche_Totali.add(passi_totali);
        Statistiche_Totali.add(calorie_totali);
        Statistiche_Totali.add(tempo_totale);

        Log.d("Statistiche", " " + Statistiche_Totali);

        return Statistiche_Totali;
    }

}
