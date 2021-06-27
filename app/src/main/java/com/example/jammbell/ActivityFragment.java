package com.example.jammbell;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class ActivityFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Menu menu;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    ArrayList<String> StoricoSessioneData = new ArrayList<String>();
    ArrayList<String> StoricoKm = new ArrayList<String>();
    ArrayList<String> StoricoTempo = new ArrayList<String>();
    ArrayList<String> StoricoCalorie = new ArrayList<String>();
    ArrayList<String> StoricoPassi = new ArrayList<String>();
    ArrayList<String> StoricoVelocitaMedia = new ArrayList<String>();
    ArrayList<String> StoricoValutazione = new ArrayList<String>();
    ArrayList<String> ArrayDocumentoID = new ArrayList<String>();

    RecyclerView recyclerViewStorico;

    double Km;
    long Tempo;
    long Passi;
    long Calorie;
    long Valutazione;
    double Velocita;
    int dayprova;
    int monthprova;
    int yearprova;
    String DocumentID;
    boolean filtroapplicato = false;
    HashMap<String, String> Datamap = new HashMap<>();
    TextView filtroTextView;
    DatePickerDialog dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {
        recyclerViewStorico = (RecyclerView) getView().findViewById(R.id.recyclerViewStorico);
        filtroTextView = (TextView) getView().findViewById(R.id.FiltroTextView);

        setHasOptionsMenu(true);





        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //Toast.makeText(getContext(), "on Move", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                    int position = viewHolder.getAdapterPosition();

                    Log.d("Documento1", StoricoCalorie.get(position).split(" ")[1]);
                    Log.d("Documento1", StoricoPassi.get(position).split(" ")[1]);
                    Log.d("Documento1", StoricoCalorie.toString());
                    Log.d("Documento1", ArrayDocumentoID.get(position));
                    Log.d("Documento1", ArrayDocumentoID.toString());


                    EliminaDocumento(ArrayDocumentoID.get(position));


                    StoricoSessioneData.remove(position);
                    StoricoCalorie.remove(position);
                    StoricoKm.remove(position);
                    StoricoPassi.remove(position);
                    StoricoTempo.remove(position);
                    StoricoValutazione.remove(position);
                    StoricoVelocitaMedia.remove(position);
                    ArrayDocumentoID.remove(position);
                    //MyAdapterStorico.notifyDataSetChanged();

                    if(filtroapplicato == false)
                        PullDatiDatabaseStorico();
                    else
                        PullDatiDatabaseStoricoData(dayprova,monthprova,yearprova);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewStorico);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                Log.d("provadata", "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                dayprova = day;
                monthprova = month;
                yearprova = year;

                if(StoricoKm.size() != 0)
                filtroTextView.setText("Filtro, giorno: " + day + "/" + month + "/" + year);

                filtroapplicato = true;
                menu.getItem(1).setVisible(true);

                Log.d("Documento2", ArrayDocumentoID.toString());
                Log.d("Documento2", StoricoCalorie.toString());
                Log.d("Documento2", StoricoKm.toString());
                Log.d("Documento2", StoricoTempo.toString());

                if(filtroapplicato == true)
                {
                    PullDatiDatabaseStoricoData(day, month, year);
                }

            }
        };

       if(filtroapplicato == false)
             PullDatiDatabaseStorico();


    }

    public void EliminaDocumento(String documentID){

        db.collection("SessioneVeloce").document(documentID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.upbar_menu, menu);
            this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button:
                {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                 dialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);

                dialog.show();

                return true;
            }

            case R.id.close_button:
                {

                    menu.getItem(1).setVisible(false);
                    PullDatiDatabaseStorico();
                    filtroapplicato = false;
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void clearCell(){

        StoricoKm.clear();
        StoricoCalorie.clear();
        StoricoPassi.clear();
        StoricoSessioneData.clear();
        StoricoTempo.clear();
        StoricoVelocitaMedia.clear();
        StoricoValutazione.clear();
        ArrayDocumentoID.clear();

    }

    public void PullDatiDatabaseStoricoData(int day, int month, int year){



            clearCell();

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
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
                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    Km = (double) document.get("Km");
                                    Passi = (long) document.get("Passi");
                                    Calorie = (long) document.get("Calorie");
                                    Tempo =  (long) document.get("Tempo");
                                    Valutazione = (long) document.get("Feedback");
                                    Velocita = (double) document.get("Velocita");
                                    Datamap = (HashMap<String, String>) document.get("Data");
                                    DocumentID = document.getId();


                                    Log.d("mappa1", String.valueOf(Datamap));


                                    DecimalFormat df = new DecimalFormat("##########.###");
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    StoricoKm.add("Km: " + String.valueOf(df.format(Km)));
                                    StoricoCalorie.add("Calorie: " + String.valueOf(Calorie));
                                    StoricoPassi.add("Passi: " + String.valueOf(Passi));
                                    StoricoSessioneData.add("Sessione del " + String.valueOf(Datamap.get("dayOfMonth")) + "/" + String.valueOf(Datamap.get("monthValue")) + "/" +  String.valueOf(Datamap.get("year")));
                                    StoricoTempo.add("Durata: " + formatSecondDateTime((int) Tempo));
                                    DecimalFormat df1 = new DecimalFormat("###.##");
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    StoricoVelocitaMedia.add("Velocità media: " + String.valueOf(df1.format(Velocita)));
                                    StoricoValutazione.add(String.valueOf(Valutazione) + "/5");
                                    ArrayDocumentoID.add(DocumentID);



                                }

                                if(StoricoKm.size() == 0) {
                                    Toast.makeText(getContext(), "Nessuna sessione trovata", Toast.LENGTH_SHORT).show();
                                    filtroTextView.setText("Nessuna sessione");
                                    filtroTextView.setGravity(Gravity.CENTER);

                                }



                                MyAdapterStorico myAdapter = new MyAdapterStorico(getContext(), StoricoSessioneData, StoricoKm, StoricoTempo, StoricoCalorie, StoricoPassi, StoricoVelocitaMedia, StoricoValutazione, ArrayDocumentoID);
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

    public void PullDatiDatabaseStorico() {


        clearCell();

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("utenteid", currentUser.getUid());

                    db.collection("SessioneVeloce")
                     .whereEqualTo("UserID", currentUser.getUid())
                            .orderBy("Data", Query.Direction.DESCENDING)
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
                                    Datamap = (HashMap<String, String>) document.get("Data");
                                    DocumentID = document.getId();


                                    Log.d("mappa", String.valueOf(Datamap));


                                    DecimalFormat df = new DecimalFormat("##########.###");
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    StoricoKm.add("Km: " + String.valueOf(df.format(Km)));
                                    StoricoCalorie.add("Calorie: " + String.valueOf(Calorie));
                                    StoricoPassi.add("Passi: " + String.valueOf(Passi));
                                    StoricoSessioneData.add("Sessione del " + String.valueOf(Datamap.get("dayOfMonth")) + "/" + String.valueOf(Datamap.get("monthValue")) + "/" +  String.valueOf(Datamap.get("year")));
                                    StoricoTempo.add("Durata: " + formatSecondDateTime((int) Tempo));
                                    DecimalFormat df1 = new DecimalFormat("###.##");
                                    df.setRoundingMode(RoundingMode.DOWN);
                                    StoricoVelocitaMedia.add("Velocità media: " + String.valueOf(df1.format(Velocita)));
                                    StoricoValutazione.add(String.valueOf(Valutazione) + "/5");
                                    ArrayDocumentoID.add(DocumentID);


                                }

                                if(StoricoKm.size() == 0) {
                                    Toast.makeText(getContext(), "Nessuna sessione trovata", Toast.LENGTH_SHORT).show();
                                    filtroTextView.setText("Nessuna sessione");

                                }
                                else {
                                    filtroTextView.setText("Tutte le tue sessioni");

                                }

                                MyAdapterStorico myAdapter = new MyAdapterStorico(getContext(), StoricoSessioneData, StoricoKm, StoricoTempo, StoricoCalorie, StoricoPassi, StoricoVelocitaMedia, StoricoValutazione, ArrayDocumentoID);
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