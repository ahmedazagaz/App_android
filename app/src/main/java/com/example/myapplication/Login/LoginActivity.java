package com.example.myapplication.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.myapplication.Registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPass;
    private Button btnLogin;
    private TextView mForgotPassword;
    private TextView mSignup;

    private FirebaseAuth mAuth;       // Objet FirebaseAuth pour gérer l'authentification
    private ProgressDialog mDialog;   // ProgressDialog pour afficher un message de chargement

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si un utilisateur est déjà connecté
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish(); // Fermer LoginActivity
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialiser FirebaseAuth et ProgressDialog
        mDialog = new ProgressDialog(this);

        // Initialiser les composants de connexion
        initializeLoginComponents();
    }

    private void initializeLoginComponents() {
        // Liaison des éléments de l'interface utilisateur
        mEmail = findViewById(R.id.email_login);
        mPass = findViewById(R.id.password_login);
        btnLogin = findViewById(R.id.btn_login);
        mForgotPassword = findViewById(R.id.forget_password_link);
        mSignup = findViewById(R.id.signup_link);

        // Définir l'action du bouton de connexion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password is required");
                    return;
                }

                mDialog.setMessage("Logging in...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Redirection vers l'écran d'inscription
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        // Lien pour le mot de passe oublié
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReseatActivity.class));
            }
        });
    }
}
