package com.appiedi.jammbell;

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

import com.appiedi.jammbell.Model.FirestoreCallback;
import com.appiedi.jammbell.Model.Utente;
import com.appiedi.jammbell.View.ILogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements ILogin {

    private FirebaseAuth mAuth;
    private EditText MailEditText=null, PasswordEditText=null;
    private Button LoginButton;
    private TextView SignupButton;
    private TextView ErroreTextView;
    private FirebaseUser currentUser;


    @Override
    public void onBackPressed() {
        return;
    }


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
                    Utente utente = new Utente();
                    String Email = MailEditText.getText().toString();
                    String Password = PasswordEditText.getText().toString();
                    utente.logIn(Email, Password, new FirestoreCallback(){
                        @Override
                        public void onLoginCallback(Boolean successo) {

                            if(successo == true){

                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.isEmailVerified() == true)
                                    startActivity(new Intent(LoginActivity.this, Main2Activity.class));
                                else{
                                    ErroreTextView.setVisibility(View.VISIBLE);
                                    ErroreTextView.setText("Controlla di aver verificato la mail di registrazione");
                                }
                            }
                            else {
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
        currentUser = mAuth.getCurrentUser();

        // Check if user is signed in (non-null) and update UI accordingly.
        if(currentUser != null) {
            if (currentUser.isEmailVerified() == true) {
                Log.d("utente", currentUser.getEmail());

                startActivity(new Intent(LoginActivity.this, Main2Activity.class));
            } else {
                Log.d("utente", currentUser.getEmail());
                ErroreTextView.setVisibility(View.VISIBLE);
                ErroreTextView.setText("Controlla di aver verificato la mail di registrazione");
            }
        }
    }

    }



