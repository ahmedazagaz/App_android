package com.example.myapplication.expense;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText expenseTitle, expenseMessage, expenseDate, expenseTime, expenseAmount;
    private Spinner categorySpinner;
    private Button submitButton;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialiser Firebase
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialiser les vues
        categorySpinner = findViewById(R.id.category_spinner);
        expenseTitle = findViewById(R.id.expense_title);
        expenseMessage = findViewById(R.id.expense_message);
        expenseDate = findViewById(R.id.expense_date);
        expenseTime = findViewById(R.id.expense_time);
        expenseAmount = findViewById(R.id.expense_amount); // Nouveau champ pour le montant
        submitButton = findViewById(R.id.submit_expense_button);

        // Charger les catégories dans le Spinner
        loadCategories();

        // Configurer les sélecteurs de date et d'heure
        expenseDate.setOnClickListener(v -> showDatePicker());
        expenseTime.setOnClickListener(v -> showTimePicker());

        // Configurer le bouton de soumission
        submitButton.setOnClickListener(v -> addExpense());
    }

    private void loadCategories() {
        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        adapter.add(doc.getString("name"));
                    }

                    categorySpinner.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            expenseDate.setText(String.format("%d-%d-%d", year, month + 1, dayOfMonth));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            expenseTime.setText(String.format("%02d:%02d", hourOfDay, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void addExpense() {
        String title = expenseTitle.getText().toString().trim();
        String message = expenseMessage.getText().toString().trim();
        String date = expenseDate.getText().toString().trim();
        String time = expenseTime.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String amountStr = expenseAmount.getText().toString().trim();

        // Validation des champs
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(category) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le montant est valide
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ajouter la dépense dans Firestore
        Map<String, Object> expense = new HashMap<>();
        expense.put("title", title);
        expense.put("message", message);
        expense.put("date", date);
        expense.put("time", time);
        expense.put("category", category);
        expense.put("amount", amount);

        firestore.collection("users")
                .document(userId)
                .collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show());
    }
}
