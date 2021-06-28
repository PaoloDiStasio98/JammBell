package com.example.jammbell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapterChallenge extends RecyclerView.Adapter<MyAdapterChallenge.MyViewHolderChallenge> {

ArrayList<String> data1, data2, data3, data4, data5;
Context context;

public MyAdapterChallenge(Context ct, ArrayList<String> DataInizio, ArrayList<String> DataFine, ArrayList<String> Nome, ArrayList<String> UsernamePartecipante, ArrayList<String> Stato){
    context = ct;
    data1 = DataInizio;
    data2 = DataFine;
    data3 = Nome;
    data4 = UsernamePartecipante;
    data5 = Stato;

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


        if(data5.get(position).matches("In attesa"))
        holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.boxinattesa));
        if(data5.get(position).matches("Attiva"))
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.boxattiva));
        if(data5.get(position).matches("Terminata"))
            holder.itemView.setBackground(ContextCompat.getDrawable(context, R.drawable.box1));


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

        public  MyViewHolderChallenge(@NonNull View itemView){
            super(itemView);
            DataInizioTextView = itemView.findViewById(R.id.DataInizioCellaTextView);
            DataFineTextView = itemView.findViewById(R.id.DataFineCellaTextView);
            NomeChallengeTextView = itemView.findViewById(R.id.NomeGaraCellaTextView);
            UsernamePartecipanteTextView = itemView.findViewById(R.id.UsernameAmicoCellaTextView);
            StatoChallengeTextView = itemView.findViewById(R.id.StatoCellaTextView);
        }


    }
}
