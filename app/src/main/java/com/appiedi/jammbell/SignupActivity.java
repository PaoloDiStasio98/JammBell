package com.appiedi.jammbell;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appiedi.jammbell.View.ISignup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity implements ISignup {

    private FirebaseAuth mAuth;
    private EditText MailEditText, PasswordEditText;
    private Button SignupButton;
    private TextView ErroreSignUpTextView;

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
                if(MailEditText.getText().toString().matches("") || PasswordEditText.getText().toString().matches("")) {
                    ErroreSignUpTextView.setVisibility(View.VISIBLE);
                    ErroreSignUpTextView.setText("Inserisci tutti i dati");
                }
                else if(isEmailValid(MailEditText.getText().toString()) == false){
                        ErroreSignUpTextView.setVisibility(View.VISIBLE);
                        ErroreSignUpTextView.setText("Controlla di aver inserito una mail corretta");
                    }
             else if (PasswordEditText.getText().length() < 6) {
                    ErroreSignUpTextView.setVisibility(View.VISIBLE);
                    ErroreSignUpTextView.setText("La password deve contenere più di 6 caratteri");
                }

                else if(isValidPassword(PasswordEditText.getText().toString()) == false){
                    ErroreSignUpTextView.setVisibility(View.VISIBLE);
                    ErroreSignUpTextView.setText("Controlla che la password abbia almeno una lettera minuscola, una lettera maiuscola" +
                            "e un numero");
                }

                else {
                    String mail = MailEditText.getText().toString().toLowerCase();
                    String password = PasswordEditText.getText().toString();
                    idTransfer(mail, password);
                }


                }

        });


    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void idTransfer(String userMail, String userPassword) {
        Intent intent = new Intent(getBaseContext(), RegistrazioneProfiloActivity.class);
        intent.putExtra("USER_MAIL", userMail);
        intent.putExtra("USER_PASSWORD", userPassword);
        startActivity(intent);
    }
}