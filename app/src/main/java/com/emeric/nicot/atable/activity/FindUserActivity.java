package com.emeric.nicot.atable.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.adapter.CustomAdapterFindUser;
import com.emeric.nicot.atable.models.AdapterCallbackFindUser;
import com.emeric.nicot.atable.models.JSONParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FindUserActivity extends AppCompatActivity implements AdapterCallbackFindUser{

    private static final String urlGetUserFind ="http://192.168.1.24/Atable/getUser.php";
    private String TAG = "debug findUser";
    private String nomSalon, salonId, userName;
    private RecyclerView.Adapter mAdapterFindUser;
    private RecyclerView recyclerViewFindUser;
    private RecyclerView.LayoutManager mLayloutManager;
    private ImageButton imageButtonFindUser;
    private EditText editTextFindUser;
    private ArrayList<String> findUserArray;
    private JSONArray charsequenceMatchArray = null;
    private Toolbar mToolbar;
    private FirebaseFirestore mFirestore;
    private CollectionReference collectionRefUser;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friend_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nomSalon = extras.getString("nomSalon");
            salonId = extras.getString("salonId");
            userName = extras.getString("userName");
        }

        mFirestore = FirebaseFirestore.getInstance();
        collectionRefUser = mFirestore.collection("users");
        recyclerViewFindUser = findViewById(R.id.recycler_view_find_user);
        imageButtonFindUser = findViewById(R.id.image_button_find_user);
        editTextFindUser = findViewById(R.id.edit_text_find_user);
        mLayloutManager = new LinearLayoutManager(this);
        recyclerViewFindUser.setLayoutManager(mLayloutManager);
        recyclerViewFindUser.setHasFixedSize(true);
        mToolbar = findViewById(R.id.toolbar_Find_User);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Ajoute une connaissance");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editTextFindUser.setMaxLines(1);
        editTextFindUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().isEmpty()) {
                        new GetUserFind().execute(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imageButtonFindUser.setImageResource(R.drawable.ic_add_24dp);
        imageButtonFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String friend = editTextFindUser.getText().toString();
                Log.d(TAG, "test click listener : "+friend);

                collectionRefUser.whereEqualTo("prenom_nom", friend).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("nom"));

                                Map<String, Object> InviteMap = new HashMap<>();
                                InviteMap.put("idPending", document.getId());
                                InviteMap.put("roomId", salonId);
                                InviteMap.put("roomName", nomSalon);

                                mFirestore.collection("pending").document().set(InviteMap);
                                Toast.makeText(getApplicationContext(),
                                        "Invitation envoyé à " + friend,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "Error getting friend id : ", task.getException());
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onMethodCallbackFindUser(String prenomNom) {
        if(!userName.equals(prenomNom)) {
            editTextFindUser.setText(prenomNom);
        }
    }

    class GetUserFind extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... args) {

            Log.d(TAG, "text input : "+args[0]);
            findUserArray = new ArrayList<>();
            HashMap<String, String> params = new HashMap<>();
            params.put("user", args[0]);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONObject json = jsonParser.makeHttpRequest(urlGetUserFind,
                        "POST", params);

                charsequenceMatchArray = json.getJSONArray("response");

                Log.d(TAG, "charsequence JSON : " + charsequenceMatchArray.toString());
                findUserArray.clear();

                if(charsequenceMatchArray.length() != 0) {
                    for (int i = 0; i < charsequenceMatchArray.length(); i++) {
                        String c = charsequenceMatchArray.getString(i);
                        findUserArray.add(c);
                        Log.d(TAG, "array find user : " + findUserArray.get(i));
                    }
                }else{
                    findUserArray.add("Utilisateur introuvable");
                }
                mAdapterFindUser = new CustomAdapterFindUser(FindUserActivity.this, findUserArray, FindUserActivity.this);
            }

        catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "error JSON : "+e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerViewFindUser.setAdapter(mAdapterFindUser);
        }
    }
}
