package com.example.myapplication.Login;

import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;

import com.example.myapplication.Registration.RegistrationActivity;

public class ReseatActivity extends AppCompatActivity {

    private EditText mEmailReset;
    private Button btnResetPassword;
    private TextView mSigninLink;
    private TextView mBackToLogin;

    // FirebaseAuth instance
    private FirebaseAuth mAuth;

    // ProgressDialog to show loading message
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reseat);

        // Setting up insets for the layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth and ProgressDialog
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        // Initialize UI components
        mEmailReset = findViewById(R.id.email_reset);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        mSigninLink = findViewById(R.id.signin_link);
        mBackToLogin=findViewById(R.id.backlogin);

        // Setting up button click for password reset
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailReset.getText().toString().trim();

                // Check if the email field is empty
                if (TextUtils.isEmpty(email)) {
                    mEmailReset.setError("Email is required");
                    return;
                }

                // Show loading dialog
                mDialog.setMessage("Sending password reset email...");
                mDialog.show();

                // Send password reset email using FirebaseAuth
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Link to sign-in screen
        mSigninLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
        // Link to LoginActivity
        mBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}
