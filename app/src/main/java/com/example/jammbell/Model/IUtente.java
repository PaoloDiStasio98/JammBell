package com.example.jammbell.Model;

import java.util.ArrayList;

public interface IUtente
{
    void getDatiUtenteDatabase(Utente.FirestoreCallback firestoreCallback);
    void pushDatiUtenteDatabase();
}
