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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class SessioneGuidataDialog extends AppCompatDialogFragment {

    Button buttonFacile, buttonMedio, buttonDifficile;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_sessioneguidata_dialog, null);
        buttonFacile = view.findViewById(R.id.buttonFacile);
        buttonMedio = view.findViewById(R.id.buttonMedio);
        buttonDifficile = view.findViewById(R.id.buttonDifficile);

        buttonFacile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SessioneGuidataActivity.class);
                intent.putExtra("Minuti_camminata", 1); //3
                intent.putExtra("Minuti_corsa", 1); //4
                intent.putExtra("Numero_ripetizioni", 1); //6
                startActivity(intent);
            }
        });

        buttonMedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SessioneGuidataActivity.class);
                intent.putExtra("Minuti_camminata", 3);
                intent.putExtra("Minuti_corsa", 6);
                intent.putExtra("Numero_ripetizioni", 5);
                startActivity(intent);
            }
        });

        buttonDifficile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SessioneGuidataActivity.class);
                intent.putExtra("Minuti_camminata", 5);
                intent.putExtra("Minuti_corsa", 25);
                intent.putExtra("Numero_ripetizioni", 2);
                startActivity(intent);
            }
        });

        builder.setView(view)
                .setTitle("Seleziona livello")
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();

    }
}
