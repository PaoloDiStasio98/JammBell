package com.appiedi.jammbell.Controller;

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Gara;

public class Challenge
{
    public Challenge(){}

    public void CercaUtente(String username)
    {
        Gara gara = new Gara();
        gara.searchUtente(username, new FirestoreCallback());
    }
}
