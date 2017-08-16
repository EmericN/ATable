package com.emeric.nicot.atable;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Nicot Emeric on 05/06/2017.
 */

public class InscriptionActivity extends AppCompatActivity {

    EditText inputNom, inputPrenom, inputMail, inputPassword;
    JSONParser jsonParser = new JSONParser();
    private static String url_create_user = "http://192.168.1.24:80/DB/db_creation_user.php";
    private static final String TAG_SUCCESS = "success";
    //private static final String TAG_SERVER = "success";
    private ProgressDialog pDialog;


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
                String password = inputPassword.getText().toString();

                new CreateNewUser().execute(nom, prenom, mail, password);

            }
        });
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

                    pDialog.dismiss();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
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

            // check log cat fro response
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

        /**
         * After completing background task Dismiss the progress dialog
         **/
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
