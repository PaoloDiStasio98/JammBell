package com.example.jammbell.Model;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.jammbell.CreateGameDialogClass;
import com.example.jammbell.LoginActivity;
import com.example.jammbell.Main2Activity;
import com.example.jammbell.RegistrazioneProfiloActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utente implements IUtente
{
    private String   Email, Password, IDUtente, Username, Nome, Cognome, Sesso, Data_di_nascita, IDdocument;
    private int      Peso, Altezza;

    public Utente(String Email, String Password, String IDUtente, String Username, String Nome, String Cognome, String Sesso, String Data_di_nascita, int Peso, int Altezza)
    {
        this.Email           = Email;
        this.Password        = Password;
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
        this.Email           = " ";
        this.Password        = " ";
        this.IDUtente        = " ";
        this.Username        = " ";
        this.Nome            = " ";
        this.Cognome         = " ";
        this.Sesso           = " ";
        this.Data_di_nascita = " ";
        this.Peso            = 0;
        this.Altezza         = 0;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Utente")
                .document(documentID)
                .update(
                        "Nome", Nome,
                        "Cognome", Cognome,
                        "Altezza", Altezza,
                        "Data di nascita", Data,
                        "Sesso", Sesso,
                        "Peso", Peso
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

    public void logIn(String Email, String Password, FirestoreCallback firestoreCallback){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Boolean successo = false;

                        if (task.isSuccessful()) {

                            successo = true;

                        } else {

                            successo = false;

                        }

                        firestoreCallback.onLoginCallback(successo);
                    }
                });

    }

    public void signUp(String Email, String Password, String username, FirestoreCallback firestoreCallback){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            db.collection("Utente").whereEqualTo("Username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    Boolean usernameEsistente = false;
                                    if(task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot document : task.getResult())
                                        {
                                            usernameEsistente = true;
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                            }
                                                        }
                                                    });
                                        }
                                        firestoreCallback.onSignupCallback(usernameEsistente);
                                    }
                                    else
                                    {
                                        Log.d("ERROREUSERNAME", "Errore documento non trovato: ", task.getException());
                                    }

                                    if(usernameEsistente == false)
                                    {
                                        Map<String, Object> utente = new HashMap<>();

                                        utente.put("IDUtente", getIDUtente());
                                        utente.put("Username", getUsername());
                                        utente.put("Nome", getNome());
                                        utente.put("Cognome", getCognome());
                                        utente.put("Data di nascita", getData_di_nascita());
                                        utente.put("Sesso", getSesso());
                                        utente.put("Peso", getPeso());
                                        utente.put("Altezza", getAltezza());
                                        Log.d("PROVA2", getUsername());

                                        //pusho utente nel database
                                        db.collection("Utente").add(utente).addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                        {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference)
                                            {
                                                Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());

                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d("TAG", "Email sent.");
                                                                    firestoreCallback.emailSentCallback();
                                                                }
                                                                else {
                                                                    Log.d("TAG", String.valueOf(task.getException()));
                                                                }
                                                            }
                                                        });



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

                                //    isValid = 0;
                                }
                            });

                        }
                        else{
                            Log.d("ERROREMAILPASSWORD", String.valueOf(task.getException()));
                        }
                    }
                });

    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
