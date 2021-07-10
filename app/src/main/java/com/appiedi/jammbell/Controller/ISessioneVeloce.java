package com.appiedi.jammbell.Controller;

import com.appiedi.jammbell.Model.FirestoreCallback;

import java.util.Map;

public interface ISessioneVeloce
{
    void pushSessione(Map<String, Object> SessioneVeloce, FirestoreCallback firestoreCallback);
    void OrdinaSessioniPerData(int day, int month, int year, FirestoreCallback firestoreCallback);
}
