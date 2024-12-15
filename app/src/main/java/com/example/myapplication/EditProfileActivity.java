package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private StorageReference storageReference;

    private String userID;

    private TextView mNameTextView, mUserIdTextView;
    private EditText mFullNameEditText, mEmailEditText;
    private ImageView mProfileImageView;
    private Button mUpdateProfileButton, mUploadImageButton;

    private Uri imageUri; // Pour l'image sélectionnée depuis la galerie

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        storageReference = fStorage.getReference();

        userID = mAuth .getCurrentUser().getUid();

        // Lier les éléments UI
        mNameTextView = findViewById(R.id.profile_name);
        mUserIdTextView = findViewById(R.id.profile_user_id);
        mFullNameEditText = findViewById(R.id.FullName_signup);
        mEmailEditText = findViewById(R.id.Email_signup);
        mProfileImageView = findViewById(R.id.profile_image);
        mUpdateProfileButton = findViewById(R.id.update_profile_button);
        mUploadImageButton = findViewById(R.id.upload_image_button);

        // Charger les données utilisateur existantes
        loadUserData();

        // Upload image
        mUploadImageButton.setOnClickListener(v -> checkPermissionsAndOpenGallery());

        // Mettre à jour le profil
        mUpdateProfileButton.setOnClickListener(v -> updateProfile());
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

    private void checkPermissionsAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
        } else {
            openGalleryIntent();
        }
    }

    private void openGalleryIntent() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryIntent();
            } else {
                Toast.makeText(this, "Permission denied to access storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            mProfileImageView.setImageURI(imageUri);
        }
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

        if (imageUri != null) {
            // Upload image to Firebase Storage
            StorageReference imageRef = storageReference.child("profile_images/" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveUserProfile(documentReference, fullName, email, imageUrl, progressDialog);
                    })
            ).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(EditProfileActivity.this, "Image upload failed. Try again.", Toast.LENGTH_SHORT).show();
            });
        } else {
            saveUserProfile(documentReference, fullName, email, null, progressDialog);
        }
    }

    private void saveUserProfile(DocumentReference documentReference, String fullName, String email, String imageUrl, ProgressDialog progressDialog) {
        if (imageUrl != null) {
            documentReference.update("Full Name", fullName, "Email", email, "Profile Picture URL", imageUrl)
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
        } else {
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
}