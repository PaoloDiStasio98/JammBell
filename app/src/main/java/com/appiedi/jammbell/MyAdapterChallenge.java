package com.appiedi.jammbell;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MyAdapterChallenge extends RecyclerView.Adapter<MyAdapterChallenge.MyViewHolderChallenge>
{

    ArrayList<String> data1, data2, data3, data4, data5, data6, data7, data8, data9;
    Context context;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MyAdapterChallenge(Context ct, ArrayList<String> DataInizio, ArrayList<String> DataFine, ArrayList<String> Nome, ArrayList<String> UsernamePartecipante, ArrayList<String> Stato, ArrayList<String> UsernameCreatore, ArrayList<String> DocumentID, ArrayList<String> Risultato, ArrayList<String> UsernameVincitore){
        context = ct;
        data1 = DataInizio;
        data2 = DataFine;
        data3 = Nome;
        data4 = UsernamePartecipante;
        data5 = Stato;
        data6 = UsernameCreatore;
        data7 = DocumentID;
        data8 = Risultato;
        data9 = UsernameVincitore;

    }

    private void CheckGaraFinita(int position) {

        DateTimeFormatter dtf = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();
            Log.d("data1", dtf.format(now));
            Log.d("data1", String.valueOf(data2));

            if(data2.get(position).compareTo(dtf.format(now)) < 0){
                Log.d("data1", "La gara  " + data3.get(position) + "  in data " + data2.get(position) + " ?? terminata" );
                data5.set(position, "Terminata");
                db.collection("Gara")
                        .document(data7.get(position))
                        .update(
                                "Stato", "Terminata"
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error updating document", e);
                            }
                        });
            }
        }
    }


    @NonNull
    @Override
    public MyViewHolderChallenge onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cellgara, parent, false);

        return new MyViewHolderChallenge(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterChallenge.MyViewHolderChallenge holder, int position) {

        Log.d("cella", "cella creata");
        String datainizioModificata = changeFormatData(data1.get(position));
        String datafineModificata = changeFormatData(data2.get(position));

        holder.DataInizioTextView.setText("Inizio: " + datainizioModificata);
        holder.DataFineTextView.setText(" fine: " + datafineModificata);
        holder.NomeChallengeTextView.setText("Nome gara: " + data3.get(position));
        holder.UsernamePartecipanteTextView.setText(data4.get(position));
        holder.StatoChallengeTextView.setText(data5.get(position));

        CheckGaraFinita(position);

        Utente utente = new Utente();
        utente.getDatiUtenteDatabase(new FirestoreCallback()
        {
            @Override
            public void onCallback()
            {
                if(data5.get(position).matches("In attesa"))
                {
                    holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.boxinattesa));
                    holder.ButtonConfermaRifiuta.setVisibility(View.VISIBLE);

                    if(utente.getUsername().matches(data4.get(position)))
                    { //l'utente connesso ?? il partecipante
                        holder.ButtonConfermaRifiuta.setBackgroundResource(R.drawable.icona_ok_verde);
                        holder.ButtonRifiutaGara.setBackgroundResource(R.drawable.icona_x_rosso);
                        holder.ButtonRifiutaGara.setVisibility(View.VISIBLE);
                        holder.ButtonRifiutaGara.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                data3.remove(position);
                                notifyItemRemoved(position);

                                EliminaGara(position);
                            }
                        });
                        holder.UsernamePartecipanteTextView.setText("Invitato da: " + data6.get(position));
                        holder.ButtonConfermaRifiuta.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                data5.set(position, "Attiva");
                                notifyItemChanged(position);

                                DateTimeFormatter dtf = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                                    LocalDateTime now = LocalDateTime.now();
                                    Log.d("data", dtf.format(now));

                                    db.collection("Gara")
                                            .document(data7.get(position))
                                            .update(
                                                    "Stato", "Attiva",
                                                    "Data", now

                                            )
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG", "DocumentSnapshot successfully updated!");

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Error updating document", e);
                                                }
                                            });

                                }
                                notifyDataSetChanged();
                            }
                        });

                    }
                    else
                    { //l'utente connesso ?? il creatore
                        holder.UsernamePartecipanteTextView.setText("Invitato: " + data4.get(position));
                        holder.ButtonConfermaRifiuta.setBackgroundResource(R.drawable.icona_x_rosso);
                        holder.StatoChallengeTextView.setText("Stato gara: " + data5.get(position));
                        holder.ButtonConfermaRifiuta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("cella", "tasto annulla cliccato");
                                data3.remove(position);
                                notifyItemRemoved(position);
                                // notifyItemRangeChanged(position, data3.size());
                                EliminaGara(position);


                            }
                        });
                    }
                }
                if(data5.get(position).matches("Attiva")) {
                    holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.boxattiva));
                    holder.ButtonConfermaRifiuta.setVisibility(View.INVISIBLE);
                    holder.ButtonRifiutaGara.setVisibility(View.INVISIBLE);
                    holder.StatoChallengeTextView.setText("Stato gara: " + data5.get(position));
                    if(utente.getUsername().matches(data4.get(position))){ //l'utente connesso ?? il partecipante
                        holder.UsernamePartecipanteTextView.setText("Invitato da: " + data6.get(position));
                    }
                    else {
                        holder.UsernamePartecipanteTextView.setText("Invitato: " + data4.get(position));
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ResoContochallengeActivity.class);
                            intent.putExtra("IDGara", data7.get(position));
                            intent.putExtra("STATO", "Attiva");
                            v.getContext().startActivity(intent);

                        }
                    });
                }
                if(data5.get(position).matches("Terminata")) {
                    Log.d("StatoTerminato", String.valueOf(position));
                    Log.d("StatoTerminato", String.valueOf(data5.get(position)));
                    Log.d("StatoTerminato", String.valueOf(data8.get(position)));
                    Log.d("StatoTerminato", String.valueOf(data9.get(position)));
                    Log.d("StatoTerminato", String.valueOf(data8));
                    Log.d("StatoTerminato", String.valueOf(data9));
                    Log.d("StatoTerminato", String.valueOf(data5));

                    holder.StatoChallengeTextView.setText("Stato gara: " + data5.get(position));
                    holder.UsernamePartecipanteTextView.setVisibility(View.INVISIBLE);

                    holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.box1));
                    holder.ButtonConfermaRifiuta.setVisibility(View.INVISIBLE);
                    holder.RisultatoChallengeTextView.setVisibility(View.VISIBLE);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ResoContochallengeActivity.class);
                            intent.putExtra("IDGara", data7.get(position));
                            intent.putExtra("STATO", "Terminata");
                            v.getContext().startActivity(intent);

                        }
                    });


                    if(data8.get(position).matches("null")){
                        holder.RisultatoChallengeTextView.setText("Clicca per scoprire il risultato");
                    }
                    else {
                        Log.d("provadata", data8.get(position) + " " + data8.get(position).length());
                        holder.RisultatoChallengeTextView.setText("Risultato: " + data8.get(position) + "\n Vincitore: " + data9.get(position));
                    }
                }
            }
        });
    }

    private String changeFormatData(String data) {
        String giorno = data.substring(8, 10);
        String mese = data.substring(5, 7);
        String anno = data.substring(0, 4);
        String datamodificata = giorno + "/" + mese + "/" + anno;
        return datamodificata;
    }


    @Override
    public int getItemCount() {
        return data3.size();
    }

    public class MyViewHolderChallenge extends  RecyclerView.ViewHolder {
        TextView DataInizioTextView;
        TextView DataFineTextView;
        TextView NomeChallengeTextView;
        TextView UsernamePartecipanteTextView;
        TextView StatoChallengeTextView;
        TextView RisultatoChallengeTextView;
        Button ButtonConfermaRifiuta;
        Button ButtonRifiutaGara;

        public  MyViewHolderChallenge(@NonNull View itemView){

            super(itemView);
            DataInizioTextView = itemView.findViewById(R.id.DataInizioCellaTextView);
            DataFineTextView = itemView.findViewById(R.id.DataFineCellaTextView);
            NomeChallengeTextView = itemView.findViewById(R.id.NomeGaraCellaTextView);
            UsernamePartecipanteTextView = itemView.findViewById(R.id.UsernameAmicoCellaTextView);
            StatoChallengeTextView = itemView.findViewById(R.id.StatoCellaTextView);
            ButtonConfermaRifiuta = itemView.findViewById(R.id.ButtonConfermaRifiuta);
            RisultatoChallengeTextView = itemView.findViewById(R.id.RisultatoCellaTextView);
            ButtonRifiutaGara = itemView.findViewById(R.id.ButtonRifiutaGara);
        }



    }

    public void EliminaGara(int position){
        db.collection("Gara").document(data7.get(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Eliminato", "DocumentSnapshot successfully deleted!");



                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log.w(TAG, "Error deleting document", e);
                    }
                });

        db.collection("GaraUtente")
                .whereEqualTo("IDGara", data7.get(position))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String IDgara = document.getId();


                                db.collection("GaraUtente")
                                        .document(IDgara)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Eliminato", "DocumentSnapshot successfully deleted!");
                                            }

                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                            }
                        }
                        else
                        {

                            Log.d("database", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
