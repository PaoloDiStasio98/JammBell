package com.example.jammbell.Controller;

import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Sessione;

import java.util.Map;

public class SessioneVeloce
{
    public SessioneVeloce() {

    }

    public void pushSessione(Map<String, Object> SessioneVeloce, FirestoreCallback firestoreCallback)
    {
        Sessione sessione = new Sessione();
        sessione.pushSessione(SessioneVeloce, firestoreCallback);
    }
}
