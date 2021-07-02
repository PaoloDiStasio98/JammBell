package com.example.jammbell;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateGameDialogClass extends AppCompatDialogFragment {



    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    DatePickerDialog dialogInizio;
    private DatePickerDialog.OnDateSetListener dateSetListenerInizio;

    DatePickerDialog dialogFine;
    private DatePickerDialog.OnDateSetListener dateSetListenerFine;


    private TextView DatainizioTextView;
    private TextView DatafineTextView;
    private EditText cercaAmicoEditText;
    private EditText nomePartitaEditText;
    private TextView ErroreTextView;

    String usernameamico;
    String datainizio;
    String datafine;
    String nomepartita;
    String usernamecreatore;
    String IDamico;
    boolean amicotrovato;

    Map<String, Object> gara = new HashMap<>();

    ChallengeFragment fragment = new ChallengeFragment();

    public interface OnGameCreatedListener{
        public void getUsername(String Datafine, String Datainizio, String IDcreatore, String Nome, String Stato, String UsernameCreatore, String UsernamePartecipante);
    }

    OnGameCreatedListener mOnGameCreatedListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_creategamedialog, null);

        DatainizioTextView = view.findViewById(R.id.DataInizioGaraTextView);
        DatafineTextView = view.findViewById(R.id.DatafineGaraTextView);
        cercaAmicoEditText = view.findViewById(R.id.cercaAmicoEditText);
        nomePartitaEditText = view.findViewById(R.id.NomePartitaEditText);
        ErroreTextView = view.findViewById(R.id.ErroreTextView);


        //prendo data di inizio e la imposto come testo del picker

        DatainizioTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                dialogInizio = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListenerInizio, year, month, day);
                dialogInizio.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialogInizio.show();


            }
        });


        dateSetListenerInizio = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                Log.d("provadata", "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                DatainizioTextView.setText(day + "/" + month + "/" + year);
                if(month < 10)
                    datainizio = year + "-0" + month + "-" + day;
                if(day < 10)
                    datainizio = year + "-" + month + "-0" + day;
                if(day < 10 && month <10)
                    datainizio = year + "-0" + month + "-0" + day;

                DatafineTextView.setText("Data fine gara");
                datafine = null;

            }
        };

        //prendo data di fine e la imposto come testo del picker



            DatafineTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(datainizio == null){
                        Toast.makeText(getContext(), "on Move", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    dialogFine = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListenerFine, year, month, day);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Long date = null;
                    try {
                        Log.d("amicodata", String.valueOf(datainizio));
                        date = df.parse(datainizio).getTime();
                        Log.d("amicodata", String.valueOf(date));
                        dialogFine.getDatePicker().setMinDate(date + 86400000);
                        dialogFine.show();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
                }
            });


        dateSetListenerFine = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                Log.d("provadata", "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                DatafineTextView.setText(day + "/" + month + "/" + year);
                if(month < 10)
                    datafine = year + "-0" + month + "-" + day;
                if(day < 10)
                    datafine = year + "-" + month + "-0" + day;
                if(day < 10 && month <10)
                    datafine = year + "-0" + month + "-0" + day;

            }
        };








        builder.setView(view)
                .setTitle("Crea Gara")
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(cercaAmicoEditText.getText().toString().matches("") || DatainizioTextView.getText().toString().matches("Data inizio gara")
                            || DatafineTextView.getText().toString().matches("Data fine gara") || nomePartitaEditText.getText().toString().matches(""))
                            Log.d("amico", "qualche campo vuoto");
                        else {
                            Log.d("amicodata", datainizio);
                            Log.d("amicodata", datafine);

                            if(datainizio.compareTo(datafine) < 0){
                            nomepartita = nomePartitaEditText.getText().toString();
                            usernameamico = cercaAmicoEditText.getText().toString();
                            cercautenteDB(usernameamico);

                            }
                            else {
                                Log.d("amico", "controlla data");
                                ErroreTextView.setVisibility(View.VISIBLE);
                                ErroreTextView.setText("Controlla le date inserite");
                            }
                        }
                    }
                });

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
        mOnGameCreatedListener = (OnGameCreatedListener) getTargetFragment();
        } catch (ClassCastException e){
            Log.d("amico", "problema nel try");
        }
        super.onAttach(context);
    }



    public void pullUsernameCreatore(){


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            db.collection("Utente")
                    .whereEqualTo("IDUtente", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    usernamecreatore = String.valueOf(document.get("Username"));
                                    gara.put("IDcreatore", currentUser.getUid());

                                }

                                DateTimeFormatter dtf = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                                    LocalDateTime now = LocalDateTime.now();
                                    Log.d("data", dtf.format(now));
                                    gara.put("Data", now);
                                }

                                gara.put("IDpartecipante", IDamico);
                                gara.put("Datafine", datafine);
                                gara.put("Datainizio", datainizio);
                                gara.put("Nome", nomepartita);
                                gara.put("UsernameCreatore", usernamecreatore);
                                gara.put("UsernamePartecipante", usernameamico);
                                gara.put("Stato", "In attesa");

                                pushGara();





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

    public void pushGara(){
        //pusho gara nel database
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("Gara").add(gara).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
        {
            @Override
            public void onSuccess(DocumentReference documentReference)
            {
                Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
              //  startActivity(new Intent(RegistrazioneProfiloActivity.this, Main2Activity.class));

                mOnGameCreatedListener.getUsername(datafine, datainizio, currentUser.getUid(), nomepartita, "In attesa", usernamecreatore, usernameamico);

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




    public void cercautenteDB(String username){

        mAuth = FirebaseAuth.getInstance();
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

                                    IDamico = String.valueOf(document.get("IDUtente"));
                                    amicotrovato = true;
                                    Log.d("amico", "amico trovato");

                                    pullUsernameCreatore();

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

        if(amicotrovato == false) {
            Log.d("amico", "amico non trovato");
            ErroreTextView.setVisibility(View.VISIBLE);
            ErroreTextView.setText("Nessun utente trovato con questo username, riprova");
        }


    }

}
