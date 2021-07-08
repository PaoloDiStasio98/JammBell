package com.example.jammbell.Model;

public interface IFirestoreCallback {
    void onCallback();
    void onLoginCallback(Boolean successo);
    void onSignupCallback(Boolean usernamevalido);
    void emailSentCallback();

}
