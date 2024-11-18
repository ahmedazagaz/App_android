package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrationActivity extends AppCompatActivity {

    // Déclaration des éléments d'interface utilisateur

    private EditText mFullName;
    private EditText mEmail;
    private EditText mPass;
    private Button btnSignup;
    private TextView mSignin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        registration();
    }

    // Méthode pour configurer l'interface utilisateur et les actions d'inscription

    private void registration() {

        mFullName = findViewById(R.id.FullName_signup);
        mEmail = findViewById(R.id.Email_signup);
        mPass = findViewById(R.id.Password_signup);
        btnSignup = findViewById(R.id.btn_login);
        mSignin = findViewById(R.id.signin_here);

        // Définir l'action du bouton d'inscription

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Récupérer les valeurs des champs de texte
                String FullName = mFullName.getText().toString();
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                // Vérifier si les champs est vide et afficher un message d'erreur

                if (TextUtils.isEmpty((FullName))) {
                    mFullName.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password is required");
                    return;
                }


            }
        });

        // Définir l'action du lien "Sign In"
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

}