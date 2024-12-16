package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.category.CategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView mNameTextView; // TextView pour afficher le nom
    private ImageView mProfileImageView; // ImageView pour l'image de profil
    private FirebaseAuth mAuth; // FirebaseAuth pour obtenir l'utilisateur connecté
    private FirebaseFirestore fStore; // Firestore pour récupérer les données
    private String userID; // ID de l'utilisateur connecté
    private TextView mUserIdTextView; // TextView pour afficher l'ID utilisateur
    private LinearLayout mEditProfileLayout; // LinearLayout pour "Edit Profile"
    private LinearLayout msecurityLayout; // LinearLayout pour "settings Profile"
    private LinearLayout msettingsLayout; // LinearLayout pour "Edit Profile"

    private LinearLayout mLogoutLayout; // LinearLayout pour "Logout"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialisation de FirebaseAuth et Firestore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialisation des vues
        mNameTextView = findViewById(R.id.profile_name);
        mUserIdTextView = findViewById(R.id.profile_user_id);
        mProfileImageView = findViewById(R.id.profile_image);
        mEditProfileLayout = findViewById(R.id.edit_profile_layout);
        msecurityLayout = findViewById(R.id.security_layout);
        msettingsLayout = findViewById(R.id.settings_layout);
        mLogoutLayout = findViewById(R.id.logout_layout);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile); // Marquer le profil comme sélectionné

        bottomNavigationView.setOnItemSelectedListener(item -> {

            // Navigation par conditions if-else
            Intent intent = null;

            if (item.getItemId() == R.id.homes) {
                intent = new Intent(ProfileActivity.this, HomeActivity.class);
            } else if (item.getItemId() == R.id.search) {
                intent = new Intent(ProfileActivity.this, SearchActivity.class);
            } else if (item.getItemId() == R.id.transaction) {
                intent = new Intent(ProfileActivity.this, TransactionActivity.class);
            } else if (item.getItemId() == R.id.category) {
                intent = new Intent(ProfileActivity.this, CategoryActivity.class);
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

        mEditProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);

                // Pass the current data to EditProfileActivity
                intent.putExtra("userName", mNameTextView.getText().toString());
                intent.putExtra("userId", mUserIdTextView.getText().toString());

                startActivityForResult(intent, 1); // Use startActivityForResult to get updated data back
            }
        });




        // Configurer le clic
        msecurityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer vers SecurityActivity
                Intent intent = new Intent(ProfileActivity.this, SecurityActivity.class);
                startActivity(intent);
            }
        });

        // Configurer le clic
        msettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer vers SettingdActivity
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        // Configurer le clic sur le LinearLayout "Logout"
        mLogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog(); // Afficher la boîte de dialogue de déconnexion
            }
        });

        // Obtenir l'ID de l'utilisateur connecté
        userID = mAuth.getCurrentUser().getUid();

        // Limiter l'affichage de l'ID utilisateur à 8 caractères
        String shortUserId = getShortUserId(userID);
        mUserIdTextView.setText("ID: " + shortUserId);

        // Récupérer les données depuis Firestore
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fullName = document.getString("Full Name");
                        mNameTextView.setText(fullName);

                        // Récupérer l'URL de la photo de profil
                        String profilePictureUrl = document.getString("Profile Picture URL");
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(profilePictureUrl)
                                    .into(mProfileImageView);
                        } else if (fullName != null && !fullName.isEmpty()) {
                            char firstLetter = fullName.charAt(0);
                            mProfileImageView.setImageDrawable(createDefaultProfileImage(firstLetter));
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Fonction pour limiter l'ID utilisateur à 8 caractères
    private String getShortUserId(String userId) {
        return userId.length() > 8 ? userId.substring(0, 8) : userId;
    }

    // Fonction pour créer une image avec la première lettre du prénom
    private Drawable createDefaultProfileImage(char firstLetter) {
        TextView textView = new TextView(this);
        textView.setText(String.valueOf(firstLetter));
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.parseColor("#00D09E"));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(40);
        textView.setLayoutParams(new ViewGroup.LayoutParams(120, 120));
        textView.setWidth(120);
        textView.setHeight(120);

        textView.setDrawingCacheEnabled(true);
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(textView.getDrawingCache());

        Bitmap circularBitmap = getCircularBitmap(bitmap);

        return new BitmapDrawable(getResources(), circularBitmap);
    }

    // Fonction pour transformer un Bitmap en cercle
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int radius = Math.min(width, height) / 2;

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawCircle(width / 2, height / 2, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }

    // Méthode pour afficher la boîte de dialogue de déconnexion
    private void showLogoutDialog() {
        // Créer le dialogue personnalisé
        View dialogView = getLayoutInflater().inflate(R.layout.end_session_dialog, null);

        // Initialiser les vues du dialogue
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_message);
        Button btnConfirmLogout = dialogView.findViewById(R.id.btn_confirm_logout);
        Button btnCancelLogout = dialogView.findViewById(R.id.btn_cancel_logout);

        // Configurer le dialogue
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        final android.app.AlertDialog dialog = builder.create();

        // Bouton "Yes, End Session"
        btnConfirmLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Fermer le dialogue
                logoutUser(); // Appeler la méthode de déconnexion
            }
        });

        // Bouton "Cancel"
        btnCancelLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Fermer le dialogue
            }
        });

        // Afficher le dialogue
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Si la mise à jour a réussi, obtenir les données modifiées
            String updatedName = data.getStringExtra("updatedName");
            String updatedEmail = data.getStringExtra("updatedEmail");

            if (updatedName != null && !updatedName.isEmpty()) {
                mNameTextView.setText(updatedName);  // Mettre à jour le nom dans le TextView
            }
            if (updatedEmail != null && !updatedEmail.isEmpty()) {
                // Mettre à jour l'email si nécessaire
                // mEmailTextView.setText(updatedEmail);
            }

            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode pour gérer la déconnexion de l'utilisateur
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Déconnexion de Firebase
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Effacer l'historique des activités
        startActivity(intent);
        finish();
    }
}
