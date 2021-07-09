package com.example.jammbell.Controller;

import android.util.Log;

import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Utente;
import com.example.jammbell.View.IProfileView;

public class Profile implements IProfile
{

    public Profile() {
    }

    public void ModificaProfilo(String Nome, String Cognome, int Altezza, String Data, String Sesso, int Peso, String documentID)
    {
        Utente utente = new Utente();
        utente.pushDatiUtenteDatabase(Nome, Cognome, Altezza, Data, Sesso, Peso, documentID);
    }
}
