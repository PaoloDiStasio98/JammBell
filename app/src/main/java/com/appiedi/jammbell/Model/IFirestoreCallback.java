package com.appiedi.jammbell.Model;

public interface IFirestoreCallback
{
    void onCallback();
    void onLoginCallback(Boolean successo);
    void onSignupCallback(Boolean usernamevalido);
    void emailSentCallback();
    void onPullSessioneCallback();
    void onPullGareCallback();
}
