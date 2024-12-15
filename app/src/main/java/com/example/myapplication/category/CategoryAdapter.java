package com.example.myapplication.category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.expense.ExpenseListActivity;
import com.example.myapplication.saving.SavingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private final FirebaseFirestore firestore;
    private final String userId;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = new ArrayList<>();
        this.firestore = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ID utilisateur connecté
        loadCategories(); // Charger les catégories pour l'utilisateur
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.categoryName.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconResId());

        if (category.isMoreButton()) {
            holder.categoryIcon.setImageResource(R.drawable.ic_more);
            holder.categoryName.setText("More");
        }

        holder.itemView.setOnClickListener(v -> {
            if (category.isMoreButton()) {
                showAddCategoryDialog();
            } else {
                Toast.makeText(context, "Selected: " + category.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (category.isMoreButton()) {
                // Show dialog to add a new category
                showAddCategoryDialog();
            } else if (category.getName().equals("Saving")) {
                // Navigate to SavingActivity when the category is "Saving"
                Intent intent = new Intent(context, SavingActivity.class);
                intent.putExtra("category_name", category.getName()); // Optional: Pass category name if needed
                context.startActivity(intent);
            } else {
                // Navigate to ExpenseListActivity for other categories
                Intent intent = new Intent(context, ExpenseListActivity.class);
                intent.putExtra("category_name", category.getName());
                context.startActivity(intent);
            }
        });


    }

    private void loadCategories() {
        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (Category category : queryDocumentSnapshots.toObjects(Category.class)) {
                        categoryList.add(category);
                    }
                    // Ajouter le bouton "More" à la fin
                    categoryList.add(new Category("More", R.drawable.ic_more, true, userId));
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to load categories", Toast.LENGTH_SHORT).show()
                );
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null);
        builder.setView(view);

        TextView categoryNameInput = view.findViewById(R.id.category_name_input);
        View addCategoryButton = view.findViewById(R.id.add_category_button);

        AlertDialog dialog = builder.create();

        addCategoryButton.setOnClickListener(v -> {
            String name = categoryNameInput.getText().toString().trim();
            if (!name.isEmpty()) {
                addCategoryToFirebase(name);
                dialog.dismiss();
            } else {
                categoryNameInput.setError("Category name required");
            }
        });

        dialog.show();
    }

    private void addCategoryToFirebase(String name) {
        String id = firestore.collection("users")
                .document(userId)
                .collection("categories")
                .document().getId(); // Générer un ID unique
        Category newCategory = new Category(name, R.drawable.placeholder, false, userId);
        newCategory.setId(id);

        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .document(id)
                .set(newCategory)
                .addOnSuccessListener(aVoid -> {
                    categoryList.add(categoryList.size() - 1, newCategory);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Category added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to add category", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView categoryIcon;
        final TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}
