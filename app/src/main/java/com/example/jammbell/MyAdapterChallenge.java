package com.example.jammbell;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyAdapterChallenge extends RecyclerView.Adapter<MyAdapterChallenge.MyViewHolderChallenge> {

ArrayList<String> data1, data2, data3, data4, data5, data6, data7;
Context context;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

Main2Activity main2Activity = new Main2Activity();

public MyAdapterChallenge(Context ct, ArrayList<String> DataInizio, ArrayList<String> DataFine, ArrayList<String> Nome, ArrayList<String> UsernamePartecipante, ArrayList<String> Stato, ArrayList<String> UsernameCreatore, ArrayList<String> DocumentID){
    context = ct;
    data1 = DataInizio;
    data2 = DataFine;
    data3 = Nome;
    data4 = UsernamePartecipante;
    data5 = Stato;
    data6 = UsernameCreatore;
    data7 = DocumentID;

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

        holder.DataInizioTextView.setText(data1.get(position));
        holder.DataFineTextView.setText(data2.get(position));
        holder.NomeChallengeTextView.setText(data3.get(position));
        holder.UsernamePartecipanteTextView.setText(data4.get(position));
        holder.StatoChallengeTextView.setText(data5.get(position));


        if(data5.get(position).matches("In attesa")) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.boxinattesa));
            holder.ButtonConfermaRifiuta.setVisibility(View.VISIBLE);

            if(String.valueOf(main2Activity.Utente.get("Username")).matches(data4.get(position))){
                holder.ButtonConfermaRifiuta.setText("Accetta");
                holder.UsernamePartecipanteTextView.setText("Invitato da: " + data6.get(position));
                holder.ButtonConfermaRifiuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        db.collection("Gara")
                                .document(data7.get(position))
                                .update(
                                        "Stato", "Attiva"

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
                });

            }
            else {
                holder.UsernamePartecipanteTextView.setText("Invitato: " + data4.get(position));
                holder.ButtonConfermaRifiuta.setText("Annulla");
                holder.ButtonConfermaRifiuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

                    }
                });
            }
        }
        if(data5.get(position).matches("Attiva")) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.boxattiva));
            holder.ButtonConfermaRifiuta.setVisibility(View.INVISIBLE);
        }
        if(data5.get(position).matches("Terminata")) {
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.box1));
            holder.ButtonConfermaRifiuta.setVisibility(View.INVISIBLE);

        }


    }

    public void cambioStato(){

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
        Button ButtonConfermaRifiuta;

        public  MyViewHolderChallenge(@NonNull View itemView){
            super(itemView);
            DataInizioTextView = itemView.findViewById(R.id.DataInizioCellaTextView);
            DataFineTextView = itemView.findViewById(R.id.DataFineCellaTextView);
            NomeChallengeTextView = itemView.findViewById(R.id.NomeGaraCellaTextView);
            UsernamePartecipanteTextView = itemView.findViewById(R.id.UsernameAmicoCellaTextView);
            StatoChallengeTextView = itemView.findViewById(R.id.StatoCellaTextView);
            ButtonConfermaRifiuta = itemView.findViewById(R.id.ButtonConfermaRifiuta);
        }


    }
}
