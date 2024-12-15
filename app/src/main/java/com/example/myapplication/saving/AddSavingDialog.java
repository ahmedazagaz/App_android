package com.example.myapplication.saving;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.R;

public class AddSavingDialog {

    private final Context context;
    private final Dialog dialog;
    private final EditText savingNameInput;
    private final Button addButton;

    public interface AddSavingListener {
        void onSavingAdded(String name, int iconResId);
    }

    private AddSavingListener listener;

    public AddSavingDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_saving);

        savingNameInput = dialog.findViewById(R.id.saving_name_input);
        addButton = dialog.findViewById(R.id.add_saving_button);

        addButton.setOnClickListener(v -> {
            String name = savingNameInput.getText().toString().trim();
            if (!name.isEmpty()) {
                listener.onSavingAdded(name, R.drawable.ic_placeholder); // Add default icon (change as needed)
                dialog.dismiss();
            } else {
                savingNameInput.setError("Saving name is required");
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public void setAddSavingListener(AddSavingListener listener) {
        this.listener = listener;
    }
}
