package com.example.jammbell;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.jammbell.Controller.Profile;
import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Utente;
import com.example.jammbell.View.IProfileView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class EditDialogClass extends AppCompatDialogFragment
{
    Utente utente = new Utente();

    Profile profilo = new Profile();

    private EditText NomeEditText;
    private EditText CognomeEditText;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    private NumberPicker pesoNumberPicker;
    private NumberPicker altezzaNumberPicker;
    private NumberPicker sessoNumberPicker;

    String date;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    String[] sesso = {"Maschio", "Femmina", "Altro"};

    String documentID;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_editdialog, null);

        NomeEditText = view.findViewById(R.id.NomeEditText);
        CognomeEditText = view.findViewById(R.id.CognomeEditText);

        //settaggio date picker
        initDatePicker();
        dateButton = view.findViewById(R.id.SelectDataButton);
        dateButton.setText(getTodayDate());

        //settaggio picker per il peso
        pesoNumberPicker = view.findViewById(R.id.PesoNumberPicker);
        pesoNumberPicker.setMaxValue(210);
        pesoNumberPicker.setMinValue(40);
        pesoNumberPicker.setValue(80);
        initPesoPicker();

        //settaggio picker per l'altezza
        altezzaNumberPicker = view.findViewById(R.id.altezzaNumberPicker);
        altezzaNumberPicker.setMaxValue(220);
        altezzaNumberPicker.setMinValue(120);
        altezzaNumberPicker.setValue(175);
        initAltezzaPicker();

        //settaggio picker per il sesso
        sessoNumberPicker = view.findViewById(R.id.SessoNumberPicker);
        String[] sesso = {"Maschio", "Femmina", "Altro"};
        sessoNumberPicker.setMaxValue(2);
        sessoNumberPicker.setMinValue(0);
        sessoNumberPicker.setDisplayedValues(sesso);

        SettaDati();

        dateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        builder.setView(view)
                .setTitle("Modifica dati")
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        pushDatiDB();

                        ProfileFragment profilo = new ProfileFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.FrammentoProfilo, profilo).commit();
                    }
                });

        return builder.create();
    }

    private void pushDatiDB()
    {
        String Nome = NomeEditText.getText().toString();
        String Cognome = CognomeEditText.getText().toString();
        int Altezza = altezzaNumberPicker.getValue();
        String Data = dateButton.getText().toString();
        String Sesso = sesso[sessoNumberPicker.getValue()];
        int Peso = pesoNumberPicker.getValue();

        profilo.ModificaProfilo(Nome, Cognome, Altezza, Data, Sesso, Peso, documentID);
    }

    private void SettaDati()
    {
        utente.getDatiUtenteDatabase(new FirestoreCallback()
        {
            @Override
            public void onCallback()
            {
                NomeEditText.setText(utente.getNome());
                CognomeEditText.setText(utente.getCognome());
                dateButton.setText(utente.getData_di_nascita());
                if(utente.getSesso().equals("Maschio")){
                    sessoNumberPicker.setValue(0);
                }
                if(utente.getSesso().equals("Femmina")){
                    sessoNumberPicker.setValue(1);
                }
                if(utente.getSesso().equals("Altro")){
                    sessoNumberPicker.setValue(2);
                }
                pesoNumberPicker.setValue(utente.getPeso());
                altezzaNumberPicker.setValue(utente.getAltezza());

                documentID = utente.getIDdocument();
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

        datePickerDialog = new DatePickerDialog(getContext(), style, dateSetListener, year, month, day);

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
}

