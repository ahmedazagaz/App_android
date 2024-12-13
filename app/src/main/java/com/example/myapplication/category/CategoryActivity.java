package com.example.myapplication.category;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.TransactionActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryGrid;
    private CategoryAdapter categoryAdapter;
    private final List<Category> categoryList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialisation de Firestore et de l'ID utilisateur connecté
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialisation de la grille des catégories
        categoryGrid = findViewById(R.id.category_grid);
        categoryGrid.setLayoutManager(new GridLayoutManager(this, 3)); // Grille de 3 colonnes

        // Configuration de l'adaptateur
        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryGrid.setAdapter(categoryAdapter);

        // Chargement des catégories depuis Firebase
        loadCategories();

        // Configuration de la barre de navigation inférieure
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.category); // Marquer la catégorie comme sélectionnée

        bottomNavigationView.setOnItemSelectedListener(item -> {

            // Navigation par conditions if-else
            Intent intent = null;

            if (item.getItemId() == R.id.homes) {
                intent = new Intent(CategoryActivity.this, HomeActivity.class);
            } else if (item.getItemId() == R.id.search) {
                intent = new Intent(CategoryActivity.this, SearchActivity.class);
            } else if (item.getItemId() == R.id.transaction) {
                intent = new Intent(CategoryActivity.this, TransactionActivity.class);
            } else if (item.getItemId() == R.id.category) {
                // Déjà sur cette activité, aucun changement nécessaire
                return true;
            } else if (item.getItemId() == R.id.profile) {
                intent = new Intent(CategoryActivity.this, ProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // Désactiver les animations pour un comportement fluide
                return true;
            }

            return false;
        });
    }


    private void loadCategories() {
        // Charger les catégories spécifiques à l'utilisateur connecté
        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear(); // Effacer les anciennes données avant d'ajouter les nouvelles

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Category category = doc.toObject(Category.class);
                        if (!category.isMoreButton()) { // Ignorer les anciens boutons "More" (si présents)
                            categoryList.add(category);
                        }
                    }

                    // Vérifier si le bouton "More" est déjà présent ; sinon, l'ajouter
                    boolean hasMoreButton = false;
                    for (Category category : categoryList) {
                        if (category.isMoreButton()) {
                            hasMoreButton = true;
                            break;
                        }
                    }

                    if (!hasMoreButton) {
                        // Ajouter le bouton "More" à la fin
                        categoryList.add(new Category("More", R.drawable.ic_more, true, userId));
                    }

                    // Notifier l'adaptateur pour mettre à jour l'interface utilisateur
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(CategoryActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show()
                );
    }
}
