package com.appiedi.jammbell.Model;

public interface IGara
{
    void getGareUtenteDatabase(FirestoreCallback firestoreCallback1);
    void searchUtente(String username, FirestoreCallback firestoreCallback);
    void pullUsernameCreatore(FirestoreCallback firestoreCallback);
}
