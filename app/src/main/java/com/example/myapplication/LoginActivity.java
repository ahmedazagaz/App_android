package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull; // Import ajouté ici
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Configurer les marges pour éviter le recouvrement par les barres de statut et de navigation
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialiser FirebaseAuth et ProgressDialog
        mAuth = FirebaseAuth.getInstance();
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
                // Récupérer l'email et le mot de passe des champs de texte
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                // Vérifier si les champs email et mot de passe sont remplis
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password is required");
                    return;
                }

                // Afficher le ProgressDialog pendant la connexion
                mDialog.setMessage("Logging in...");
                mDialog.show();

                // Utiliser FirebaseAuth pour connecter l'utilisateur
                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mDialog.dismiss(); // Cacher le ProgressDialog

                        if (task.isSuccessful()) {
                            // Si la connexion est réussie, afficher un message et démarrer HomeActivity
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish(); // Terminer l'activité de connexion
                        } else {
                            // En cas d'échec, afficher un message d'erreur
                            Toast.makeText(getApplicationContext(), "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Lien vers l'écran d'inscription si l'utilisateur n'a pas de compte
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
