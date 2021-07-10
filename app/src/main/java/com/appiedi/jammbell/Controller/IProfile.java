package com.appiedi.jammbell.Controller;

import com.appiedi.jammbell.Model.FirestoreCallback;

public interface IProfile
{
    void ModificaProfilo(String Nome, String Cognome, int Altezza, String Data, String Sesso, int Peso, String documentID);
    void signUp(String userMail, String userPassword, String userUsername, FirestoreCallback firestoreCallback);
    void logIn(String mail, String password, FirestoreCallback firestoreCallback);
}
