package com.appiedi.jammbell.Model;

import java.util.ArrayList;
import java.util.Map;

public interface ISessione
{
    void getDatiSessioneDatabase();
    ArrayList<Double> StatisticheTotali();
    void EliminaSessione(String documentID);
    void OrdinaTutteSessioni(FirestoreCallback firestoreCallback);
    void OrdinaSessioniPerData(int day, int month, int year, FirestoreCallback firestoreCallback);
    void pushSessione(Map<String, Object> SessioneVeloce, FirestoreCallback firestoreCallback);
}
