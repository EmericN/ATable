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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Button btnConnect;
    private TextInputLayout txtInputLayoutMail, getTxtInputLayoutPass;
    private EditText editTextMail, editTextPass;
    private String TAG = "debug login";
    private FirebaseAuth mAuth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        btnConnect = findViewById(R.id.button_connect);
        txtInputLayoutMail = findViewById(R.id.input_layout_mail);
        getTxtInputLayoutPass =  findViewById(R.id.input_layout_pass);
        editTextMail =  findViewById(R.id.edit_text_mail);
        editTextPass =  findViewById(R.id.edit_text_password);

        mAuth = FirebaseAuth.getInstance();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = editTextMail.getText().toString();
                String password = editTextPass.getText().toString();
                if (!mail.isEmpty() && !password.isEmpty()) {
                    signIn(mail, password);
                } else {
                        if (mail.isEmpty()) {
                            txtInputLayoutMail.setError(getString(R.string.err_msg_email));
                        }
                        if (password.isEmpty()) {
                            getTxtInputLayoutPass.setError(getString(R.string.err_msg_password));
                        }
                  }
            }
        });
    }

    private void signIn(String mail, String password) {
        Log.d(TAG, "signIn:" + mail);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful()) {
                        }
                    }
                });
    }
}
