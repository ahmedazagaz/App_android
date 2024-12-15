package com.example.myapplication.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;  // Ajouté pour l'ImageView
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final Context context;
    private final List<Expense> expenseList;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.expenseName.setText(expense.getCategory());
        holder.expenseAmount.setText(String.format("%.2f DH", expense.getAmount()));
        holder.expenseDate.setText(expense.getDate());  // Affichage de la date
        holder.categoryIcon.setImageResource(expense.getCategoryIcon());  // Affichage de l'icône de la catégorie
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        final TextView expenseName;
        final TextView expenseAmount;
        final TextView expenseDate;  // Pour afficher la date
        final ImageView categoryIcon;  // Correction ici, il faut utiliser ImageView pour afficher l'icône

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.expense_name);
            expenseAmount = itemView.findViewById(R.id.expense_amount);
            expenseDate = itemView.findViewById(R.id.expense_date);  // Récupération de la vue pour la date
            categoryIcon = itemView.findViewById(R.id.category_icon);  // Récupération de l'ImageView pour l'icône de la catégorie
        }
    }
}
