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

                    mAuth.createUserWithEmailAndPassword(MailEditText.getText().toString(), PasswordEditText.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Log.d("id", user.getUid());
                                        idTransfer(user);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        ErroreSignUpTextView.setVisibility(View.VISIBLE);
                                        ErroreSignUpTextView.setText("Registrazione fallita, controlla di aver inserito una mail valida e che la " +
                                                "password abbia almeno 6 caratteri tra cui un numero");
                                    }
                                }
                            });
                }
            }
        });


    }
    public void idTransfer(FirebaseUser user) {
        Intent intent = new Intent(getBaseContext(), RegistrazioneProfiloActivity.class);
        intent.putExtra("USER_ID", user.getUid());
        startActivity(intent);
    }
}