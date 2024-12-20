package com.example.myapplication.saving;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddSavingActivity extends AppCompatActivity {

    private EditText savingTitle, savingGoal, savedAmount, savingDate, savingMessage;
    private Button btnSubmitSaving;
    private Spinner savingCategorySpinner;
    private FirebaseFirestore firestore;
    private String userId;
    private ArrayList<String> savingList;  // Liste des noms de catégories
    private ArrayList<String> savingIds;  // Liste des IDs des documents

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving);

        // Initialisation des vues
        savingTitle = findViewById(R.id.saving_title);
        savingGoal = findViewById(R.id.saving_goal);
        savedAmount = findViewById(R.id.saved_amount);
        savingDate = findViewById(R.id.saving_date);
        savingMessage = findViewById(R.id.saving_message);
        savingCategorySpinner = findViewById(R.id.saving_category_spinner);
        btnSubmitSaving = findViewById(R.id.submit_saving_button);

        // Initialisation Firestore et utilisateur
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialiser les listes
        savingList = new ArrayList<>();
        savingIds = new ArrayList<>();

        // Récupération des types de savings depuis Firestore
        fetchSavingTypesFromFirestore();

        // Sélecteur de date
        savingDate.setOnClickListener(v -> showDatePicker());

        // Action bouton soumettre
        btnSubmitSaving.setOnClickListener(view -> handleSavingSubmission());
    }

    private void fetchSavingTypesFromFirestore() {
        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        savingList.clear();
                        savingIds.clear();

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String savingType = document.getString("name");
                            if (savingType != null) {
                                savingList.add(savingType);
                                savingIds.add(document.getId());
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, savingList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        savingCategorySpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "No saving types available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch saving types", Toast.LENGTH_SHORT).show());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, day1) -> {
            String date = day1 + "/" + (month1 + 1) + "/" + year1;
            savingDate.setText(date);
        }, year, month, day).show();
    }

    private void handleSavingSubmission() {
        String title = savingTitle.getText().toString().trim();
        String goal = savingGoal.getText().toString().trim();
        String saved = savedAmount.getText().toString().trim();
        String date = savingDate.getText().toString().trim();
        String message = savingMessage.getText().toString().trim();

        if (title.isEmpty() || goal.isEmpty() || saved.isEmpty() || date.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double goalValue;
        double savedValue;

        try {
            goalValue = Double.parseDouble(goal);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid goal value", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            savedValue = Double.parseDouble(saved);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid saved amount", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedSavingType = savingCategorySpinner.getSelectedItem().toString();
        String savingId = savingIds.get(savingList.indexOf(selectedSavingType));

        saveNewSaving(title, goalValue, savedValue, date, message, savingId);
    }

    private void saveNewSaving(String title, double goal, double saved, String date, String message, String savingId) {
        Map<String, Object> newSaving = new HashMap<>();
        newSaving.put("goal", goal);
        newSaving.put("savedAmount", saved);

        firestore.collection("users")
                .document(userId)
                .collection("savings")
                .document(savingId)
                .update(newSaving)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("name", title);
                    transaction.put("date", date);
                    transaction.put("amount", saved);
                    transaction.put("message", message);

                    firestore.collection("users")
                            .document(userId)
                            .collection("savings")
                            .document(savingId)
                            .collection("transactions")
                            .add(transaction)
                            .addOnSuccessListener(aVoid1 -> Toast.makeText(this, "Saving and transaction added successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update saving", Toast.LENGTH_SHORT).show());
    }
}
