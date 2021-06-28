package com.example.jammbell;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class ChallengeFragment extends Fragment {

    private FragmentActivity myContext;

    Main2Activity main2Activity = new Main2Activity();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    ArrayList<String> ChallengeDataInizio = new ArrayList<String>();
    ArrayList<String> ChallengeDataFine = new ArrayList<String>();
    ArrayList<String> ChallengeNome = new ArrayList<String>();
    ArrayList<String> ChallengeUsernamePartecipante = new ArrayList<String>();
    ArrayList<String> ChallengeUsernameCreatore = new ArrayList<String>();
    ArrayList<String> ChallengeStato = new ArrayList<String>();
    ArrayList<String> ChallengeDocumento = new ArrayList<String>();


    RecyclerView recyclerViewChallenge;


    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        recyclerViewChallenge = (RecyclerView) getView().findViewById(R.id.recyclerViewChallenge);

        setHasOptionsMenu(true);

        PullGare();

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.upbarchallenge_menu, menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.create_button: {
                    CreateGameDialog();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void CreateGameDialog() {
        CreateGameDialogClass createGameDialogClass = new CreateGameDialogClass();
        createGameDialogClass.show(myContext.getSupportFragmentManager(), "creategame");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("challenge", String.valueOf(main2Activity.Utente));
        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }


    public void PullGare() {


       // clearCell();

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();

            db.collection("Gara")
                    .whereEqualTo("UsernameCreatore", main2Activity.Utente.get("Username"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Log.d("Challenge", "Trovato creatore");

                                    ChallengeDataInizio.add("da " + String.valueOf(document.get("Datainizio")));
                                    ChallengeDataFine.add("al " + String.valueOf(document.get("Datafine")));
                                    ChallengeNome.add("Nome gara: " + String.valueOf(document.get("Nome")));
                                    ChallengeStato.add(String.valueOf(document.get("Stato")));
                                    ChallengeUsernamePartecipante.add(String.valueOf(document.get("UsernamePartecipante")));
                                    ChallengeUsernameCreatore.add(String.valueOf(document.get("UsernameCreatore")));
                                    ChallengeDocumento.add(String.valueOf(document.getId()));



                                }

                                MyAdapterChallenge myAdapter = new MyAdapterChallenge(getContext(), ChallengeDataInizio, ChallengeDataFine, ChallengeNome, ChallengeUsernamePartecipante, ChallengeStato, ChallengeUsernameCreatore, ChallengeDocumento);
                                recyclerViewChallenge.setAdapter(myAdapter);
                                recyclerViewChallenge.setLayoutManager(new LinearLayoutManager(getContext()));



                            }

                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });

        db.collection("Gara")
                .whereEqualTo("UsernamePartecipante", main2Activity.Utente.get("Username"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d("Challenge", "Trovato partecipante");

                                ChallengeDataInizio.add(String.valueOf(document.get("Datainizio")));
                                ChallengeDataFine.add(String.valueOf(document.get("Datafine")));
                                ChallengeNome.add(String.valueOf(document.get("Nome")));
                                ChallengeStato.add(String.valueOf(document.get("Stato")));
                                ChallengeUsernamePartecipante.add(String.valueOf(document.get("UsernamePartecipante")));
                                ChallengeUsernameCreatore.add(String.valueOf(document.get("UsernameCreatore")));
                                ChallengeDocumento.add(String.valueOf(document.getId()));


                            }

                            MyAdapterChallenge myAdapter = new MyAdapterChallenge(getContext(), ChallengeDataInizio, ChallengeDataFine, ChallengeNome, ChallengeUsernamePartecipante, ChallengeStato, ChallengeUsernameCreatore, ChallengeDocumento);
                            recyclerViewChallenge.setAdapter(myAdapter);
                            recyclerViewChallenge.setLayoutManager(new LinearLayoutManager(getContext()));

                        }

                        else
                        {
                            Log.d("database", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}