package com.example.myapplication.category;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryGrid;
    private CategoryAdapter categoryAdapter;
    private final List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryGrid = findViewById(R.id.category_grid);
        categoryGrid.setLayoutManager(new GridLayoutManager(this, 3)); // Grille de 3 colonnes

        // Charger les catégories
        loadCategories();

        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryGrid.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        // Simulation de données, remplacez par des données de base de données
        categoryList.add(new Category("Food", R.drawable.ic_food, false));
        categoryList.add(new Category("Transport", R.drawable.ic_transport, false));
        categoryList.add(new Category("Shopping", R.drawable.ic_shopping, false));
        categoryList.add(new Category("More", R.drawable.ic_more, true));
    }
}
