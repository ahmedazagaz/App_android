package com.example.myapplication.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Home.HomeActivity;
import com.example.myapplication.NotificationActivity;
import com.example.myapplication.R;
import com.example.myapplication.Search.SearchActivity;
import com.example.myapplication.Transaction.TransactionActivity;
import com.example.myapplication.category.CategoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private String userID;

    private TextView mNameTextView, mUserIdTextView;
    private EditText mFullNameEditText, mEmailEditText;
    private ImageView mProfileImageView;
    private Button mUpdateProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        ImageView notificationIcon = findViewById(R.id.notification_icon);
        notificationIcon.setOnClickListener(v -> {
            // Démarrer l'activité de notification
            Intent notificationIntent = new Intent(EditProfileActivity.this, NotificationActivity.class);
            startActivity(notificationIntent);
        });

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = mAuth.getCurrentUser().getUid();

        // Lier les éléments UI
        mNameTextView = findViewById(R.id.profile_name);
        mUserIdTextView = findViewById(R.id.profile_user_id);
        mFullNameEditText = findViewById(R.id.FullName_signup);
        mEmailEditText = findViewById(R.id.Email_signup);
        mProfileImageView = findViewById(R.id.profile_image);
        mUpdateProfileButton = findViewById(R.id.update_profile_button);

        ImageView backIcon = findViewById(R.id.back_icon); // ID défini dans le fichier XML

        // Action sur le bouton "Retour"
        backIcon.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // Facultatif : ferme l'activité actuelle
        });

        // Charger les données utilisateur existantes
        loadUserData();

        // Mettre à jour le profil
        mUpdateProfileButton.setOnClickListener(v -> updateProfile());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile); // Marquer le profil comme sélectionné

        bottomNavigationView.setOnItemSelectedListener(item -> {

            // Navigation par conditions if-else
            Intent intent = null;

            if (item.getItemId() == R.id.homes) {
                intent = new Intent(EditProfileActivity.this, HomeActivity.class);
            } else if (item.getItemId() == R.id.search) {
                intent = new Intent(EditProfileActivity.this, SearchActivity.class);
            } else if (item.getItemId() == R.id.transaction) {
                intent = new Intent(EditProfileActivity.this, TransactionActivity.class);
            } else if (item.getItemId() == R.id.category) {
                intent = new Intent(EditProfileActivity.this, CategoryActivity.class);
            } else if (item.getItemId() == R.id.profile) {
                // Déjà sur cette activité, aucun changement nécessaire
                return true;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // Désactiver les animations pour un comportement fluide
                return true;
            }

            return false;
        });
    }
    private void loadUserData() {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String fullName = documentSnapshot.getString("Full Name");
                String email = documentSnapshot.getString("Email");
                String profileImageUrl = documentSnapshot.getString("Profile Picture URL");

                // Mettre à jour l'interface utilisateur
                mNameTextView.setText(fullName);
                mUserIdTextView.setText("ID: " + getShortUserId(userID));
                mFullNameEditText.setText(fullName);
                mEmailEditText.setText(email);

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this).load(profileImageUrl).into(mProfileImageView);
                }
            }
        });
    }

    private String getShortUserId(String userId) {
        return userId.length() > 8 ? userId.substring(0, 8) : userId;
    }

    private void updateProfile() {
        String fullName = mFullNameEditText.getText().toString();
        String email = mEmailEditText.getText().toString();

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.update("Full Name", fullName, "Email", email)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedName", fullName); // Send updated name to ProfileActivity
                    resultIntent.putExtra("updatedEmail", email); // Send updated email to ProfileActivity
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Close the EditProfileActivity
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile. Try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
