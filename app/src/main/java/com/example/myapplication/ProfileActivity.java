package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;


public class ProfileActivity extends AppCompatActivity {
    private TextView mNameTextView;  // TextView pour afficher le nom
    private ImageView mProfileImageView;  // ImageView pour l'image de profil
    private FirebaseAuth mAuth;     // FirebaseAuth pour obtenir l'utilisateur connecté
    private FirebaseFirestore fStore; // Firestore pour récupérer les données
    private String userID;          // ID de l'utilisateur connecté
    private TextView mUserIdTextView; // TextView pour afficher l'ID utilisateur

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialisation de FirebaseAuth et Firestore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Récupérer les TextViews
        mNameTextView = findViewById(R.id.profile_name); // Assurez-vous que l'ID est correct dans votre XML
        mUserIdTextView = findViewById(R.id.profile_user_id);
        mProfileImageView = findViewById(R.id.profile_image); // ImageView de l'image de profil

        // Obtenez l'ID de l'utilisateur connecté
        userID = mAuth.getCurrentUser().getUid();

        // Limiter l'affichage de l'ID utilisateur à 8 caractères
        String shortUserId = getShortUserId(userID);

        // Afficher l'ID limité dans le TextView
        mUserIdTextView.setText("ID: " + shortUserId);

        // Récupérez les données depuis Firestore
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Affichez le nom dans le TextView
                        String fullName = document.getString("Full Name");
                        mNameTextView.setText(fullName);

                        // Mettre à jour l'icône de l'image de profil si disponible
                        String profilePictureUrl = document.getString("Profile Picture URL");
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            // Si l'URL de l'image est présente, chargez l'image
                            Glide.with(ProfileActivity.this)
                                    .load(profilePictureUrl)
                                    .into(mProfileImageView);
                        } else {
                            // Sinon, afficher la première lettre du prénom dans l'image
                            if (fullName != null && !fullName.isEmpty()) {
                                char firstLetter = fullName.charAt(0);  // Première lettre du prénom
                                mProfileImageView.setImageDrawable(createDefaultProfileImage(firstLetter));
                            }
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
        // Créer un TextView pour afficher la première lettre
        TextView textView = new TextView(this);
        textView.setText(String.valueOf(firstLetter));
        textView.setTextColor(Color.WHITE); // Couleur du texte
        textView.setBackgroundColor(Color.parseColor("#00D09E")); // Couleur de fond
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(40); // Taille du texte
        textView.setLayoutParams(new ViewGroup.LayoutParams(120, 120)); // Taille de l'image (120dp)
        textView.setWidth(120); // Largeur de l'ImageView
        textView.setHeight(120); // Hauteur de l'ImageView

        // Convertir TextView en Drawable pour l'utiliser dans ImageView
        textView.setDrawingCacheEnabled(true);
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(textView.getDrawingCache());

        // Créer un cercle à partir de l'image bitmap
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

        // Créer un "Paint" pour dessiner le cercle
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Dessiner un cercle dans le "canvas"
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        // Utiliser un masque pour découper le bitmap en forme circulaire
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }
}