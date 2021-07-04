package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText MailEditText, PasswordEditText;
    Button SignupButton;
    TextView ErroreSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupp_view);

        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        MailEditText = (EditText)findViewById(R.id.EditTextMailSignUp);
        PasswordEditText = (EditText)findViewById(R.id.EditTextPasswordSignUp);
        SignupButton = (Button)findViewById(R.id.ButtonSignUp);
        ErroreSignUpTextView = (TextView) findViewById(R.id.ErroreSignUp);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MailEditText.getText().toString().matches("") || PasswordEditText.getText().toString().matches("")){
                    ErroreSignUpTextView.setVisibility(View.VISIBLE);
                    ErroreSignUpTextView.setText("Inserisci tutti i dati");
            } else {

                    String mail = MailEditText.getText().toString();
                    String password = PasswordEditText.getText().toString();
                    idTransfer(mail, password);
                }
            }
        });


    }
    public void idTransfer(String userMail, String userPassword) {
        Intent intent = new Intent(getBaseContext(), RegistrazioneProfiloActivity.class);
        intent.putExtra("USER_MAIL", userMail);
        intent.putExtra("USER_PASSWORD", userPassword);
        startActivity(intent);
    }
}