package com.emeric.nicot.atable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    Button btnConnecter, btnInscription;
    EditText editTextMail, editTextPassword;
    TextInputLayout input_layout_mail, input_layout_pass;
    private String TAG = "indentification";
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        //Buttons
        btnConnecter = (Button) findViewById(R.id.buttonConnecter);
        btnInscription = (Button) findViewById(R.id.buttonInscriptioon);

        //EditText
        editTextMail = (EditText) findViewById(R.id.editTextMail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        //input_layout
        input_layout_mail = (TextInputLayout) findViewById(R.id.input_layout_mail);
        input_layout_pass = (TextInputLayout) findViewById(R.id.input_layout_pass);

        // view inscription click event
        btnInscription.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent inscription = new Intent(getApplicationContext(), InscriptionActivity.class);
                startActivity(inscription);
            }
        });


        // view products click event
        btnConnecter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String mail = editTextMail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (!mail.isEmpty() && !password.isEmpty()) {
                    signIn(mail, password);

                } else {
                    if (mail.isEmpty()) {
                        input_layout_mail.setError(getString(R.string.err_msg_email));
                    }
                    if (password.isEmpty()) {
                        input_layout_pass.setError(getString(R.string.err_msg_password));
                    }
                }
            }
        });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            // closing this screen
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editTextMail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextMail.setError("Required.");
            valid = false;
        } else {
            editTextMail.setError(null);
        }

        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Required.");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }
}
