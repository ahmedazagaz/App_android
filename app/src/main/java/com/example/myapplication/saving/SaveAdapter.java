package com.example.myapplication.saving;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;
import java.util.Locale;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.SaveViewHolder> {

    private Context context;
    private List<Save> saveItems;
    private int savingIconResId; // Icone associée au Saving principal

    public SaveAdapter(Context context, List<Save> saveItems, int savingIconResId) {
        this.context = context;
        this.saveItems = saveItems;
        this.savingIconResId = savingIconResId;
    }

    @NonNull
    @Override
    public SaveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_save, parent, false);
        return new SaveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaveViewHolder holder, int position) {
        Save save = saveItems.get(position);

        holder.date.setText(save.getDate() != null ? save.getDate() : "No Date");
        holder.amount.setText(String.format(Locale.getDefault(), "$%.2f", save.getAmount()));
        holder.message.setText(save.getMessage() != null ? save.getMessage() : "No Message");
        holder.name.setText(save.getName() != null ? save.getName() : "No Name");

        // Utiliser l'icône principale
        holder.icon.setImageResource(savingIconResId);

        if (save.getMessage() == null || save.getMessage().isEmpty()) {
            holder.message.setVisibility(View.GONE);
        } else {
            holder.message.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return saveItems.size();
    }

    public static class SaveViewHolder extends RecyclerView.ViewHolder {

        TextView date, amount, message, name;
        ImageView icon; // ImageView pour l'icône

        public SaveViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.save_date);
            amount = itemView.findViewById(R.id.save_amount);
            message = itemView.findViewById(R.id.save_message);
            name = itemView.findViewById(R.id.save_name);
            icon = itemView.findViewById(R.id.saving_icon); // Lier l'ImageView
        }
    }
}
