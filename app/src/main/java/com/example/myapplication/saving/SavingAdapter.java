package com.example.myapplication.saving;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.saving.Saving;

import java.util.List;
public class SavingAdapter extends RecyclerView.Adapter<SavingAdapter.ViewHolder> {
    private final Context context;
    private final List<Saving> savingList;

    public SavingAdapter(Context context, List<Saving> savingList) {
        this.context = context;
        this.savingList = savingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saving, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Saving saving = savingList.get(position);
        holder.savingName.setText(saving.getName());
        holder.savingIcon.setImageResource(saving.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            if (saving.getName().equals("More")) {
                // Afficher le dialogue pour ajouter un nouveau saving
                ((SavingActivity) context).showAddSavingDialog();
            } else {
                Toast.makeText(context, "Saving: " + saving.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return savingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView savingName;
        ImageView savingIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            savingName = itemView.findViewById(R.id.saving_name);
            savingIcon = itemView.findViewById(R.id.saving_icon);
        }
    }
}
