package com.emeric.nicot.atable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        session = new SessionManagement(getApplicationContext());

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
                    new CheckUser().execute(mail, password);

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
