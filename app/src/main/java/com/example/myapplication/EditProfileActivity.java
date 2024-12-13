package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private TextView mNameTextView;
    private TextView mUserIdTextView;
    private ImageView mProfileImageView;
    private EditText mFullNameEditText;
    private EditText mEmailEditText;
    private Button mUpdateButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity); // Assurez-vous que ce fichier XML existe

        // Initialisation des objets Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        // Initialisation des vues
        mNameTextView = findViewById(R.id.profile_name);
        mUserIdTextView = findViewById(R.id.profile_user_id);
        mProfileImageView = findViewById(R.id.profile_image);
        mFullNameEditText = findViewById(R.id.FullName_signup);
        mEmailEditText = findViewById(R.id.Email_signup);
        mUpdateButton = findViewById(R.id.update_profile_button);

        // Récupérer les données de Firestore
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Si la récupération des données est réussie
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Récupérer le nom complet et l'email
                    String fullName = document.getString("Full Name");
                    String email = document.getString("Email");
                    String profilePictureUrl = document.getString("Profile Picture URL");

                    // Afficher ces informations dans les vues
                    mNameTextView.setText(fullName);
                    mUserIdTextView.setText("ID: " + userID);
                    mFullNameEditText.setText(fullName);
                    mEmailEditText.setText(email);

                    // Charger l'image de profil avec Glide
                    if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(profilePictureUrl)
                                .into(mProfileImageView);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "No such document!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        // Définir un gestionnaire de clic pour le bouton "Update Profile"
        mUpdateButton.setOnClickListener(view -> {
            // Récupérer les nouvelles valeurs du nom complet et de l'email
            String updatedFullName = mFullNameEditText.getText().toString().trim();
            String updatedEmail = mEmailEditText.getText().toString().trim();

            // Vérifier que les champs ne sont pas vides
            if (updatedFullName.isEmpty()) {
                mFullNameEditText.setError("Full Name is required!");
                mFullNameEditText.requestFocus();
                return;
            }

            if (updatedEmail.isEmpty()) {
                mEmailEditText.setError("Email is required!");
                mEmailEditText.requestFocus();
                return;
            }

            // Mettre à jour les données dans Firestore
            DocumentReference documentReferenceUpdate = fStore.collection("users").document(userID);
            documentReferenceUpdate.update("Full Name", updatedFullName, "Email", updatedEmail)
                    .addOnSuccessListener(aVoid -> {
                        // Notifier l'utilisateur que la mise à jour a réussi
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Retourner à l'activité précédente
                    })
                    .addOnFailureListener(e -> {
                        // Notifier l'utilisateur en cas d'échec
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
