package com.example.jammbell.Model;

import java.util.ArrayList;

public interface ISessione
{
    void getDatiSessioneDatabase();
    ArrayList<Double> StatisticheTotali();
    void EliminaSessione(String documentID);
    void OrdinaTutteSessioni(FirestoreCallback firestoreCallback);
    void OrdinaSessioniPerData(int day, int month, int year, FirestoreCallback firestoreCallback);
}
