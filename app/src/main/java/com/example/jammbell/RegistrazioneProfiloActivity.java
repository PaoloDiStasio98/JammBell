package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrazioneProfiloActivity extends AppCompatActivity
{

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    private DatePickerDialog sessoPickerDialog;
    private Button sessoButton;

    private TextView pesoTextView;
    private NumberPicker pesoNumberPicker;

    private TextView altezzaTextView;
    private NumberPicker altezzaNumberPicker;

    private NumberPicker sessoNumberPicker;

    private Button confermaButton;

    private TextView nomeTextView;
    private TextView cognomeTextView;
    private TextView usernameTextView;
    private TextView erroreUsernameTextView;
    private TextView erroreNomeTextView;
    private TextView erroreCognomeTextView;
    private TextView erroreDataDiNascitaTextView;

    String date;

    int isValid = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onDestroy() {
        Log.d("user", "on destroy");
        super.onDestroy();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("onDestroy", "User account deleted.");
                        }
                    }
                });

        Log.d("userMailDestroy", "dopo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione_profilo);

        //settaggio

        //settaggio date picker
        initDatePicker();
        dateButton = findViewById(R.id.ButtonSelectData);
        dateButton.setText(getTodayDate());

        //settaggio picker per il peso
        pesoTextView = findViewById(R.id.PesoTextView);
        pesoNumberPicker = findViewById(R.id.PesoNumberPicker);
        pesoNumberPicker.setMaxValue(210);
        pesoNumberPicker.setMinValue(40);
        pesoNumberPicker.setValue(80);
        initPesoPicker();

        //settaggio picker per l'altezza
        altezzaTextView = findViewById(R.id.altezzaTextView);
        altezzaNumberPicker = findViewById(R.id.altezzaNumberPicker);
        altezzaNumberPicker.setMaxValue(220);
        altezzaNumberPicker.setMinValue(120);
        altezzaNumberPicker.setValue(175);
        initAltezzaPicker();

        //settaggio picker per il sesso
        sessoNumberPicker = findViewById(R.id.SessoNumberPicker);
        String[] sesso = {"Maschio", "Femmina", "Altro"};
        sessoNumberPicker.setMaxValue(2);
        sessoNumberPicker.setMinValue(0);
        sessoNumberPicker.setDisplayedValues(sesso);

        //set button conferma
        confermaButton = findViewById(R.id.buttonConferma);

        Map<String, Object> utente = new HashMap<>();

        //set informazioni
        usernameTextView            = findViewById(R.id.UsernameEditText);
        nomeTextView                = findViewById(R.id.NomeEditText);
        cognomeTextView             = findViewById(R.id.CognomeEditText);

        erroreUsernameTextView      = findViewById(R.id.ErroreUsername);
        erroreNomeTextView          = findViewById(R.id.ErroreNome);
        erroreCognomeTextView       = findViewById(R.id.ErroreCognome);
        erroreDataDiNascitaTextView = findViewById(R.id.ErroreDataDiNascita);

        String userId = getIntent().getStringExtra("USER_ID");

        confermaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                erroreUsernameTextView.setVisibility(View.INVISIBLE);
                erroreNomeTextView.setVisibility(View.INVISIBLE);
                erroreCognomeTextView.setVisibility(View.INVISIBLE);
                erroreDataDiNascitaTextView.setVisibility(View.INVISIBLE);
                
                //controllo se ci sono dati non inseriti
                if (usernameTextView.getText().toString().matches("")  || nomeTextView.getText().toString().matches("")  || cognomeTextView.getText().toString().matches("")  || date == null)
                {
                    Toast.makeText(RegistrazioneProfiloActivity.this, "Inserisci tutti i campi", Toast.LENGTH_LONG).show();
                    if(usernameTextView.getText().toString().matches(""))
                    {
                        erroreUsernameTextView.setVisibility(View.VISIBLE);
                    }
                    if(nomeTextView.getText().toString().matches(""))
                    {
                        erroreNomeTextView.setVisibility(View.VISIBLE);
                    }
                    if(cognomeTextView.getText().toString().matches(""))
                    {
                        erroreCognomeTextView.setVisibility(View.VISIBLE);
                    }
                    if(date == null)
                    {
                        erroreDataDiNascitaTextView.setVisibility(View.VISIBLE);
                    }
                }
                else if(!usernameTextView.getText().toString().matches("") && !nomeTextView.getText().toString().matches("") && !cognomeTextView.getText().toString().matches("")  || date != null)
                {
                    db.collection("Utente").whereEqualTo("Username", usernameTextView.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if(task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                    isValid = 1;
                                }
                            }
                            else
                            {
                                Log.d("TAG", "Errore documento non trovato: ", task.getException());
                            }

                            if(isValid == 0)
                            {
                                //Prendo le informazioni e le metto in utente
                                utente.put("IDUtente", userId);
                                utente.put("Username", usernameTextView.getText().toString());
                                utente.put("Nome", nomeTextView.getText().toString());
                                utente.put("Cognome", cognomeTextView.getText().toString());
                                utente.put("Data di nascita", date);
                                utente.put("Sesso", sesso[sessoNumberPicker.getValue()]);
                                utente.put("Peso", pesoNumberPicker.getValue());
                                utente.put("Altezza", altezzaNumberPicker.getValue());

                                Log.d("utente", String.valueOf(utente));

                                //pusho utente nel database
                                db.collection("Utente").add(utente).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference)
                                    {
                                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                                        startActivity(new Intent(RegistrazioneProfiloActivity.this, Main2Activity.class));
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
                            else
                            {
                                Toast.makeText(RegistrazioneProfiloActivity.this, "Username già esistente", Toast.LENGTH_LONG).show();
                                erroreUsernameTextView.setVisibility(View.VISIBLE);
                                erroreUsernameTextView.setText("Username già esistente");
                            }
                            isValid = 0;
                        }
                    });
                }
            }

        });
    }

    private void initAltezzaPicker()
    {
        altezzaNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {

            }
        });
    }

    private void initPesoPicker()
    {
        pesoNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {

            }
        });
    }

    private String getTodayDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                month = month + 1;
                date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year)
    {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1){
            return "GEN";
        }
        if(month == 2){
            return "FEB";
        }
        if(month == 3){
            return "MAR";
        }
        if(month == 4){
            return "APR";
        }
        if(month == 5){
            return "MAG";
        }
        if(month == 6){
            return "GIU";
        }
        if(month == 7){
            return "LUG";
        }
        if(month == 8){
            return "AGO";
        }
        if(month == 9){
            return "SET";
        }
        if(month == 10){
            return "OTT";
        }
        if(month == 11){
            return "NOV";
        }
        if(month == 12){
            return "DIC";
        }

        //default
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    public void openSessoPicker(View view) {
    }
}