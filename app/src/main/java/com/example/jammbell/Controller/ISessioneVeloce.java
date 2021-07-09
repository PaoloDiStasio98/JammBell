package com.example.jammbell.Controller;

import com.example.jammbell.Model.FirestoreCallback;

import java.util.Map;

public interface ISessioneVeloce
{
    void pushSessione(Map<String, Object> SessioneVeloce, FirestoreCallback firestoreCallback);
}
