package com.example.jammbell.Model;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.jammbell.CreateGameDialogClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Utente implements IUtente
{
    private String   IDUtente, Username, Nome, Cognome, Sesso, Data_di_nascita, IDdocument;
    private int      Peso, Altezza;

    public Utente(String IDUtente, String Username, String Nome, String Cognome, String Sesso, String Data_di_nascita, int Peso, int Altezza)
    {
        this.IDUtente        = IDUtente;
        this.Username        = Username;
        this.Nome            = Nome;
        this.Cognome         = Cognome;
        this.Sesso           = Sesso;
        this.Data_di_nascita = Data_di_nascita;
        this.Peso            = Peso;
        this.Altezza         = Altezza;
    }

    public Utente()
    {
        this.IDUtente        = " ";
        this.Username        = " ";
        this.Nome            = " ";
        this.Cognome         = " ";
        this.Sesso           = " ";
        this.Data_di_nascita = " ";
        this.Peso            = 0;
        this.Altezza         = 0;
    }

    public String getIDdocument() {
        return IDdocument;
    }

    public void setIDdocument(String IDdocument) {
        this.IDdocument = IDdocument;
    }

    public String getIDUtente() {
        return IDUtente;
    }

    public void setIDUtente(String IDUtente) {
        this.IDUtente = IDUtente;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getCognome() {
        return Cognome;
    }

    public void setCognome(String cognome) {
        Cognome = cognome;
    }

    public String getSesso() {
        return Sesso;
    }

    public void setSesso(String sesso) {
        Sesso = sesso;
    }

    public String getData_di_nascita() {
        return Data_di_nascita;
    }

    public void setData_di_nascita(String data_di_nascita) {
        Data_di_nascita = data_di_nascita;
    }

    public int getPeso() {
        return Peso;
    }

    public void setPeso(int peso) {
        Peso = peso;
    }

    public int getAltezza() {
        return Altezza;
    }

    public void setAltezza(int altezza) {
        Altezza = altezza;
    }

    public interface FirestoreCallback
    {
        void onCallback();
    }

    public void getDatiUtenteDatabase(FirestoreCallback firestoreCallback)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            db.collection("Utente")
                    .whereEqualTo("IDUtente", currentUser.getUid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
                        {
                            if (error != null)
                            {
                                Log.w("TAG", "Listen failed.", error);
                                return;
                            }

                            for(QueryDocumentSnapshot doc : value)
                            {
                                setIDUtente(String.valueOf(doc.get("IDUtente")));
                                setUsername(String.valueOf(doc.get("Username")));
                                setNome(String.valueOf(doc.get("Nome")));
                                setCognome(String.valueOf(doc.get("Cognome")));
                                setSesso(String.valueOf(doc.get("Sesso")));
                                setData_di_nascita(String.valueOf(doc.get("Data di nascita")));
                                setPeso(((Long) doc.get("Peso")).intValue());
                                setAltezza(((Long) doc.get("Altezza")).intValue());
                                setIDdocument(doc.getId());
                            }
                            firestoreCallback.onCallback();
                        }
                    });
        }
        else
        {
            Log.d("utenteid", "niente vuoto");
        }
    }

    public void pushDatiUtenteDatabase(String Nome, String Cognome, int Altezza, String Data, String Sesso, int Peso, String documentID)
    {
        db.collection("Utente")
                .document(documentID)
                .update(
                        "Nome", NomeEditText.getText().toString(),
                        "Cognome", CognomeEditText.getText().toString(),
                        "Altezza", altezzaNumberPicker.getValue(),
                        "Data di nascita", dateButton.getText().toString(),
                        "Sesso", sesso[sessoNumberPicker.getValue()],
                        "Peso", pesoNumberPicker.getValue()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

}
