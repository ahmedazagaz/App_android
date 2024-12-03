package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.category.CategoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Configuration de la BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            // Utilisation de if-else pour gérer la navigation
            if (item.getItemId() == R.id.homes) {
                // Action pour Home
                // Par exemple, on reste sur cette activité
                Intent searchIntent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(searchIntent);
                return true;
            } else if (item.getItemId() == R.id.search) {
                // Action pour Search
                // Démarrer l'activité de recherche
                Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            } else if (item.getItemId() == R.id.transaction) {
                // Action pour Transaction
                // Démarrer l'activité des transactions
                Intent transactionIntent = new Intent(HomeActivity.this, TransactionActivity.class);
                startActivity(transactionIntent);
                return true;
            } else if (item.getItemId() == R.id.category) {
                // Action pour Category
                // Démarrer l'activité des catégories
                Intent categoryIntent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(categoryIntent);
                return true;
            } else if (item.getItemId() == R.id.profile) {
                // Action pour Profile
                // Démarrer l'activité du profil
                Intent profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            }

            // Si aucune des conditions n'est remplie, retourner false
            return false;
        });
    }
}
