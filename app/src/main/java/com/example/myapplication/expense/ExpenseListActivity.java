package com.example.myapplication.expense;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.expense.AddExpenseActivity;
import com.example.myapplication.expense.Expense;
import com.example.myapplication.expense.ExpenseAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private RecyclerView expenseListRecyclerView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;
    private String categoryName; // Nom de la catégorie sélectionnée

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        // Récupérer le nom de la catégorie sélectionnée
        categoryName = getIntent().getStringExtra("category_name");

        // Mise à jour du TextView pour afficher le nom de la catégorie
        TextView categoryTextView = findViewById(R.id.category_name);
        categoryTextView.setText(categoryName);

        // Initialiser Firebase Firestore
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialiser RecyclerView
        expenseListRecyclerView = findViewById(R.id.expense_list_recycler);
        expenseListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        expenseListRecyclerView.setAdapter(expenseAdapter);

        // Charger les dépenses de la catégorie
        loadExpenses();

        // Configurer le bouton "Ajouter une dépense"
        Button addExpenseButton = findViewById(R.id.add_expense_button);
        addExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseListActivity.this, AddExpenseActivity.class);
            intent.putExtra("category_name", categoryName);
            startActivity(intent);
        });
    }


    private void loadExpenses() {
        firestore.collection("users")
                .document(userId)
                .collection("expenses")
                .whereEqualTo("category", categoryName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    expenseList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Expense expense = doc.toObject(Expense.class);

                        // Mappez manuellement l'icône si elle est manquante ou incorrecte
                        if (expense.getCategory().equals("Food")) {
                            expense.setCategoryIcon(R.drawable.ic_food);
                        } else if (expense.getCategory().equals("Transport")) {
                            expense.setCategoryIcon(R.drawable.ic_transport);
                        }else if (expense.getCategory().equals("Entertainment")) {
                            expense.setCategoryIcon(R.drawable.ic_entertainment);
                        }else if (expense.getCategory().equals("Medicine")) {
                            expense.setCategoryIcon(R.drawable.ic_medicine);
                        }else if (expense.getCategory().equals("Rent")) {
                            expense.setCategoryIcon(R.drawable.ic_rent);
                        }else if (expense.getCategory().equals("Saving")) {
                            expense.setCategoryIcon(R.drawable.ic_saving);
                        }else if (expense.getCategory().equals("Groceries")) {
                            expense.setCategoryIcon(R.drawable.ic_groceries);
                        }
                        else {
                            expense.setCategoryIcon(R.drawable.placeholder); // Par défaut
                        }

                        expenseList.add(expense);
                    }
                    expenseAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ExpenseListActivity.this, "Failed to load expenses", Toast.LENGTH_SHORT).show()
                );
    }

}
