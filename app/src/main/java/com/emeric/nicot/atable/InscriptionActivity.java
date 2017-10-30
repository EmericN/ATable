package com.emeric.nicot.atable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private FirebaseFirestore mFirestore;
    private String nom, prenom, nomPrenom, mail, password;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Edit Text
        inputNom = (EditText) findViewById(R.id.editTextNom);
        inputPrenom = (EditText) findViewById(R.id.editTextPrenom);
        inputMail = (EditText) findViewById(R.id.editTextMail);
        inputPassword = (EditText) findViewById(R.id.editTextMDP);

        // Create button
        Button btnCreateUser = (Button) findViewById(R.id.buttonEnvoyer);


        // button click event
        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                nom = inputNom.getText().toString();
                prenom = inputPrenom.getText().toString();
                nomPrenom = nom + "_" + prenom;
                mail = inputMail.getText().toString();
                password = inputPassword.getText().toString();

                createAccount(mail, password);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            }
        });
    }

    private void createAccount(String mail, String password) {
        Log.d(TAG, "createAccount:" + mail);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
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
        userMap.put("nom_prenom", nomPrenom);
        userMap.put("mail", user.getEmail());

        mFirestore.collection("users").document(user.getUid())
                .set(userMap, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "user successfully registered");
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "user not registered");
                    }
                })

        ;

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            /*i.putExtra("Nom",nom);
                            i.putExtra("Prenom",prenom);*/
        startActivity(i);
        finish();
    }


    private boolean validateForm() {
        boolean valid = true;

        String mail = inputMail.getText().toString();
        if (TextUtils.isEmpty(mail)) {
            inputMail.setError("Required.");
            valid = false;
        } else {
            inputMail.setError(null);
        }

        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Required.");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }
}
