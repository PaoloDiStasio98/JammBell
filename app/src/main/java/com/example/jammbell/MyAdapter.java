package com.example.jammbell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    String data1[], data2[];
    Context context;

    public MyAdapter(Context ct, String Titoli[], String Descrizione[]) {
        context = ct;
        data1 = Titoli;
        data2 = Descrizione;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
       View view = inflater.inflate(R.layout.cell_layout_statisticheprofilo, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder( @NonNull MyAdapter.MyViewHolder holder, int position) {
        holder.TitoloTextView.setText(data1[position]);
        holder.DescrizioneTextView.setText(data2[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView TitoloTextView;
        TextView DescrizioneTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TitoloTextView = itemView.findViewById(R.id.TitoloCellaTextView);
            DescrizioneTextView = itemView.findViewById(R.id.DescrizioneCellaTextView);
        }
    }
}
