package com.appiedi.jammbell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Gara;
import com.appiedi.jammbell.Model.Sessione;
import com.appiedi.jammbell.Model.Utente;
import com.appiedi.jammbell.View.IProfileView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class ProfileFragment extends Fragment implements IProfileView
{
    private Main2Activity main2Activity = new Main2Activity();
    private Utente utente               = new Utente();
    private Sessione sessione           = new Sessione();

    private Boolean garaNonEsistente = true;
    private FragmentActivity myContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private TextView  ciaoNomeeCognomeTextView;
    private ImageView ImageProfilo;
    private TextView  usernameTextView;
    private TextView  datadinascitaTextView;
    private TextView  pesoTextView;
    private TextView  altezzaTextView;

    private String TitoliStatistiche[]      = {"Km percorsi", "Passi", "Calorie", "Ore totali", "Gare", "Gare vinte"};
    private String DescrizioneStatistiche[] = {"0", "0", "0", "0", "0", "0"};

    private RecyclerView recyclerViewStatistiche;
    private BarChart barChart;

    private int numGare = 0;
    private int numGareVinte = 0;

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState)
    {
        ciaoNomeeCognomeTextView = getView().findViewById(R.id.CiaoNomeCognomeText);
        usernameTextView         = getView().findViewById(R.id.UsernameTextView);
        datadinascitaTextView    = getView().findViewById(R.id.DataTextView);
        pesoTextView             = getView().findViewById(R.id.PesoProfiloTextView);
        altezzaTextView          = getView().findViewById(R.id.AltezzaProfiloTextView);
        ImageProfilo             = getView().findViewById(R.id.Imageprofilo);

        setHasOptionsMenu(true);

        recyclerViewStatistiche  =  getView().findViewById(R.id.recyclerViewStatistiche);

        barChart = getView().findViewById(R.id.graficoSettimanale);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Prelevo dati utente database
        utente.getDatiUtenteDatabase(new FirestoreCallback() {
            @Override
            public void onCallback() {

            }
        });

        //Prelevo sessioni utente database
        sessione.getDatiSessioneDatabase();
        utente.getDatiUtenteDatabase(new FirestoreCallback() {
            @Override
            public void onCallback()
            {
                usernameTextView.setText("Username: " + utente.getUsername());
                ciaoNomeeCognomeTextView.setText(Html.fromHtml("Ciao, " +"<b>"+ utente.getNome() + " " + utente.getCognome() + "</b>"));
                datadinascitaTextView.setText("Data di nascita: " + utente.getData_di_nascita());
                pesoTextView.setText("Peso: " + utente.getPeso() + " Kg");
                altezzaTextView.setText("Altezza: " + utente.getAltezza() + " cm");
                if(utente.getSesso().equals("Maschio"))
                {
                    int blu = Color.parseColor("#01BAEF");
                    ImageProfilo.setColorFilter(blu);
                }
                else if(utente.getSesso().equals("Femmina"))
                {
                    int rosa = Color.parseColor("#E16684");
                    ImageProfilo.setColorFilter(rosa);
                }

                //Inserimento statistiche totali
                VisualizzaStatistiche();
            }
        });

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private void graficogenerate()
    {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());

        Log.d("data", formatter.format(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Log.d("data", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        Log.d("dataGiorno", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        calendar.add(Calendar.DATE, -7);

        SimpleDateFormat formattergiornosettimana = new SimpleDateFormat("EEEE");
        Log.d("dataoggi", String.valueOf(formattergiornosettimana.format(date)));
        String giornoSettimanaOggi = formattergiornosettimana.format(date);
        Log.d("data7giornifa", String.valueOf(formatter.format(calendar.getTime())));

        String datacorrente;
        String data7giorni;
        double KmTotLunedi    = 0;
        double KmTotMartedi   = 0;
        double KmTotMercoledi = 0;
        double KmTotGiovedi   = 0;
        double KmTotVenerdi   = 0;
        double KmTotSabato    = 0;
        double KmTotDomenica  = 0;

        datacorrente = formatter.format(date);
        data7giorni = formatter.format(calendar.getTime());

        ArrayList<Double> Km = new ArrayList<>();
        Km.clear();
        Km = sessione.getKm_Percorsi();
        ArrayList<HashMap<String,String>> Data = sessione.getData();
        Double km_effettuati   = 0.0;
        Double km_effettuati_0 = 0.0;

        for(int i = 0; i < sessione.getData().size(); i++)
        {
            String DateSessioni  = null;

            String stringmese = String.valueOf(Data.get(i).get("monthValue"));
            int mese = Integer.parseInt(stringmese);

            String stringgiorno = String.valueOf(Data.get(i).get("dayOfMonth"));
            int giorno = Integer.parseInt(stringgiorno);

            String giornosettimana = Data.get(i).get("dayOfWeek");

            if(mese < 10)
                DateSessioni = String.valueOf(Data.get(i).get("year")) + "-0" + String.valueOf(Data.get(i).get("monthValue")) + "-" +  String.valueOf(Data.get(i).get("dayOfMonth"));
            if(giorno < 10)
                DateSessioni = String.valueOf(Data.get(i).get("year")) + "-" + String.valueOf(Data.get(i).get("monthValue")) + "-0" +  String.valueOf(Data.get(i).get("dayOfMonth"));
            if(mese < 10 && giorno < 10)
                DateSessioni = String.valueOf(Data.get(i).get("year")) + "-0" + String.valueOf(Data.get(i).get("monthValue")) + "-0" +  String.valueOf(Data.get(i).get("dayOfMonth"));
            if(mese > 10 && giorno > 10)
                DateSessioni = String.valueOf(Data.get(i).get("year")) + "-" + String.valueOf(Data.get(i).get("monthValue")) + "-" +  String.valueOf(Data.get(i).get("dayOfMonth"));

            if(data7giorni.compareTo(DateSessioni) < 0 && datacorrente.compareTo(DateSessioni) >= 0)
            {
                Log.d("datamap1", DateSessioni);
                Log.d("giorno", giornosettimana + " " + Km);

                km_effettuati = Km.get(i);

                if(giornosettimana.equals("MONDAY"))
                    KmTotLunedi = KmTotLunedi + km_effettuati;
                if(giornosettimana.equals("TUESDAY"))
                    KmTotMartedi = KmTotMartedi + km_effettuati;
                if(giornosettimana.equals("WEDNESDAY"))
                    KmTotMercoledi = KmTotMercoledi + km_effettuati;
                if(giornosettimana.equals("THURSDAY"))
                    KmTotGiovedi = KmTotGiovedi + km_effettuati;
                if(giornosettimana.equals("FRIDAY"))
                    KmTotVenerdi = KmTotVenerdi + km_effettuati;
                if(giornosettimana.equals("SATURDAY"))
                    KmTotSabato = KmTotSabato + km_effettuati;
                if(giornosettimana.equals("SUNDAY"))
                    KmTotDomenica = KmTotDomenica + km_effettuati;
            }
        }

        barChart.setVisibility(View.VISIBLE);

        ArrayList<BarEntry> sessioni = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Log.d("giornosettimana", giornoSettimanaOggi);
        int oggi = calendar.get(Calendar.DAY_OF_WEEK);

        switch (oggi)
        {
            case Calendar.MONDAY:

                String[] giornisettimana = {"Mar", "Mer", "Gio", "Ven", "Sab", "Dom", "Oggi"};
                Double[] KmGiorni = {KmTotMartedi, KmTotMercoledi, KmTotGiovedi, KmTotVenerdi, KmTotSabato, KmTotDomenica, KmTotLunedi};

                for(int i = 0; i < 7; i++)
                {
                    labels.add(giornisettimana[i]);
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni[i]))));
                }
                break;

            case Calendar.TUESDAY:

                Log.d("grafico", "entrato");
                String[] giornisettimana1 = { "Mer", "Gio", "Ven", "Sab", "Dom", "Lun", "Oggi"};
                Double[] KmGiorni1 = {KmTotMercoledi, KmTotGiovedi, KmTotVenerdi, KmTotSabato, KmTotDomenica, KmTotLunedi, KmTotMartedi};

                for(int i = 0; i < 7; i++)
                {
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni1[i]))));
                    labels.add(giornisettimana1[i]);
                }
                break;

            case Calendar.WEDNESDAY:

                Log.d("grafico", "entrato");
                String[] giornisettimana2 = { "Gio", "Ven", "Sab", "Dom", "Lun",  "Mar", "Oggi"};
                Double[] KmGiorni2 = {KmTotGiovedi, KmTotVenerdi, KmTotSabato, KmTotDomenica, KmTotLunedi, KmTotMartedi, KmTotMercoledi};

                for(int i = 0; i < 7; i++)
                {
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni2[i]))));
                    labels.add(giornisettimana2[i]);
                }
                break;

            case Calendar.THURSDAY:

                String[] giornisettimana3 = {"Ven", "Sab", "Dom", "Lun",  "Mar", "Mer", "Oggi"};
                Double[] KmGiorni3 = {KmTotVenerdi, KmTotSabato, KmTotDomenica, KmTotLunedi, KmTotMartedi, KmTotMercoledi, KmTotGiovedi};

                for(int i = 0; i < 7; i++)
                {
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni3[i]))));
                    labels.add(giornisettimana3[i]);
                }
                break;

            case Calendar.FRIDAY:

                String[] giornisettimana4 = {"Sab", "Dom", "Lun",  "Mar", "Mer", "Gio", "Oggi"};
                Double[] KmGiorni4 = {KmTotSabato, KmTotDomenica, KmTotLunedi, KmTotMartedi, KmTotMercoledi, KmTotGiovedi, KmTotVenerdi};

                for(int i = 0; i < 7; i++)
                {
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni4[i]))));
                    labels.add(giornisettimana4[i]);
                }
                break;

            case Calendar.SATURDAY:

                String[] giornisettimana5 = {"Dom", "Lun",  "Mar", "Mer", "Gio", "Ven", "Oggi"};
                Double[] KmGiorni5 = { KmTotDomenica, KmTotLunedi, KmTotMartedi, KmTotMercoledi, KmTotGiovedi, KmTotVenerdi, KmTotSabato};

                for(int i = 0; i < 7; i++)
                {
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni5[i]))));
                    labels.add(giornisettimana5[i]);
                }
                break;

            case Calendar.SUNDAY:

                String[] giornisettimana6 = {"Lun",  "Mar", "Mer", "Gio", "Ven", "Sab", "Oggi"};
                Double[] KmGiorni6 = {KmTotLunedi, KmTotMartedi, KmTotMercoledi, KmTotGiovedi, KmTotVenerdi, KmTotSabato, KmTotDomenica};

                for(int i = 0; i < 7; i++)
                {
                    sessioni.add(new BarEntry(Float.parseFloat(String.valueOf(i)), Float.parseFloat(String.valueOf(KmGiorni6[i]))));
                    labels.add(giornisettimana6[i]);
                }
                break;
        }

        BarDataSet barDataSet = new BarDataSet(sessioni, "Km");
        barDataSet.setColor(Color.parseColor("#0B4F6C"));


        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        BarData data = new BarData(barDataSet);

        barChart.setData(data);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);
        barDataSet.setColors(Color.parseColor("#0B4F6C"));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(13f);
        barChart.getDescription().setText("Riepilogo Km settimanali");
        barChart.getDescription().setTextSize(15f);
        barChart.animateY(2000);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.upbarprofile_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.edit_button:
            {
                openEditDialog();
                return true;
            }
            case R.id.logout_button:
            {
                utente.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void openEditDialog()
    {
        EditDialogClass editDialogClass = new EditDialogClass();
        editDialogClass.show(myContext.getSupportFragmentManager(), "esempio" );
    }

    private void VisualizzaStatistiche()
    {
        ArrayList<Double> statistiche = new ArrayList<>();
        statistiche = sessione.StatisticheTotali();

        Log.d("Statistiche1"," " + statistiche);

        DecimalFormat df = new DecimalFormat("##########.###");
        df.setRoundingMode(RoundingMode.DOWN);
        if(statistiche.get(0) == 0.0){
            DescrizioneStatistiche[0] = "0.0 Km";
        }
        else{
            DescrizioneStatistiche[0] = String.valueOf(df.format(statistiche.get(0)) + " Km");
        }

        if(statistiche.get(1) == 0.0){
            DescrizioneStatistiche[1] = "0";
        }
        else{
            DescrizioneStatistiche[1] = String.valueOf(df.format(statistiche.get(1)));
        }

        if(statistiche.get(2) == 0.0){
            DescrizioneStatistiche[2] = "0 Kcal";
        }
        else{
            DescrizioneStatistiche[2] = String.valueOf(df.format(statistiche.get(2)) + " Kcal");
        }

        if(statistiche.get(3) == 0.0){
            DescrizioneStatistiche[3] = "0 h";
        }
        else
        {
            if(statistiche.get(3)/3600 < 1)
                DescrizioneStatistiche[3] = "0 h";
            else
                DescrizioneStatistiche[3] = String.valueOf(df.format((int) (statistiche.get(3) / 3600)) + " h");
        }

        StatisticheGare();
        graficogenerate();
    }

    private void StatisticheGare()
    {
        Gara gara = new Gara();
        mAuth = FirebaseAuth.getInstance();
        DescrizioneStatistiche[4] = "0";
        DescrizioneStatistiche[5] = "0";

        MyAdapter myAdapter = new MyAdapter(getContext(), TitoliStatistiche, DescrizioneStatistiche);
        recyclerViewStatistiche.setAdapter(myAdapter);
        recyclerViewStatistiche.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        gara.getGareUtenteDatabase(new FirestoreCallback()
        {
            @Override
            public void onPullGareCallback()
            {
                numGare = gara.getNome().size();
                StatisticheGareVinte();
                Log.d("ppp", String.valueOf(garaNonEsistente));
            }
        });

    }

    private void StatisticheGareVinte()
    {
        numGareVinte = 0;
        db.collection("Gara")
                .whereEqualTo("UsernameVincitore", main2Activity.Utente.get("Username"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            garaNonEsistente = false;

                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                Log.d("Statistica", "trovata una gara come partecipante");
                                numGareVinte++;
                            }

                            Log.d("Statistica", "numero di gare vinte" + numGareVinte);

                            DescrizioneStatistiche[4] = String.valueOf(numGare);
                            DescrizioneStatistiche[5] = String.valueOf(numGareVinte);

                            MyAdapter myAdapter = new MyAdapter(getContext(), TitoliStatistiche, DescrizioneStatistiche);
                            recyclerViewStatistiche.setAdapter(myAdapter);
                            recyclerViewStatistiche.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        }
                        else
                        {
                            Log.d("database", "Error getting documents: ", task.getException());
                        }
                    }
                });
        Log.d("ciao", String.valueOf(garaNonEsistente));
        if(garaNonEsistente == true){
            Log.d("C pall", String.valueOf(garaNonEsistente));
            DescrizioneStatistiche[4] = "0";
            DescrizioneStatistiche[5] = "0";

            MyAdapter myAdapter = new MyAdapter(getContext(), TitoliStatistiche, DescrizioneStatistiche);
            recyclerViewStatistiche.setAdapter(myAdapter);
            recyclerViewStatistiche.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}