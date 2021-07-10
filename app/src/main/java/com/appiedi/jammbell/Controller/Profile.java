package com.appiedi.jammbell.Controller;

import com.appiedi.jammbell.Model.Utente;

public class Profile implements IProfile
{

    public Profile() {
    }

    public void ModificaProfilo(String Nome, String Cognome, int Altezza, String Data, String Sesso, int Peso, String documentID)
    {
        Utente utente = new Utente();
        utente.pushDatiUtenteDatabase(Nome, Cognome, Altezza, Data, Sesso, Peso, documentID);
    }
}
