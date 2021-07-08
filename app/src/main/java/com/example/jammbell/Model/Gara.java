package com.example.jammbell.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Gara {

    private String   Data_inizio, Data_fine, Nome, Stato, Username_creatore, Username_partecipante, ID_creatore, ID_partecipante;
    private ArrayList<HashMap<String,String>> Data_creazione = new ArrayList<>();

    public Gara(String data_inizio, String data_fine, String nome, String stato, String username_creatore, String username_partecipante, String ID_creatore, String ID_partecipante, ArrayList<HashMap<String, String>> data_creazione) {
        Data_inizio = data_inizio;
        Data_fine = data_fine;
        Nome = nome;
        Stato = stato;
        Username_creatore = username_creatore;
        Username_partecipante = username_partecipante;
        this.ID_creatore = ID_creatore;
        this.ID_partecipante = ID_partecipante;
        Data_creazione = data_creazione;
    }

    public Gara(){

    }

    public String getData_inizio() {
        return Data_inizio;
    }

    public void setData_inizio(String data_inizio) {
        Data_inizio = data_inizio;
    }

    public String getData_fine() {
        return Data_fine;
    }

    public void setData_fine(String data_fine) {
        Data_fine = data_fine;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getStato() {
        return Stato;
    }

    public void setStato(String stato) {
        Stato = stato;
    }

    public String getUsername_creatore() {
        return Username_creatore;
    }

    public void setUsername_creatore(String username_creatore) {
        Username_creatore = username_creatore;
    }

    public String getUsername_partecipante() {
        return Username_partecipante;
    }

    public void setUsername_partecipante(String username_partecipante) {
        Username_partecipante = username_partecipante;
    }

    public String getID_creatore() {
        return ID_creatore;
    }

    public void setID_creatore(String ID_creatore) {
        this.ID_creatore = ID_creatore;
    }

    public String getID_partecipante() {
        return ID_partecipante;
    }

    public void setID_partecipante(String ID_partecipante) {
        this.ID_partecipante = ID_partecipante;
    }

    public ArrayList<HashMap<String, String>> getData_creazione() {
        return Data_creazione;
    }

    public void setData_creazione(ArrayList<HashMap<String, String>> data_creazione) {
        Data_creazione = data_creazione;
    }
}
