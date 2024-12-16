package com.example.myapplication.saving;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.R;
import com.example.myapplication.Search.SearchActivity;
import com.example.myapplication.Transaction.TransactionActivity;
import com.example.myapplication.category.CategoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavingActivity extends AppCompatActivity {

    private RecyclerView savingGrid;
    private SavingAdapter savingAdapter;
    private final List<Saving> savingList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile); // Marquer le profil comme sélectionné

        bottomNavigationView.setOnItemSelectedListener(item -> {

            // Navigation par conditions if-else
            Intent intent = null;

            if (item.getItemId() == R.id.homes) {
                intent = new Intent(SavingActivity.this, HomeActivity.class);
            } else if (item.getItemId() == R.id.search) {
                intent = new Intent(SavingActivity.this, SearchActivity.class);
            } else if (item.getItemId() == R.id.transaction) {
                intent = new Intent(SavingActivity.this, TransactionActivity.class);
            } else if (item.getItemId() == R.id.category) {
                intent = new Intent(SavingActivity.this, CategoryActivity.class);
            } else if (item.getItemId() == R.id.profile) {
                // Déjà sur cette activité, aucun changement nécessaire
                return true;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // Désactiver les animations pour un comportement fluide
                return true;
            }

            return false;
        });



        // Initialisation Firestore et Firebase Auth
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialisation de la grille des savings
        savingGrid = findViewById(R.id.saving_grid);
        savingGrid.setLayoutManager(new GridLayoutManager(this, 3));

        // Initialisation de l'adaptateur
        savingAdapter = new SavingAdapter(this, savingList);
        savingGrid.setAdapter(savingAdapter);

        // Charger les savings depuis Firebase
        loadSavings();

        // Ajouter les savings par défaut si nécessaires
        addDefaultSavingsIfNeeded();



    }

    private void addDefaultSavingsIfNeeded() {
        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Ajouter les savings par défaut
                        addDefaultSavings();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to check default savings: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addDefaultSavings() {
        List<Saving> defaultSavings = new ArrayList<>();
        defaultSavings.add(new Saving("Travel", R.drawable.ic_travel));
        defaultSavings.add(new Saving("New House", R.drawable.ic_house));
        defaultSavings.add(new Saving("Car", R.drawable.ic_car));
        defaultSavings.add(new Saving("Wedding", R.drawable.ic_wedding));

        for (Saving saving : defaultSavings) {
            firestore.collection("users")
                    .document(userId)
                    .collection("savings")
                    .document(saving.getName()) // Utiliser le nom comme ID unique
                    .set(saving)
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadSavings() {
        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savingList.clear(); // Effacer les anciennes données avant d'ajouter les nouvelles

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Saving saving = doc.toObject(Saving.class);
                        savingList.add(saving);
                    }

                    // Ajouter le bouton "More" à la fin
                    boolean hasMoreButton = false;
                    for (Saving saving : savingList) {
                        if (saving.getName().equals("More")) {
                            hasMoreButton = true;
                            break;
                        }
                    }

                    if (!hasMoreButton) {
                        savingList.add(new Saving("More", R.drawable.ic_more));
                    }

                    // Notifier l'adaptateur pour mettre à jour l'interface utilisateur
                    savingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(SavingActivity.this, "Failed to load savings", Toast.LENGTH_SHORT).show());
    }

    void showAddSavingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_saving, null);
        builder.setView(dialogView);

        EditText savingNameInput = dialogView.findViewById(R.id.saving_name_input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String savingName = savingNameInput.getText().toString().trim();
            if (!savingName.isEmpty()) {
                // Ajouter un nouveau saving dans Firestore
                Saving newSaving = new Saving(savingName, R.drawable.ic_placeholder);
                firestore.collection("users")
                        .document(userId)
                        .collection("savings")
                        .document(newSaving.getName())
                        .set(newSaving)
                        .addOnSuccessListener(aVoid -> {
                            savingList.add(newSaving);
                            savingAdapter.notifyDataSetChanged();
                            Toast.makeText(this, "Saving added!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to add saving: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Saving name cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
