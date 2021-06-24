package com.example.jammbell;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ProfileFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    TextView ciaoNomeeCognomeTextView;
    ImageView ImageProfilo;
    TextView usernameTextView;
    TextView datadinascitaTextView;
    TextView pesoTextView;
    TextView altezzaTextView;






    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        Button logoutButton = (Button) getView().findViewById(R.id.logoutButton);
        ciaoNomeeCognomeTextView = (TextView) getView().findViewById(R.id.CiaoNomeCognomeText);
        usernameTextView = (TextView) getView().findViewById(R.id.UsernameTextView);
        datadinascitaTextView = (TextView) getView().findViewById(R.id.DataTextView);
        pesoTextView = (TextView) getView().findViewById(R.id.PesoProfiloTextView);
        altezzaTextView = (TextView) getView().findViewById(R.id.AltezzaProfiloTextView);
        ImageProfilo = (ImageView) getView().findViewById(R.id.Imageprofilo);

        super.onViewCreated(view, savedInstanceState);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("utenteid", currentUser.getUid());

            db.collection("Utente")
                    .whereEqualTo("IDUtente", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    usernameTextView.setText("Username: " + document.get("Username").toString());
                                    ciaoNomeeCognomeTextView.setText("Ciao, " + document.get("Nome").toString() + " " + document.get("Cognome").toString());
                                    datadinascitaTextView.setText("Data di nascita: " + document.get("Data di nascita").toString());
                                    pesoTextView.setText("Peso: " + document.get("Peso").toString());
                                    altezzaTextView.setText("Altezza: " + document.get("Altezza").toString());

                                    if(document.get("Sesso").toString().equals("Maschio")){
                                        int blu = Color.parseColor("#1e90ff");
                                        ImageProfilo.setColorFilter(blu);
                                    }
                                    if(document.get("Sesso").toString().equals("Femmina")){
                                        int rosa = Color.parseColor("#ef35fc");
                                        ImageProfilo.setColorFilter(rosa);
                                    }


                                    Log.d("database", document.getId() + " => " + document.getData() + " "  + " " + document.get("Altezza"));
                                }
                            } else {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            Log.d("utenteid", "niente vuoto");
        }







        return inflater.inflate(R.layout.fragment_profile, container, false);



    }


}