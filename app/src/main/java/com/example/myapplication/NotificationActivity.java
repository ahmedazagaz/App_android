package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.Profile.ProfileActivity;
import com.example.myapplication.Profile.SecurityActivity;
import com.example.myapplication.R;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ImageView backIcon = findViewById(R.id.back_icon); // ID défini dans le fichier XML

        // Action sur le bouton "Retour"
        backIcon.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Facultatif : ferme l'activité actuelle
        });
    }
}
