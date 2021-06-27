package com.example.jammbell;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class ProfileFragment extends Fragment {



    private FragmentActivity myContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    TextView ciaoNomeeCognomeTextView;
    ImageView ImageProfilo;
    TextView usernameTextView;
    TextView datadinascitaTextView;
    TextView pesoTextView;
    TextView altezzaTextView;

    double KmTot = 0;
    long PassiTot = 0;
    long CalorieTot = 0;
    long TempoTot = 0;
    double KmTotLunedi = 0;
    double KmTotMartedi = 0;
    double KmTotMercoledi = 0;
    double KmTotGiovedi = 0;
    double KmTotVenerdi = 0;
    double KmTotSabato = 0;
    double KmTotDomenica = 0;

    HashMap<String, String> Datamap1 = new HashMap<>();

    String TitoliStatistiche[] = {"Km percorsi", "Passi", "Calorie", "Ore totali"};
    String DescrizioneStatistiche[] = {"0", "0", "0", "0"};

    RecyclerView recyclerViewStatistiche;

    BarChart barChart;

    String datacorrente;
    String data7giorni;
    String DateSessioni;



    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        ciaoNomeeCognomeTextView = (TextView) getView().findViewById(R.id.CiaoNomeCognomeText);
        usernameTextView = (TextView) getView().findViewById(R.id.UsernameTextView);
        datadinascitaTextView = (TextView) getView().findViewById(R.id.DataTextView);
        pesoTextView = (TextView) getView().findViewById(R.id.PesoProfiloTextView);
        altezzaTextView = (TextView) getView().findViewById(R.id.AltezzaProfiloTextView);
        ImageProfilo = (ImageView) getView().findViewById(R.id.Imageprofilo);



        setHasOptionsMenu(true);

        recyclerViewStatistiche = (RecyclerView) getView().findViewById(R.id.recyclerViewStatistiche);

        PullDatiDatabase();

        barChart = getView().findViewById(R.id.graficoSettimanale);

        super.onViewCreated(view, savedInstanceState);

    }

    public void graficogenerate(){

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());

        Log.d("data", formatter.format(date));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Log.d("data", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DATE, -7);
        Log.d("dataoggi", String.valueOf(formatter.format(date)));
        Log.d("data7giornifa", String.valueOf(formatter.format(calendar.getTime())));

        datacorrente = formatter.format(date);
        data7giorni = formatter.format(calendar.getTime());

            mAuth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser != null){
                Log.d("utenteid", currentUser.getUid());

                db.collection("SessioneVeloce")
                        .whereEqualTo("UserID", currentUser.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        Double Km = (Double) document.get("Km");
                                        Datamap1 = (HashMap<String, String>) document.get("Data");

                                        Log.d("datamap", String.valueOf(Datamap1));

                                        Log.d("datamapint", String.valueOf(Datamap1.get("monthValue")));

                                        String stringmese = String.valueOf(Datamap1.get("monthValue"));
                                        int mese = Integer.parseInt(stringmese);

                                        String stringgiorno = String.valueOf(Datamap1.get("dayOfMonth"));
                                        int giorno = Integer.parseInt(stringgiorno);

                                        String giornosettimana = Datamap1.get("dayOfWeek");

                                        if(mese < 10)
                                           DateSessioni = String.valueOf(Datamap1.get("year")) + "-0" + String.valueOf(Datamap1.get("monthValue")) + "-" +  String.valueOf(Datamap1.get("dayOfMonth"));
                                        if(giorno < 10)
                                            DateSessioni = String.valueOf(Datamap1.get("year")) + "-" + String.valueOf(Datamap1.get("monthValue")) + "-0" +  String.valueOf(Datamap1.get("dayOfMonth"));
                                        if(mese < 10 && giorno < 10)
                                            DateSessioni = String.valueOf(Datamap1.get("year")) + "-0" + String.valueOf(Datamap1.get("monthValue")) + "-0" +  String.valueOf(Datamap1.get("dayOfMonth"));

                                        if(data7giorni.compareTo(DateSessioni) < 0 && datacorrente.compareTo(DateSessioni) >= 0)
                                        {
                                            Log.d("datamap1", DateSessioni);
                                            Log.d("giorno", giornosettimana + " " + Km);

                                            if(giornosettimana.equals("MONDAY"))
                                                KmTotLunedi = KmTotLunedi + Km;
                                            if(giornosettimana.equals("TUESDAY"))
                                                KmTotMartedi = KmTotMartedi + Km;
                                            if(giornosettimana.equals("WEDNESDAY"))
                                                KmTotMercoledi = KmTotMercoledi + Km;
                                            if(giornosettimana.equals("THURSDAY"))
                                                KmTotGiovedi = KmTotGiovedi + Km;
                                            if(giornosettimana.equals("FRIDAY"))
                                                KmTotVenerdi = KmTotVenerdi + Km;
                                            if(giornosettimana.equals("SATURDAY"))
                                                KmTotSabato = KmTotSabato + Km;
                                            if(giornosettimana.equals("SUNDAY"))
                                                KmTotDomenica = KmTotDomenica + Km;
                                        }
                                    }

                                    barChart.setVisibility(View.VISIBLE);

                                    ArrayList<BarEntry> sessioni = new ArrayList<>();

                                    sessioni.add(new BarEntry(0f,Float.parseFloat(String.valueOf(KmTotLunedi))));
                                    sessioni.add(new BarEntry(1f,Float.parseFloat(String.valueOf(KmTotMartedi))));
                                    sessioni.add(new BarEntry(2f,Float.parseFloat(String.valueOf(KmTotMercoledi))));
                                    sessioni.add(new BarEntry(3f,Float.parseFloat(String.valueOf(KmTotGiovedi))));
                                    sessioni.add(new BarEntry(4f,Float.parseFloat(String.valueOf(KmTotVenerdi))));
                                    sessioni.add(new BarEntry(5f,Float.parseFloat(String.valueOf(KmTotSabato))));
                                    sessioni.add(new BarEntry(6f,Float.parseFloat(String.valueOf(KmTotDomenica))));
                                    BarDataSet barDataSet = new BarDataSet(sessioni, "Km");

                                    ArrayList<String> labels = new ArrayList<>();
                                    labels.add("Lun");
                                    labels.add("Mar");
                                    labels.add("Mer");
                                    labels.add("Gio");
                                    labels.add("Ven");
                                    labels.add("Sab");
                                    labels.add("Dom");

                                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                                    BarData data = new BarData(barDataSet);
                                    barChart.setData(data);
                                    barChart.getAxisLeft().setDrawGridLines(false);
                                    barChart.getAxisRight().setDrawGridLines(false);
                                    barChart.getXAxis().setDrawGridLines(false);
                                    barChart.setScaleEnabled(false);
                                    barDataSet.setColors(Color.CYAN);
                                    barDataSet.setValueTextColor(Color.BLACK);
                                    barDataSet.setValueTextSize(14f);
                                    barChart.getDescription().setText("Riepilogo Km settimanali");
                                    barChart.getDescription().setTextSize(15f);
                                    barChart.animateY(2000);
                                }

                                else
                                {
                                    Log.d("database", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
            else {
                Log.d("utenteid", "niente vuoto");
            }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull  MenuInflater inflater) {
        inflater.inflate(R.menu.upbarprofile_menu, menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_button: {

               openEditDialog();
                return true;
            }
            case R.id.logout_button: {
                logout();

                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void openEditDialog() {
        EditDialogClass editDialogClass = new EditDialogClass();
        editDialogClass.show(myContext.getSupportFragmentManager(), "esempio" );
    }

    public void PullDatiDatabase() {



       mAuth = FirebaseAuth.getInstance();


       FirebaseUser currentUser = mAuth.getCurrentUser();
       if(currentUser != null){
           Log.d("utenteid", currentUser.getUid());

           db.collection("SessioneVeloce")
                   .whereEqualTo("UserID", currentUser.getUid())
                   .get()
                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               for (QueryDocumentSnapshot document : task.getResult()) {

                                   //Statistiche Totali
                                   KmTot = KmTot + (double) document.get("Km");
                                   PassiTot = PassiTot + (long) document.get("Passi");
                                   CalorieTot = CalorieTot + (long) document.get("Calorie");
                                   TempoTot = TempoTot + (long) document.get("Tempo");


                               }

                               DecimalFormat df = new DecimalFormat("##########.###");
                               df.setRoundingMode(RoundingMode.DOWN);
                               DescrizioneStatistiche[0] = String.valueOf(df.format(KmTot) + " Km");
                               DescrizioneStatistiche[1] = String.valueOf(PassiTot);
                               DescrizioneStatistiche[2] = String.valueOf(CalorieTot + " Kcal");
                               DescrizioneStatistiche[3] = String.valueOf(TempoTot/3600 + " h");

                               MyAdapter myAdapter = new MyAdapter(getContext(), TitoliStatistiche, DescrizioneStatistiche);
                               recyclerViewStatistiche.setAdapter(myAdapter);
                               recyclerViewStatistiche.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


                                   graficogenerate();


                           }

                           else
                               {
                               Log.d("database", "Error getting documents: ", task.getException());
                           }






                       }
                   });

           Log.d("descrizione", DescrizioneStatistiche[1]);


       }
       else {
           Log.d("utenteid", "niente vuoto");
       }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("utenteid", currentUser.getUid());

            db.collection("Utente")
                    .whereEqualTo("IDUtente", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    usernameTextView.setText("Username: " + document.get("Username").toString());
                                    ciaoNomeeCognomeTextView.setText("Ciao, " + document.get("Nome").toString() + " " + document.get("Cognome").toString());
                                    datadinascitaTextView.setText("Data di nascita: " + document.get("Data di nascita").toString());
                                    pesoTextView.setText("Peso: " + document.get("Peso").toString());
                                    altezzaTextView.setText("Altezza: " + document.get("Altezza").toString());

                                    if(document.get("Sesso").toString().equals("Maschio")){
                                        int blu = Color.parseColor("#1e90ff");
                                        ImageProfilo.setColorFilter(blu);
                                    }
                                    if(document.get("Sesso").toString().equals("Femmina")){
                                        int rosa = Color.parseColor("#ef35fc");
                                        ImageProfilo.setColorFilter(rosa);
                                    }


                                    Log.d("database", document.getId() + " => " + document.getData() + " "  + " " + document.get("Altezza"));
                                }
                            } else {
                                Log.d("database", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            Log.d("utenteid", "niente vuoto");
        }




        return inflater.inflate(R.layout.fragment_profile, container, false);



    }




}