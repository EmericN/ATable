package com.emeric.nicot.atable.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emeric.nicot.atable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class InscriptionActivity extends AppCompatActivity {

    EditText inputNom, inputPrenom, inputMail, inputPassword;
    private String TAG = "DEBUG / FIREBASE";
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String nom, prenom, prenomNom, mail, password;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classic_registration_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        // Edit Text
        inputNom = findViewById(R.id.editTextNom);
        inputPrenom = findViewById(R.id.editTextPrenom);
        inputMail = findViewById(R.id.editTextMail);
        inputPassword = findViewById(R.id.editTextMDP);

        Button btnCreateUser = findViewById(R.id.buttonEnvoyer);
        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                nom = inputNom.getText().toString();
                prenom = inputPrenom.getText().toString();
                prenomNom = prenom + " " + nom;
                mail = inputMail.getText().toString();
                password = inputPassword.getText().toString();

                createAccount(mail, password);
            }
        });
    }

    private void createAccount(String mail, String password) {
        Log.d(TAG, "createAccount:" + mail);

        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            onAuthSuccess(task.getResult().getUser());

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(InscriptionActivity.this, "Registration failed : "
                                                                     + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("nom", nom);
        userMap.put("prenom", prenom);
        userMap.put("prenom_nom", prenomNom);
        userMap.put("mail", user.getEmail());

        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "user successfully registered");
                        Intent i = new Intent(getApplicationContext(), LoginChoiceActivity.class);
                        startActivity(i);
                        finish();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "user not registered");
                    }
                });
    }
}
