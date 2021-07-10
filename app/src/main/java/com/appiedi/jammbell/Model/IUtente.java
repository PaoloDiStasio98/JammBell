package com.appiedi.jammbell.Model;

public interface IUtente
{
    void getDatiUtenteDatabase(FirestoreCallback firestoreCallback);
    void pushDatiUtenteDatabase(String Nome, String Cognome, int Altezza, String Data, String Sesso, int Peso, String documentID);
    void logIn(String Email, String Password, FirestoreCallback firestoreCallback);
    void signUp(String Email, String Password, String username, FirestoreCallback firestoreCallback);
    void logOut();
}
