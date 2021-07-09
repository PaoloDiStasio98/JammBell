package com.example.jammbell;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyAdapterStorico extends RecyclerView.Adapter<MyAdapterStorico.MyViewHolderStorico> {

    ArrayList<String> data1, data2, data3, data4, data5, data6, data7, data8;
    Context context;

    public MyAdapterStorico(Context ct, ArrayList<String> DataTempo, ArrayList<String> Km, ArrayList<String> Tempo, ArrayList<String> Calorie, ArrayList<String> Passi, ArrayList<String> Velocita, ArrayList<String> Valutazione, ArrayList<String> DocumentoID)
    {
        context = ct;
        data1 = DataTempo;
        data2 = Km;
        data3 = Tempo;
        data4 = Calorie;
        data5 = Passi;
        data6 = Velocita;
        data7 = Valutazione;
        data8 = DocumentoID;
    }

    @NonNull
    @Override
    public MyViewHolderStorico onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cellstorico, parent, false);
        return new MyViewHolderStorico(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterStorico.MyViewHolderStorico holder, int position) {
        holder.SessioneDataStoricoTextView.setText(data1.get(position));
        holder.KmCellaTextView.setText(data2.get(position));
        holder.TempoCellaTextView.setText(data3.get(position));
        holder.CalorieCellaTextView.setText(data4.get(position));
        holder.PassiCellaTextView.setText(data5.get(position));
        holder.VelocitaCellaTextView.setText(data6.get(position));
        holder.ValutazioneCellaTextView.setText(data7.get(position));

    }

    @Override
    public int getItemCount() {
        return data2.size();
    }

    public class MyViewHolderStorico extends RecyclerView.ViewHolder {

        TextView SessioneDataStoricoTextView;
        TextView TempoCellaTextView;
        TextView CalorieCellaTextView;
        TextView KmCellaTextView;
        TextView PassiCellaTextView;
        TextView VelocitaCellaTextView;
        TextView ValutazioneCellaTextView;

        public MyViewHolderStorico(@NonNull View itemView){
            super(itemView);
            SessioneDataStoricoTextView = itemView.findViewById(R.id.SessioneDataCellaTextView);
            TempoCellaTextView = itemView.findViewById(R.id.TempoCellaTextView);
            CalorieCellaTextView = itemView.findViewById(R.id.CalorieCellaTextView);
            KmCellaTextView = itemView.findViewById(R.id.KmCellaTextView);
            PassiCellaTextView = itemView.findViewById(R.id.PassiCellaTextView);
            VelocitaCellaTextView = itemView.findViewById(R.id.VelocitaCellaTextView);
            ValutazioneCellaTextView = itemView.findViewById(R.id.ValutazioneCellaTextView);


        }
    }
}
