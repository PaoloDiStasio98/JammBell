package com.example.jammbell;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jammbell.Model.FirestoreCallback;
import com.example.jammbell.Model.Gara;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Challenge;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class ChallengeFragment extends Fragment implements CreateGameDialogClass.OnGameCreatedListener {


    private static final String TAG = "ChallengeFragment";

    private FragmentActivity myContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<String> ChallengeDataInizio           = new ArrayList<String>();
    private ArrayList<String> ChallengeDataFine             = new ArrayList<String>();
    private ArrayList<String> ChallengeNome                 = new ArrayList<String>();
    private ArrayList<String> ChallengeUsernamePartecipante = new ArrayList<String>();
    private ArrayList<String> ChallengeUsernameCreatore = new ArrayList<String>();
    private ArrayList<String> ChallengeStato = new ArrayList<String>();
    private ArrayList<String> ChallengeDocumento = new ArrayList<String>();
    private ArrayList<String> ChallengeRisultato = new ArrayList<String>();
    private ArrayList<String> ChallengeUsernameVincitore = new ArrayList<String>();

    RecyclerView recyclerViewChallenge;
    TextView filtroTextView;

    @Override
    public void getUsername(String Datafine, String Datainizio, String IDcreatore, String Nome, String Stato, String UsernameCreatore, String UsernamePartecipante)
    {
        PullGare();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        recyclerViewChallenge = getView().findViewById(R.id.recyclerViewChallenge);

        setHasOptionsMenu(true);

        PullGare();

        filtroTextView = getView().findViewById(R.id.FiltroTextView);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.upbarchallenge_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu)
    {
        menu.clear();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.upbarchallenge_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.create_button:
            {
                CreateGameDialog();
                return true;
            }
            case R.id.refresh_button:
            {
                PullGare();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    public void CreateGameDialog()
    {
        CreateGameDialogClass createGameDialogClass = new CreateGameDialogClass();
        createGameDialogClass.show(getFragmentManager(), "creategame");
        createGameDialogClass.setTargetFragment(ChallengeFragment.this, 1);
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_challenge, container, false);
    }


    public void clearCell()
    {
        ChallengeDataInizio.clear();
        ChallengeDataFine.clear();
        ChallengeDocumento.clear();
        ChallengeNome.clear();
        ChallengeStato.clear();
        ChallengeUsernameCreatore.clear();
        ChallengeUsernamePartecipante.clear();
        ChallengeRisultato.clear();
        ChallengeUsernameVincitore.clear();
    }

    public void PullGare()
    {
        clearCell();

        Gara gara = new Gara();

        gara.getGareUtenteDatabase(new FirestoreCallback()
        {
            @Override
            public void onPullGareCallback()
            {
                ChallengeDataInizio           = gara.getData_inizio();
                ChallengeDataFine             = gara.getData_fine();
                ChallengeNome                 = gara.getNome();
                ChallengeUsernamePartecipante = gara.getUsername_partecipante();
                ChallengeUsernameCreatore     = gara.getUsername_creatore();
                ChallengeStato                = gara.getStato();
                ChallengeDocumento            = gara.getIDDocumento();
                ChallengeRisultato            = gara.getRisultato();
                ChallengeUsernameVincitore    = gara.getUsername_vincitore();

                Log.d("IDUTENTE", String.valueOf(ChallengeStato));
                MyAdapterChallenge myAdapter = new MyAdapterChallenge(getContext(), ChallengeDataInizio, ChallengeDataFine, ChallengeNome, ChallengeUsernamePartecipante, ChallengeStato, ChallengeUsernameCreatore, ChallengeDocumento, ChallengeRisultato, ChallengeUsernameVincitore);
                recyclerViewChallenge.setAdapter(myAdapter);
                recyclerViewChallenge.setLayoutManager(new LinearLayoutManager(getContext()));

                if(ChallengeDataInizio.size() == 0)
                    filtroTextView.setText("Nessuna gara");

            }
        });
    }
}