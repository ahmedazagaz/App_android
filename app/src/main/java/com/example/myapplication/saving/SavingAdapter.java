package com.example.myapplication.saving;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

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
        // Inflate the layout for individual saving items
        View view = LayoutInflater.from(context).inflate(R.layout.item_saving, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Saving saving = savingList.get(position);

        // Bind the saving's name and icon
        holder.savingName.setText(saving.getName());
        holder.savingIcon.setImageResource(saving.getIconResId());

        // Set an OnClickListener for each saving item
        holder.itemView.setOnClickListener(v -> {
            if (saving.getName().equals("More")) {
                // If the "More" saving is clicked, show the dialog for adding a new saving
                ((SavingActivity) context).showAddSavingDialog();
            } else {
                // If a specific saving is clicked, navigate to the SavingDetailsActivity
                Intent intent = new Intent(context, SavingDetailsActivity.class);

                // Pass the saving name and icon resource ID to the details activity
                intent.putExtra("savingName", saving.getName());
                intent.putExtra("savingIcon", saving.getIconResId());

                // Start the activity
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savingList.size();
    }

    // ViewHolder class to hold the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView savingName;
        ImageView savingIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            savingName = itemView.findViewById(R.id.saving_name);
            savingIcon = itemView.findViewById(R.id.saving_icon);
        }
    }
}
