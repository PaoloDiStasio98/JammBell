package com.appiedi.jammbell;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appiedi.jammbell.Controller.Profile;
import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Utente;
import com.appiedi.jammbell.View.IRegistrazioneProfilo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrazioneProfiloActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, IRegistrazioneProfilo
{

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    private Button sessoButton;
    private Button altezzaButton;
    private Button pesoButton;
    private Spinner sessoSpinner;

    private TextView pesoTextView;

    private TextView altezzaTextView;

    private Button confermaButton;

    private TextView nomeTextView;
    private TextView cognomeTextView;
    private TextView usernameTextView;
    private TextView erroreUsernameTextView;
    private TextView erroreNomeTextView;
    private TextView erroreCognomeTextView;
    private TextView erroreDataDiNascitaTextView;

    private String date;

    private int isValid = 0;

    private Boolean AltezzaCliccato = false;
    private Boolean PesoCliccato = false;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione_profilo);

        mAuth = FirebaseAuth.getInstance();


        String[] SessoArray = {"Maschio, Femmina, Altro"};

        //settaggio picker per il peso
        sessoSpinner = (Spinner) findViewById(R.id.SessoSpinner);
        pesoTextView = findViewById(R.id.PesoTextView);

        altezzaButton = findViewById(R.id.ButtonSelectAltezza);
        pesoButton = findViewById(R.id.ButtonSelectPeso);

        //settaggio date picker
        initDatePicker();
        dateButton = findViewById(R.id.ButtonSelectData);
        dateButton.setText(getTodayDate());



        //settaggio picker per l'altezza
        altezzaTextView = findViewById(R.id.altezzaTextView);


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

        String userMail = getIntent().getStringExtra("USER_MAIL");
        String userPassword = getIntent().getStringExtra("USER_PASSWORD");

        Log.d("userDati", userMail);
        Log.d("userDati", userPassword);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(RegistrazioneProfiloActivity.this,
                R.layout.spinner_style, getResources().getStringArray(R.array.sesso_array));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessoSpinner.setAdapter(myAdapter);


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
                    Utente utente = new Utente();
                    String userUsername = usernameTextView.getText().toString();
                    utente.signUp(userMail, userPassword, userUsername, new FirestoreCallback(){
                        @Override
                        public void onSignupCallback(Boolean usernameEsistente) {

                            if(usernameEsistente == true){

                                Toast.makeText(RegistrazioneProfiloActivity.this, "Username già esistente", Toast.LENGTH_LONG).show();
                                erroreUsernameTextView.setVisibility(View.VISIBLE);
                                erroreUsernameTextView.setText("Username già esistente");
                            }
                            else {

                                user = FirebaseAuth.getInstance().getCurrentUser();

                                utente.setIDUtente(user.getUid());
                                utente.setUsername(usernameTextView.getText().toString().replace(" ", ""));
                                utente.setNome(nomeTextView.getText().toString().trim());
                                utente.setCognome(cognomeTextView.getText().toString().trim());
                                utente.setData_di_nascita(date);
                                utente.setSesso(sessoSpinner.getSelectedItem().toString());
                                utente.setPeso(Integer.parseInt(pesoButton.getText().toString()));
                                utente.setAltezza(Integer.parseInt(altezzaButton.getText().toString()));

                            }
                        }

                        @Override
                        public void emailSentCallback(){
                            startActivity(new Intent(RegistrazioneProfiloActivity.this, LoginActivity.class));
                        }
                    });
                }
            }

        });
    }

    public void showNumberPickerAltezza(View view) {
        AltezzaCliccato = true;
        PesoCliccato = false;
        NumberPickerDialog newFragment = new NumberPickerDialog(120, 220, 175);
        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "time picker");
    }

    public void showNumberPickerPeso(View view) {
        AltezzaCliccato = false;
        PesoCliccato = true;
        NumberPickerDialog newFragment = new NumberPickerDialog(40, 210, 70);
        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "time picker");
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(AltezzaCliccato == true){
            altezzaButton.setText(String.valueOf(newVal));
        }
        else {
            pesoButton.setText(String.valueOf(newVal));
        }

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

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -15);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

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



}
