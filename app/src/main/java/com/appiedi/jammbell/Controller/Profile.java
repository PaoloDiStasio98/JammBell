package com.appiedi.jammbell.Controller;

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Utente;

public class Profile implements IProfile
{

    public Profile() {
    }

    public void logIn(String mail, String password, FirestoreCallback firestoreCallback){
        Utente utente = new Utente();
        utente.logIn(mail, password, firestoreCallback);
    }

    public void signUp(String userMail, String userPassword, String userUsername, FirestoreCallback firestoreCallback){
        Utente utente = new Utente();
        utente.signUp(userMail, userPassword, userUsername, firestoreCallback);
    }

    public void ModificaProfilo(String Nome, String Cognome, int Altezza, String Data, String Sesso, int Peso, String documentID)
    {
        Utente utente = new Utente();
        utente.pushDatiUtenteDatabase(Nome, Cognome, Altezza, Data, Sesso, Peso, documentID);
    }
}
