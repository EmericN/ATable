package com.emeric.nicot.atable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    private static String url_lecture_user = "http://192.168.1.24:80/DB/db_lecture_user.php";
    Button btnConnecter, btnInscription;
    EditText editTextMail, editTextPassword;
    TextInputLayout input_layout_mail, input_layout_pass;
    JSONParser jsonParser = new JSONParser();
    SessionManagement session;
    private String TAG = "indentification";
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        session = new SessionManagement(getApplicationContext());
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
                    // new CheckUser().execute(mail, password);
                    signIn(mail, password);

           /*         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    writeNewUser(nom,prenom,mail);*/

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
        //updateUI(currentUser);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //session.createLoginSession(params.get("password"), params.get("mail"));

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            // closing this screen
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //  mStatusTextView.setText(R.string.auth_failed);
                        }
                        //  hideProgressDialog();
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

    public void writeNewUser(String nom, String prenom, String mail) {

        InscriptionActivity.User user = new InscriptionActivity.User(nom, prenom, mail);
        mDatabase.child("users").push().setValue(user);
    }

    public static class User {

        public String nom;
        public String prenom;
        public String mail;

        public User() {
        }

        public User(String nom, String prenom, String mail) {
            this.nom = nom;
            this.mail = mail;
            this.prenom = prenom;
        }

    }

    class CheckUser extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Vérification...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating user in background thread
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("mail", args[0]);
            params.put("password", args[1]);

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_lecture_user,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created user
                    session.createLoginSession(params.get("password"), params.get("mail"));
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    // closing this screen
                    finish();
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();

        }

    }

}
