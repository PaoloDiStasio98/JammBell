package com.example.jammbell;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ActivityFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    ArrayList<String> StoricoSessioneData = new ArrayList<String>();
    ArrayList<String> StoricoKm = new ArrayList<String>();
    ArrayList<String> StoricoTempo = new ArrayList<String>();
    ArrayList<String> StoricoCalorie = new ArrayList<String>();
    ArrayList<String> StoricoPassi = new ArrayList<String>();
    ArrayList<String> StoricoVelocitaMedia = new ArrayList<String>();
    ArrayList<String> StoricoValutazione = new ArrayList<String>();



    RecyclerView recyclerViewStorico;

    double Km;
    long Tempo;
    long Passi;
    long Calorie;
    long Valutazione;
    double Velocita;
    Date Data;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        recyclerViewStorico = (RecyclerView) getView().findViewById(R.id.recyclerViewStorico);
        PullDatiDatabaseStorico();
        super.onViewCreated(view, savedInstanceState);
    }


    public void PullDatiDatabaseStorico() {



        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("utenteid", currentUser.getUid());

            db.collection("SessioneVeloce")
                    .whereEqualTo("UserID", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Km = (double) document.get("Km");
                                    Passi = (long) document.get("Passi");
                                    Calorie = (long) document.get("Calorie");
                                    Tempo =  (long) document.get("Tempo");
                                    Valutazione = (long) document.get("Feedback");
                                    Velocita = (double) document.get("Velocita");
                                    //Data = (Date) document.get("Data");

                                    DecimalFormat df = new DecimalFormat("##########.###");
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    StoricoKm.add("Km: " + String.valueOf(df.format(Km)));
                                    StoricoCalorie.add("Calorie: " + String.valueOf(Calorie));
                                    StoricoPassi.add("Passi: " + String.valueOf(Passi));
                                    StoricoSessioneData.add("Sessione del " + String.valueOf(Data));
                                    StoricoTempo.add("Durata: " + formatSecondDateTime((int) Tempo));
                                    DecimalFormat df1 = new DecimalFormat("###.##");
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    StoricoVelocitaMedia.add("Velocità media: " + String.valueOf(df1.format(Velocita)));
                                    StoricoValutazione.add(String.valueOf(Valutazione) + "/5");


                                }

                                MyAdapterStorico myAdapter = new MyAdapterStorico(getContext(), StoricoSessioneData, StoricoKm, StoricoTempo, StoricoCalorie, StoricoPassi, StoricoVelocitaMedia, StoricoValutazione);
                                recyclerViewStorico.setAdapter(myAdapter);
                                recyclerViewStorico.setLayoutManager(new LinearLayoutManager(getContext()));

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
    public ActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }
}