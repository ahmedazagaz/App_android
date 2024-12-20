package com.example.myapplication.saving;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavingDetailsActivity extends AppCompatActivity {

    private ImageView savingIcon;
    private TextView progressLabel, amountGoalLabel, amountSavedLabel, savingNameLabel, savingNameActivity;
    private RecyclerView savesList;
    private List<Save> saveItems;
    private SaveAdapter saveAdapter;

    private FirebaseFirestore firestore;
    private String userId, savingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_details);

        initUI();
        initFirebase();

        // Récupération des données passées via l'intent
        savingName = getIntent().getStringExtra("savingName");
        int iconResId = getIntent().getIntExtra("savingIcon", R.drawable.ic_holder2);

        if (savingName == null || savingName.isEmpty()) {
            Log.e("SavingDetailsActivity", "Saving name is null or empty");
            finish();
            return;
        }

        savingIcon.setImageResource(iconResId);
        savingNameLabel.setText(savingName);

        loadSavingDetails();
        loadSaveItems();

        // Naviguer vers l'activité d'ajout de saving
        Button buttonAddSaving = findViewById(R.id.btn_add_saving);
        buttonAddSaving.setOnClickListener(v -> {
            Intent intent = new Intent(SavingDetailsActivity.this, AddSavingActivity.class);
            startActivity(intent);
        });
    }

    private void initUI() {
        savingIcon = findViewById(R.id.saving_icon);
        progressLabel = findViewById(R.id.progress_label);
        amountGoalLabel = findViewById(R.id.amount_goal_label);
        amountSavedLabel = findViewById(R.id.amount_saved_label);
        savingNameActivity = findViewById(R.id.saving_name);
        savingNameLabel = findViewById(R.id.saving_name_detail);
        savesList = findViewById(R.id.saves_list);

        savesList.setLayoutManager(new LinearLayoutManager(this));
        saveItems = new ArrayList<>();

        int iconResId = getIntent().getIntExtra("savingIcon", R.drawable.ic_holder2); // Récupération de l'icône
        saveAdapter = new SaveAdapter(this, saveItems, iconResId);
        savesList.setAdapter(saveAdapter);
    }


    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Log.e("SavingDetailsActivity", "User ID is null, exiting");
            finish();
        }
    }

    private void loadSavingDetails() {
        if (userId == null || savingName == null) {
            Log.e("SavingDetailsActivity", "User ID or Saving Name is null");
            return;
        }

        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .document(savingName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Récupérer et afficher les données
                        double goal = documentSnapshot.getDouble("goal");
                        double amountSaved = documentSnapshot.getDouble("amountSaved");
                        String icon = documentSnapshot.getString("icon");
                        String name = documentSnapshot.getString("name");

                        Log.d("SavingDetailsActivity", "Saving Name: " + savingName);  // Vérification dans les logs
                        Log.d("SavingDetailsActivity", "Amount Saved: " + amountSaved);


                        // Vérifier si le nom est bien récupéré
                        if (name != null && !name.isEmpty()) {
                            savingNameLabel.setText(name);  // Mettre à jour l'UI avec le nom du saving
                        } else {
                            Log.e("SavingDetailsActivity", "Saving name is missing in Firestore");
                            savingNameLabel.setText("Unknown Saving");  // Afficher un nom par défaut
                        }

                        // Mettre à jour l'UI
                        amountGoalLabel.setText(String.format(Locale.getDefault(), "Goal: $%.2f", goal));
                        amountSavedLabel.setText(String.format(Locale.getDefault(), "Saved: $%.2f", amountSaved));
                        amountSavedLabel.invalidate();  // Force l'invalidation du rendu
                        // Calculer le pourcentage d'accomplissement
                        int progress = (int) ((amountSaved / goal) * 100);
                        progressLabel.setText(String.format(Locale.getDefault(), "%d%% completed", progress));

                        // Mettre à jour l'icône si disponible
                        if (icon != null && !icon.isEmpty()) {
                            int iconResId = getResources().getIdentifier(icon, "drawable", getPackageName());
                            savingIcon.setImageResource(iconResId != 0 ? iconResId : R.drawable.ic_holder2);
                        }
                    } else {
                        Log.e("SavingDetailsActivity", "No document found for this saving");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading saving details", e));
    }

    private void loadSaveItems() {
        if (userId == null || savingName == null) {
            Log.e("SavingDetailsActivity", "User ID or Saving Name is null");
            return;
        }

        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .document(savingName)
                .collection("transactions")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    saveItems.clear();
                    double totalAmountSaved = 0.0;  // Variable to hold the total saved amount

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Save save = doc.toObject(Save.class);
                        saveItems.add(save);
                        totalAmountSaved += save.getAmount();  // Assuming Save class has a method getAmount()
                    }

                    // Update the amountSaved in the saving document with the total
                    updateAmountSaved(totalAmountSaved);

                    // Notify the adapter to update the RecyclerView
                    saveAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error loading transactions", e));
    }

    private void updateAmountSaved(double totalAmountSaved) {
        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .document(savingName)
                .update("amountSaved", totalAmountSaved)
                .addOnSuccessListener(aVoid -> {
                    Log.d("SavingDetailsActivity", "Amount saved updated successfully");

                    // Update the UI with the new amountSaved
                    amountSavedLabel.setText(String.format(Locale.getDefault(), "Saved: $%.2f", totalAmountSaved));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating amountSaved", e));
    }


}
