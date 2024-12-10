package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText mFullName, mEmail, mPass;
    private Button btnSignup;
    private TextView mSignin;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialisation des composants Firebase
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        // Initialisation du ProgressDialog
        mDialog = new ProgressDialog(this);

        // Configuration de l'interface utilisateur
        registration();
    }

    private void registration() {
        mFullName = findViewById(R.id.FullName_signup);
        mEmail = findViewById(R.id.Email_signup);
        mPass = findViewById(R.id.Password_signup);
        btnSignup = findViewById(R.id.btn_signup);
        mSignin = findViewById(R.id.login_here);

        // Action pour le bouton "Sign Up"
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String FullName = mFullName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                // Validation des champs
                if (TextUtils.isEmpty(FullName)) {
                    mFullName.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password is required");
                    return;
                }
                if (pass.length() < 6) {
                    mPass.setError("Password must be at least 6 characters");
                    return;
                }

                // Affichage du ProgressDialog
                mDialog.setMessage("Registration in progress...");
                mDialog.show();

                // Création de l'utilisateur dans Firebase
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                    userID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fstore.collection("users").document(userID);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("Full Name", FullName);
                                    user.put("Email", email);

                                    // Ajout des données à Firestore
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("TAG", "onSuccess: User profile is created for " + userID);
                                        }
                                    });

                                    // Redirection vers l'écran de connexion
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // Action pour le lien "Sign In"
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}
