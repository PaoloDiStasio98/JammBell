package com.example.jammbell.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.jammbell.ChallengeFragment;
import com.example.jammbell.CreateGameDialogClass;
import com.example.jammbell.MyAdapterChallenge;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gara
{
    private ArrayList<String>                 Data_inizio           = new ArrayList<>();
    private ArrayList<String>                 Data_fine             = new ArrayList<>();
    private ArrayList<String>                 Nome                  = new ArrayList<>();
    private ArrayList<String>                 Stato                 = new ArrayList<>();
    private ArrayList<String>                 Username_creatore     = new ArrayList<>();
    private ArrayList<String>                 Username_partecipante = new ArrayList<>();
    private ArrayList<String>                 ID_creatore           = new ArrayList<>();
    private ArrayList<String>                 ID_partecipante       = new ArrayList<>();
    private ArrayList<String>                 IDDocumento           = new ArrayList<>();
    private ArrayList<HashMap<String,String>> Data_creazione        = new ArrayList<>();
    private ArrayList<String>                 Risultato             = new ArrayList<>();
    private ArrayList<String>                 Username_vincitore    = new ArrayList<>();
    private String                            Utente_Invitato;
    private String                            Username_Creatore;


    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUsername_Creatore() {
        return Username_Creatore;
    }

    public void setUsername_Creatore(String username_Creatore) {
        Username_Creatore = username_Creatore;
    }

    public String getUtente_Invitato() {
        return Utente_Invitato;
    }

    public void setUtente_Invitato(String utente_Invitato) {
        Utente_Invitato = utente_Invitato;
    }

    public ArrayList<String> getUsername_vincitore() {
        return Username_vincitore;
    }

    public void setUsername_vincitore(ArrayList<String> username_vincitore) {
        Username_vincitore = username_vincitore;
    }

    public ArrayList<String> getRisultato() {
        return Risultato;
    }

    public void setRisultato(ArrayList<String> risultato) {
        Risultato = risultato;
    }

    public Gara(){
    }

    public ArrayList<String> getData_inizio() {
        return Data_inizio;
    }

    public void setData_inizio(ArrayList<String> data_inizio) {
        Data_inizio = data_inizio;
    }

    public ArrayList<String> getData_fine() {
        return Data_fine;
    }

    public void setData_fine(ArrayList<String> data_fine) {
        Data_fine = data_fine;
    }

    public ArrayList<String> getNome() {
        return Nome;
    }

    public void setNome(ArrayList<String> nome) {
        Nome = nome;
    }

    public ArrayList<String> getStato() {
        return Stato;
    }

    public void setStato(ArrayList<String> stato) {
        Stato = stato;
    }

    public ArrayList<String> getUsername_creatore() {
        return Username_creatore;
    }

    public void setUsername_creatore(ArrayList<String> username_creatore) {
        Username_creatore = username_creatore;
    }

    public ArrayList<String> getUsername_partecipante() {
        return Username_partecipante;
    }

    public void setUsername_partecipante(ArrayList<String> username_partecipante) {
        Username_partecipante = username_partecipante;
    }

    public ArrayList<String> getID_creatore() {
        return ID_creatore;
    }

    public void setID_creatore(ArrayList<String> ID_creatore) {
        this.ID_creatore = ID_creatore;
    }

    public ArrayList<String> getID_partecipante() {
        return ID_partecipante;
    }

    public void setID_partecipante(ArrayList<String> ID_partecipante) {
        this.ID_partecipante = ID_partecipante;
    }

    public ArrayList<String> getIDDocumento() {
        return IDDocumento;
    }

    public void setIDDocumento(ArrayList<String> IDDocumento) {
        this.IDDocumento = IDDocumento;
    }

    public ArrayList<HashMap<String, String>> getData_creazione() {
        return Data_creazione;
    }

    public void setData_creazione(ArrayList<HashMap<String, String>> data_creazione) {
        Data_creazione = data_creazione;
    }

    public void getGareUtenteDatabase(FirestoreCallback firestoreCallback1)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Utente utente = new Utente();
        utente.getDatiUtenteDatabase(new FirestoreCallback()
        {
            @Override
            public void onCallback()
            {
                Log.d("IDUTENTE",utente.getIDUtente());
                db.collection("GaraUtente")
                        .whereEqualTo("UTENTEID", utente.getIDUtente())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        String IDGara = String.valueOf(document.get("IDGara"));
                                        Log.d("CHALLENGEIDGARA", IDGara);

                                        db.collection("Gara")
                                                .document(IDGara)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                                    {

                                                        if (task.isSuccessful())
                                                        {
                                                            DocumentSnapshot document1 = task.getResult();
                                                            Log.d("CHALLENGEID", "dentrooooooo");
                                                            Log.d("CHALLENGEID", String.valueOf(document1.get("Datainizio")));
                                                            Log.d("CHALLENGEID", String.valueOf(document1.getId()));

                                                            Data_inizio.add(String.valueOf(document1.get("Datainizio")));
                                                            Data_fine.add(String.valueOf(document1.get("Datafine")));
                                                            Nome.add(String.valueOf(document1.get("Nome")));
                                                            Stato.add(String.valueOf(document1.get("Stato")));
                                                            Username_partecipante.add(String.valueOf(document1.get("UsernamePartecipante")));
                                                            Username_creatore.add(String.valueOf(document1.get("UsernameCreatore")));
                                                            IDDocumento.add(document1.getId());

                                                            if (document1.get("Stato").equals("In attesa") || document1.get("Stato").equals("Attiva"))
                                                            {
                                                                Username_vincitore.add("null");
                                                                Risultato.add("null");
                                                            }
                                                            else
                                                            {
                                                                Username_vincitore.add(String.valueOf(document1.get("UsernameVincitore")));
                                                                Risultato.add(String.valueOf(document1.get("Risultato")));
                                                            }

                                                            firestoreCallback1.onPullGareCallback();
                                                            }

                                                        }
                                                    });
                                    }
                                }
                            }
                        });

            }
        });

    }

    public void searchUtente(String username, FirestoreCallback firestoreCallback)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("Utente")
                    .whereEqualTo("Username", username)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {

                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Utente_Invitato = String.valueOf(document.get("IDUtente"));
                                }
                                firestoreCallback.onCallback();
                            }


                        }
                    });
        }
    }

    public void pullUsernameCreatore(FirestoreCallback firestoreCallback)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(currentUser != null)
        {
            db.collection("Utente")
                    .whereEqualTo("IDUtente", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Username_Creatore = String.valueOf(document.get("Username"));
                                }
                                firestoreCallback.onCallback();
                            }
                            else
                            {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else
        {
            Log.d("utenteid", "niente vuoto");
        }
    }



}
