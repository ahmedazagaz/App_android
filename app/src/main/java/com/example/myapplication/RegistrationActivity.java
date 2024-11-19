package com.example.myapplication;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    // Déclaration des éléments d'interface utilisateur

    private EditText mFullName;
    private EditText mEmail;
    private EditText mPass;
    private Button btnSignup;
    private TextView mSignin;

    // Déclaration de l'objet ProgressDialog pour afficher un message de chargement
    private ProgressDialog mDialog;

    // Déclaration de l'objet FirebaseAuth pour gérer l'authentification Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        mAuth = FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);

        registration();

    }

    // Méthode pour configurer l'interface utilisateur et les actions d'inscription

    private void registration() {

        mFullName = findViewById(R.id.FullName_signup);
        mEmail = findViewById(R.id.Email_signup);
        mPass = findViewById(R.id.Password_signup);
        btnSignup = findViewById(R.id.btn_login);
        mSignin = findViewById(R.id.signin_here);

        // Définir l'action du bouton d'inscription

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Récupérer les valeurs des champs de texte

                String FullName = mFullName.getText().toString();
                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                // Vérifier si les champs est vide et afficher un message d'erreur

                if (TextUtils.isEmpty((FullName))) {
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

                // Afficher un message de chargement pendant la création de l'utilisateur
                mDialog.setMessage("Registration in progress");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if  (task.isSuccessful()){

                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                        }else {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        // Définir l'action du lien "Sign In"
        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

}