package com.example.jammbell;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Sessione;
import com.example.jammbell.View.IActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class ActivityFragment extends Fragment implements IActivity
{
    private Menu menu;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private ArrayList<String> StoricoSessioneData = new ArrayList<String>();
    private ArrayList<String> StoricoKm = new ArrayList<String>();
    private ArrayList<String> StoricoTempo = new ArrayList<String>();
    private ArrayList<String> StoricoCalorie = new ArrayList<String>();
    private ArrayList<String> StoricoPassi = new ArrayList<String>();
    private ArrayList<String> StoricoVelocitaMedia = new ArrayList<String>();
    private ArrayList<String> StoricoValutazione = new ArrayList<String>();
    private ArrayList<String> ArrayDocumentoID = new ArrayList<String>();

    private RecyclerView recyclerViewStorico;

    private int dayprova;
    private int monthprova;
    private int yearprova;

    private boolean filtroapplicato = false;
    private TextView filtroTextView;
    private DatePickerDialog dialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState)
    {
        recyclerViewStorico =  getView().findViewById(R.id.recyclerViewStorico);
        filtroTextView      =  getView().findViewById(R.id.FiltroTextView);

        setHasOptionsMenu(true);

        //eliminazione delle celle
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir)
            {
                    int position = viewHolder.getAdapterPosition();

                    Log.d("Documento1", StoricoCalorie.get(position).split(" ")[1]);
                    Log.d("Documento1", StoricoPassi.get(position).split(" ")[1]);
                    Log.d("Documento1", StoricoCalorie.toString());
                    Log.d("Documento1", ArrayDocumentoID.get(position));
                    Log.d("Documento1", ArrayDocumentoID.toString());

                    //Elimina la sessione
                    Sessione sessione = new Sessione();
                    sessione.EliminaSessione(ArrayDocumentoID.get(position));

                    StoricoSessioneData.remove(position);
                    StoricoCalorie.remove(position);
                    StoricoKm.remove(position);
                    StoricoPassi.remove(position);
                    StoricoTempo.remove(position);
                    StoricoValutazione.remove(position);
                    StoricoVelocitaMedia.remove(position);
                    ArrayDocumentoID.remove(position);

                    if(filtroapplicato == false)
                        PullDatiDatabaseStorico();
                    else
                        PullDatiDatabaseStoricoData(dayprova,monthprova,yearprova);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewStorico);

        dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month + 1;
                Log.d("provadata", "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                dayprova = day;
                monthprova = month;
                yearprova = year;

                filtroapplicato = true;
                menu.getItem(1).setVisible(true);

                Log.d("Documento2", ArrayDocumentoID.toString());
                Log.d("Documento2", StoricoCalorie.toString());
                Log.d("Documento2", StoricoKm.toString());
                Log.d("Documento2", StoricoTempo.toString());

                if(filtroapplicato == true)
                {
                    PullDatiDatabaseStoricoData(day, month, year);
                }

            }
        };

       if(filtroapplicato == false)
             PullDatiDatabaseStorico();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater)
    {
        inflater.inflate(R.menu.upbar_menu, menu);
        this.menu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu)
    {
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.upbar_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.search_button:
                {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                 dialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);

                 dialog.show();

                return true;
            }

            case R.id.close_button:
                {

                    menu.getItem(1).setVisible(false);
                    PullDatiDatabaseStorico();
                    filtroapplicato = false;
                    return true;
                }

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void clearCell()
    {
        StoricoKm.clear();
        StoricoCalorie.clear();
        StoricoPassi.clear();
        StoricoSessioneData.clear();
        StoricoTempo.clear();
        StoricoVelocitaMedia.clear();
        StoricoValutazione.clear();
        ArrayDocumentoID.clear();
    }

    private void PullDatiDatabaseStoricoData(int day, int month, int year)
    {
        clearCell();

        Sessione sessione = new Sessione();

        sessione.OrdinaSessioniPerData(day, month, year, new FirestoreCallback()
        {
            @Override
            public void onPullSessioneCallback()
            {
                DecimalFormat df = new DecimalFormat("##########.###");
                df.setRoundingMode(RoundingMode.DOWN);
                DecimalFormat df1 = new DecimalFormat("###.##");
                df.setRoundingMode(RoundingMode.DOWN);

                for(int i = 0; i < sessione.getPassi().size(); i++)
                {
                    StoricoKm.add("Km: " + String.valueOf(df.format(sessione.getKm_Percorsi().get(i))));
                    StoricoCalorie.add("Calorie: " + String.valueOf(sessione.getCalorie_Bruciate().get(i)));
                    StoricoPassi.add("Passi: " + String.valueOf(sessione.getPassi().get(i)));
                    StoricoSessioneData.add("Sessione del " + String.valueOf(sessione.getData().get(i).get("dayOfMonth")) + "/" + String.valueOf(sessione.getData().get(i).get("monthValue")) + "/" + String.valueOf(sessione.getData().get(i).get("year")));
                    StoricoTempo.add("Durata: " + formatSecondDateTime(Integer.parseInt(String.valueOf(sessione.getTempo().get(i)))));
                    StoricoVelocitaMedia.add("Velocità media: " + String.valueOf(df1.format(sessione.getVelocita_Media().get(i))));
                    StoricoValutazione.add(String.valueOf(sessione.getValutazione().get(i)) + "/5");
                    ArrayDocumentoID.add(sessione.getDocumentID().get(i));
                }

                if(StoricoKm.size() == 0)
                {
                    Toast.makeText(getContext(), "Nessuna sessione trovata", Toast.LENGTH_SHORT).show();
                    filtroTextView.setText("Nessuna sessione");
                }
                else
                {
                    filtroTextView.setText("Filtro, giorno: " + day + "/" + month + "/" + year);
                }
                MyAdapterStorico myAdapter = new MyAdapterStorico(getContext(), StoricoSessioneData, StoricoKm, StoricoTempo, StoricoCalorie, StoricoPassi, StoricoVelocitaMedia, StoricoValutazione, ArrayDocumentoID);
                recyclerViewStorico.setAdapter(myAdapter);
                recyclerViewStorico.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }

    private void PullDatiDatabaseStorico()
    {
        clearCell();

        Sessione sessione = new Sessione();

        sessione.OrdinaTutteSessioni(new FirestoreCallback()
        {
            @Override
            public void onPullSessioneCallback()
            {
                DecimalFormat df = new DecimalFormat("##########.###");
                df.setRoundingMode(RoundingMode.DOWN);
                DecimalFormat df1 = new DecimalFormat("###.##");
                df.setRoundingMode(RoundingMode.DOWN);

                for(int i = 0; i < sessione.getPassi().size(); i++)
                {
                    StoricoKm.add("Km: " + String.valueOf(df.format(sessione.getKm_Percorsi().get(i))));
                    StoricoCalorie.add("Calorie: " + String.valueOf(sessione.getCalorie_Bruciate().get(i)));
                    StoricoPassi.add("Passi: " + String.valueOf(sessione.getPassi().get(i)));
                    StoricoSessioneData.add("Sessione del " + String.valueOf(sessione.getData().get(i).get("dayOfMonth")) + "/" + String.valueOf(sessione.getData().get(i).get("monthValue")) + "/" + String.valueOf(sessione.getData().get(i).get("year")));

                    Log.d("STRUNZ", String.valueOf(sessione.getTempo().get(i)));
                    if(sessione.getTempo().get(i) == 0){
                        StoricoTempo.add("Durata: 0:00");
                    }
                    else{
                        StoricoTempo.add("Durata: " + formatSecondDateTime(Integer.parseInt(String.valueOf(sessione.getTempo().get(i)))));
                    }

                    StoricoVelocitaMedia.add("Velocità media: " + String.valueOf(df1.format(sessione.getVelocita_Media().get(i))));
                    StoricoValutazione.add(String.valueOf(sessione.getValutazione().get(i)) + "/5");
                    ArrayDocumentoID.add(sessione.getDocumentID().get(i));
                }

                if(StoricoKm.size() == 0)
                {
                    Toast.makeText(getContext(), "Nessuna sessione trovata", Toast.LENGTH_SHORT).show();
                    filtroTextView.setText("Nessuna sessione");
                }
                else
                {
                    filtroTextView.setText("Tutte le tue sessioni");
                }

                MyAdapterStorico myAdapter = new MyAdapterStorico(getContext(), StoricoSessioneData, StoricoKm, StoricoTempo, StoricoCalorie, StoricoPassi, StoricoVelocitaMedia, StoricoValutazione, ArrayDocumentoID);
                recyclerViewStorico.setAdapter(myAdapter);
                recyclerViewStorico.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });
    }


    private static String formatSecondDateTime(int scound)
    {
        if(scound <= 0)return "";
        int h = scound / 3600;
        int m = scound % 3600 / 60;
        int s = scound % 60; // Less than 60 is the second, enough 60 is the minute
        if(m<10 && s<10)
            return h+":0"+m+":0"+s+"";
        if(m < 10)
            return h+":0"+m+":"+s+"";
        if(s < 10)
            return h+":"+m+":0"+s+"";

        return h+":"+m+":"+s+"";
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }
}