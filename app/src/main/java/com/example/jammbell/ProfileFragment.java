package com.example.jammbell;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {


    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        Button logoutButton = (Button) getView().findViewById(R.id.logoutButton);
        super.onViewCreated(view, savedInstanceState);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {






        return inflater.inflate(R.layout.fragment_profile, container, false);



    }


}