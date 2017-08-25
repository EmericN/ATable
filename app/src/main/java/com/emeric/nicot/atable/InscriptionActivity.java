package com.emeric.nicot.atable;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Nicot Emeric on 05/06/2017.
 */

public class InscriptionActivity extends AppCompatActivity {

    private static final String TAG_SUCCESS = "success";
    private static String url_create_user = "http://192.168.1.24:80/DB/db_creation_user.php";
    EditText inputNom, inputPrenom, inputMail, inputPassword;
    JSONParser jsonParser = new JSONParser();
    //private static final String TAG_SERVER = "success";
    private String TAG = "DEBUG / FIREBASE";
    private ProgressDialog pDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                String nom = inputNom.getText().toString();
                String prenom = inputPrenom.getText().toString();
                String mail = inputMail.getText().toString();
                String password = inputMail.getText().toString();
                // new CreateNewUser().execute(nom, prenom, mail, password);
                createAccount(mail, password);


            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String mail, String password) {
        Log.d(TAG, "createAccount:" + mail);
        if (!validateForm()) {
            return;
        }

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(InscriptionActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //   hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
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


    /**
     * Background Async Task to Create new user
     */

    class CreateNewUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InscriptionActivity.this);
            pDialog.setMessage("Création du compte ...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener(){


                @Override
                public void onClick(DialogInterface dialog, int which) {

                 /*   pDialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);*/
                    finish();
                }

            } );
            pDialog.show();
            pDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
            //pDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.INVISIBLE);
        }

        /**
         * Creating user in background thread
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("nom", args[0]);
            params.put("prenom", args[1]);
            params.put("mail", args[2]);
            params.put("password", args[3]);

            // getting JSON Object
            // Note that create product url accepts POST method

                JSONObject json = jsonParser.makeHttpRequest(url_create_user,
                        "POST", params);

            // check log cat fro responseA
            Log.d("Create Response", json.toString());


            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    //finish();
                    return "success";
                } else {
                    System.out.println("failed to create user");
                    return "errorserver";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("failed to create user2");
            }
            return "errorserver";
        }


        protected void onPostExecute(String result) {

            ProgressBar bar = (ProgressBar) pDialog.findViewById(android.R.id.progress);
            Drawable indeterminateDrawable = bar.getIndeterminateDrawable();
            Rect bounds = indeterminateDrawable.getBounds();

            if (result != "errorserver") {
                pDialog.setMessage("Compte créé, Connecte toi !");

               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Drawable drawable = getDrawable(R.drawable.ic_checked);
                    drawable.setBounds(bounds);
                    bar.setIndeterminateDrawable(drawable);
                }
                pDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            } else {
                pDialog.setMessage("Erreur Serveur");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Drawable drawable = getDrawable(R.drawable.ic_error);
                    drawable.setBounds(bounds);
                    bar.setIndeterminateDrawable(drawable);
                }
                pDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            }
        }
    }
}
