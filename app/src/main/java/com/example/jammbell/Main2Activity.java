package com.example.jammbell;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


       BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
       NavController navController = Navigation.findNavController(Main2Activity.this, R.id.fragment);
       NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}