package com.appiedi.jammbell;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

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
                intent.putExtra("Minuti_camminata", 3);
                intent.putExtra("Minuti_corsa", 4);
                intent.putExtra("Numero_ripetizioni", 6);
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
