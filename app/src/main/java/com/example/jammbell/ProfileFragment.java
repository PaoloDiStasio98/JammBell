package com.example.jammbell;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.FrameLayout;
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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class ProfileFragment extends Fragment {

    private FragmentActivity myContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    TextView ciaoNomeeCognomeTextView;
    ImageView ImageProfilo;
    TextView usernameTextView;
    TextView datadinascitaTextView;
    TextView pesoTextView;
    TextView altezzaTextView;


    double KmTot = 0;
    long PassiTot = 0;
    long CalorieTot = 0;
    long TempoTot = 0;


    String TitoliStatistiche[] = {"Km percorsi", "Passi", "Calorie", "Ore totali"};
    String DescrizioneStatistiche[] = {"0", "0", "0", "0"};

    RecyclerView recyclerViewStatistiche;






    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        Button logoutButton = (Button) getView().findViewById(R.id.logoutButton);
        ciaoNomeeCognomeTextView = (TextView) getView().findViewById(R.id.CiaoNomeCognomeText);
        usernameTextView = (TextView) getView().findViewById(R.id.UsernameTextView);
        datadinascitaTextView = (TextView) getView().findViewById(R.id.DataTextView);
        pesoTextView = (TextView) getView().findViewById(R.id.PesoProfiloTextView);
        altezzaTextView = (TextView) getView().findViewById(R.id.AltezzaProfiloTextView);
        ImageProfilo = (ImageView) getView().findViewById(R.id.Imageprofilo);


        setHasOptionsMenu(true);

        recyclerViewStatistiche = (RecyclerView) getView().findViewById(R.id.recyclerViewStatistiche);

        PullDatiDatabase();

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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {
        inflater.inflate(R.menu.upbarprofile_menu, menu);

    }

    /*
    @Override
    public void applicaText(String Nome, String Cognome, String Datadinascita, int Peso, int Altezza, String sesso) {
        ciaoNomeeCognomeTextView.setText("Ciao " + Nome + " " + Cognome);
        datadinascitaTextView.setText(Datadinascita);
        pesoTextView.setText(Peso);
        altezzaTextView.setText(Altezza);

        if(sesso.equals("Maschio")){
            int blu = Color.parseColor("#1e90ff");
            ImageProfilo.setColorFilter(blu);
        }
        if(sesso.equals("Femmina")){
            int rosa = Color.parseColor("#ef35fc");
            ImageProfilo.setColorFilter(rosa);
        }
    }
    */


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_button: {

               openEditDialog();
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

    public void openEditDialog() {
        EditDialogClass editDialogClass = new EditDialogClass();
        editDialogClass.show(myContext.getSupportFragmentManager(), "esempio" );
    }

    public void PullDatiDatabase() {



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

                                   KmTot = KmTot + (double) document.get("Km");
                                   PassiTot = PassiTot + (long) document.get("Passi");
                                   CalorieTot = CalorieTot + (long) document.get("Calorie");
                                   TempoTot = TempoTot + (long) document.get("Tempo");


                               }

                               DecimalFormat df = new DecimalFormat("##########.###");
                               df.setRoundingMode(RoundingMode.DOWN);
                               DescrizioneStatistiche[0] = String.valueOf(df.format(KmTot) + " Km");
                               DescrizioneStatistiche[1] = String.valueOf(PassiTot);
                               DescrizioneStatistiche[2] = String.valueOf(CalorieTot + " Kcal");
                               DescrizioneStatistiche[3] = String.valueOf(TempoTot/3600 + " h");

                               MyAdapter myAdapter = new MyAdapter(getContext(), TitoliStatistiche, DescrizioneStatistiche);
                               recyclerViewStatistiche.setAdapter(myAdapter);
                               recyclerViewStatistiche.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                           }

                           else
                               {
                               Log.d("database", "Error getting documents: ", task.getException());
                           }






                       }
                   });

           Log.d("descrizione", DescrizioneStatistiche[1]);


       }
       else {
           Log.d("utenteid", "niente vuoto");
       }
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