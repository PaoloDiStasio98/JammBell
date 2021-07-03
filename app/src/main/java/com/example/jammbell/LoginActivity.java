package com.example.jammbell;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText MailEditText=null, PasswordEditText=null;
    Button LoginButton;
    TextView SignupButton;
    TextView ErroreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);


        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        MailEditText = (EditText)findViewById(R.id.EditTextMail);
        PasswordEditText = (EditText)findViewById(R.id.EditTextPassword);
        LoginButton = (Button)findViewById(R.id.ButtonLogin);
        SignupButton = (TextView) findViewById(R.id.ButtonRegistrazione);

        ErroreTextView = findViewById(R.id.ErroreLogin);

        SpannableString TextRegistrazione = new SpannableString("Oppure registrati cliccando qui");
        TextRegistrazione.setSpan(new UnderlineSpan(), 28, 31, 0);
        SignupButton.setText(TextRegistrazione);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MailEditText.getText().toString().matches("") || PasswordEditText.getText().toString().matches("")) {
                    ErroreTextView.setVisibility(View.VISIBLE);
                    ErroreTextView.setText("Inserisci tutti i campi per effettuare il login");

                }
                else {
                    Log.d("accesso", "dentroooooo:success");

                    mAuth.signInWithEmailAndPassword(MailEditText.getText().toString(), PasswordEditText.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(LoginActivity.this, Main2Activity.class));
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        ErroreTextView.setVisibility(View.VISIBLE);
                                        ErroreTextView.setText("Autenticazione fallita, ricontrolla mail e/o password");
                                    }
                                }
                            });
                }

            }
        });

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));

            }
        });



    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("utente", currentUser.getEmail());

            startActivity(new Intent(LoginActivity.this, Main2Activity.class));
        }
    }

    }



