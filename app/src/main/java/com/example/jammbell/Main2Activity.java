package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.jammbell.Model.Utente;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {


    public String PosizioneCorrente;
    public static Map<String, Object> Utente = new HashMap<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    FragmentManager fManager = getFragmentManager();
    FragmentTransaction fTransaction = fManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        pullUtente();
        PosizioneCorrente = getIntent().getStringExtra("USER_ID");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(Main2Activity.this, R.id.fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.challengeFragment, R.id.mapFragment, R.id.activityFragment, R.id.profileFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }


    public void pullUtente()
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    Utente.put("Altezza", document.get("Altezza"));
                                    Utente.put("Cognome", document.get("Cognome"));
                                    Utente.put("Nome", document.get("Nome"));
                                    Utente.put("Data di nascita", document.get("Data di nascita"));
                                    Utente.put("ID", document.get("IDUtente"));
                                    Utente.put("Peso", document.get("Peso"));
                                    Utente.put("Sesso", document.get("Sesso"));
                                    Utente.put("Username", document.get("Username"));
                                }
                                    Log.d("challengemain", String.valueOf(Utente));
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