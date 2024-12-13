package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String FIRST_RUN_KEY = "firstRun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si cette activité est appelée après un logout
        Intent intent = getIntent();
        boolean fromLogout = intent.getBooleanExtra("fromLogout", false);

        // Si ce n'est pas un logout, vérifier le premier lancement
        if (!fromLogout) {
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isFirstRun = preferences.getBoolean(FIRST_RUN_KEY, true);

            if (!isFirstRun) {
                // Rediriger vers LoginActivity si ce n'est pas le premier lancement
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish(); // Fermer MainActivity
                return;
            }

            // Mettre à jour le flag pour indiquer que l'application a été lancée
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRST_RUN_KEY, false);
            editor.apply();
        }

        // Afficher le layout de MainActivity
        setContentView(R.layout.activity_main);

        // Initialiser les boutons
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        Button btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(v -> {
            Intent signupIntent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(signupIntent);
        });
    }
}
