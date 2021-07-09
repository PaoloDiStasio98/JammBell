package com.example.jammbell.Model;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.jammbell.Main2Activity;
import com.example.jammbell.MyAdapterStorico;
import com.example.jammbell.RiepilogoSessioneVeloceActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<String>                 documentID       = new ArrayList<>();
    private ArrayList<Long>                   Valutazione      = new ArrayList<>();

    public ArrayList<Long> getValutazione() {
        return Valutazione;
    }

    public ArrayList<String> getDocumentID() {
        return documentID;
    }

    public void setDocumentID(ArrayList<String> documentID) {
        this.documentID = documentID;
    }

    public void setValutazione(ArrayList<Long> valutazione) {
        Valutazione = valutazione;
    }

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
        Log.d("prova", String.valueOf(getTempo()));


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

        Log.d("tempo", String.valueOf(tempo_totale));

        Statistiche_Totali.add(km_totali);
        Statistiche_Totali.add(passi_totali);
        Statistiche_Totali.add(calorie_totali);
        Statistiche_Totali.add(tempo_totale);

        Log.d("Statistiche", " " + Statistiche_Totali);

        return Statistiche_Totali;
    }

    public void EliminaSessione(String documentID)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("SessioneVeloce").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Eliminato", "DocumentSnapshot successfully deleted!");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    public void OrdinaTutteSessioni(FirestoreCallback firestoreCallback)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(currentUser != null)
        {
            db.collection("SessioneVeloce")
                    .whereEqualTo("UserID", currentUser.getUid())
                    .orderBy("Data.year", Query.Direction.DESCENDING)
                    .orderBy("Data.monthValue", Query.Direction.DESCENDING)
                    .orderBy("Data.dayOfMonth", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Double Km                      = (double) document.get("Km");
                                    Long Passi_Fatti               = (long)   document.get("Passi");
                                    Long Calorie                   = (long)   document.get("Calorie");
                                    Long Tempo_Sessione            = (long)   document.get("Tempo");
                                    Long Valutazione_Sessione      = (long)   document.get("Feedback");
                                    Double Velocita                = (double) document.get("Velocita");
                                    HashMap<String,String> Datamap = (HashMap<String, String>) document.get("Data");
                                    String DocumentID              = document.getId();

                                    Km_Percorsi.add(Km);
                                    Passi.add(Passi_Fatti);
                                    Calorie_Bruciate.add(Calorie);
                                    Tempo.add(Tempo_Sessione);
                                    Valutazione.add(Valutazione_Sessione);
                                    Velocita_Media.add(Velocita);
                                    Data.add(Datamap);
                                    documentID.add(DocumentID);
                                }
                                firestoreCallback.onPullSessioneCallback();
                            }
                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {
            Log.d("utenteid", "niente vuoto");
        }
    }

    public void OrdinaSessioniPerData(int day, int month, int year, FirestoreCallback firestoreCallback)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            Log.d("utenteid", currentUser.getUid());
            db.collection("SessioneVeloce")
                    .whereEqualTo("UserID", currentUser.getUid())
                    .whereEqualTo("Data.dayOfMonth", day)
                    .whereEqualTo("Data.monthValue", month)
                    .whereEqualTo("Data.year", year)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Double Km                      = (double) document.get("Km");
                                    Long Passi_Fatti               = (long)   document.get("Passi");
                                    Long Calorie                   = (long)   document.get("Calorie");
                                    Long Tempo_Sessione            = (long)   document.get("Tempo");
                                    Long Valutazione_Sessione      = (long)   document.get("Feedback");
                                    Double Velocita                = (double) document.get("Velocita");
                                    HashMap<String,String> Datamap = (HashMap<String, String>) document.get("Data");
                                    String DocumentID              = document.getId();

                                    Km_Percorsi.add(Km);
                                    Passi.add(Passi_Fatti);
                                    Calorie_Bruciate.add(Calorie);
                                    Tempo.add(Tempo_Sessione);
                                    Valutazione.add(Valutazione_Sessione);
                                    Velocita_Media.add(Velocita);
                                    Data.add(Datamap);
                                    documentID.add(DocumentID);
                                }
                                firestoreCallback.onPullSessioneCallback();
                            }
                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {
            Log.d("utenteid", "niente vuoto");
        }
    }

    public void pushSessione(Map<String, Object> SessioneVeloce, FirestoreCallback firestoreCallback)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("SessioneVeloce").add(SessioneVeloce).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                firestoreCallback.onCallback();
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

}
