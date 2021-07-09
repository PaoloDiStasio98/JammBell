package com.example.jammbell.Controller;

import android.util.Log;

import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Gara;
import com.example.jammbell.Model.Utente;

import java.util.Map;

public class Challenge
{
    public Challenge(){}

    public void CercaUtente(String username)
    {
        Gara gara = new Gara();
        gara.searchUtente(username, new FirestoreCallback());
    }
}
