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
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
                return true;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
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

        savingAdapter = new SavingAdapter(this, savingList);
        savingGrid.setAdapter(savingAdapter);

        // Charger les savings depuis Firebase
        loadSavings();
        addDefaultSavingsIfNeeded();
    }

    private void addDefaultSavingsIfNeeded() {
        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        addDefaultSavings();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to check default savings: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addDefaultSavings() {
        List<Saving> defaultSavings = new ArrayList<>();
        defaultSavings.add(new Saving("Travel", R.drawable.ic_travel, 1000));
        defaultSavings.add(new Saving("New House", R.drawable.ic_house, 2000));
        defaultSavings.add(new Saving("Car", R.drawable.ic_car, 1500));
        defaultSavings.add(new Saving("Wedding", R.drawable.ic_wedding, 5000));

        for (Saving saving : defaultSavings) {
            firestore.collection("users")
                    .document(userId)
                    .collection("savings")
                    .document(saving.getName())
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
                    savingList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Saving saving = doc.toObject(Saving.class);
                        savingList.add(saving);
                    }

                    boolean hasMoreButton = false;
                    for (Saving saving : savingList) {
                        if (saving.getName().equals("More")) {
                            hasMoreButton = true;
                            break;
                        }
                    }

                    if (!hasMoreButton) {
                        savingList.add(new Saving("More", R.drawable.ic_more, 0));
                    }

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
        EditText goalInput = dialogView.findViewById(R.id.saving_goal_input);  // EditText pour le goal

        builder.setPositiveButton("Add", (dialog, which) -> {
            String savingName = savingNameInput.getText().toString().trim();
            String goalText = goalInput.getText().toString().trim();

            if (!savingName.isEmpty() && !goalText.isEmpty()) {
                double goal = Double.parseDouble(goalText);
                Saving newSaving = new Saving(savingName, R.drawable.ic_placeholder, goal);

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
                Toast.makeText(this, "Saving name and goal cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
