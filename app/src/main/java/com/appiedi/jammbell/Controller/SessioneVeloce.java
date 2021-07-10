package com.appiedi.jammbell.Controller;

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Sessione;

import java.util.Map;

public class SessioneVeloce
{
    public SessioneVeloce() {

    }

    public void OrdinaSessioniPerData(int day, int month, int year, FirestoreCallback firestoreCallback){
        Sessione sessione = new Sessione();
        sessione.OrdinaSessioniPerData(day, month, year, firestoreCallback);
    }

    public void pushSessione(Map<String, Object> SessioneVeloce, FirestoreCallback firestoreCallback)
    {
        Sessione sessione = new Sessione();
        sessione.pushSessione(SessioneVeloce, firestoreCallback);
    }
}
