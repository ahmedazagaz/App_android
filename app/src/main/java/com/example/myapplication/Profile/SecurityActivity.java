package com.example.myapplication.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SecurityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        ImageView backIcon = findViewById(R.id.back_icon); // ID défini dans le fichier XML

        // Action sur le bouton "Retour"
        backIcon.setOnClickListener(view -> {
            Intent intent = new Intent(SecurityActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // Facultatif : ferme l'activité actuelle
        });

    }
}
