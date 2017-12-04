package com.emeric.nicot.atable;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.emeric.nicot.atable.adapter.CustomAdapter;
import com.emeric.nicot.atable.adapter.CustomAdapterChat;
import com.emeric.nicot.atable.models.MessageChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;


public class SalonActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView, mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter, mAdapter2;
    private ArrayList<String> Ordre, listtest;
    private ArrayList<Integer> Image;
    private String mail, nomSalon, ts, userId, salonId, tag;
    private ArrayAdapter<String> adapter;
    private ArrayList<MessageChat> ListMessage = new ArrayList<>();
    private EditText editTextSend;
    private TextView textV1;
    private Button buttonSend;
    private ListView listViewChat;
    private ProgressDialog pDialog;
    private FirebaseFirestore mFirestore;
    private String TAG = "debug add friend";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarRoom);
        setSupportActionBar(mToolbar);


        Bundle extras = getIntent().getExtras();
        nomSalon = extras.getString("NomSalon");
        userId = extras.getString("userId");
        tag = extras.getString("tag");
        salonId = extras.getString("SalonId");

        buttonSend = (Button) findViewById(R.id.buttonSend);
        editTextSend = (EditText) findViewById(R.id.editTextSend);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        LinearLayoutManager mLayloutManager2 = new LinearLayoutManager(this);
        mLayloutManager2.setStackFromEnd(true);
        mRecyclerViewChat.setLayoutManager(mLayloutManager2);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapter2 = new CustomAdapterChat(getApplicationContext(), ListMessage);
        mRecyclerViewChat.setAdapter(mAdapter2);
        mFirestore = FirebaseFirestore.getInstance();
        final CollectionReference docRefChat = mFirestore.collection("chats");
        final CollectionReference docRefFriend = mFirestore.collection("users");
        mToolbar.setTitle(nomSalon);
        // actionBar.setIcon(R.drawable.ic_crown); TODO à regarder

        FloatingActionButton floatAddFriend = (FloatingActionButton) findViewById(R.id.floatingActionButtonFriend);

        floatAddFriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                alert.setTitle("Ajouter une personne :");
                alert.setView(edittext);
                alert.setPositiveButton("Ajouter", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String friend = edittext.getText().toString();
                        String[] separate = friend.split(" ");
                        Log.d(TAG, separate[0] + "_" + separate[1]);

                        docRefFriend.whereEqualTo("nom_prenom", separate[0] + "_" + separate[1])
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.get("nom"));
                                        docRefChat.document(salonId).update("pending", document.getId());
                                    }
                                } else {
                                    Log.d(TAG, "Error getting friend id : ", task.getException());
                                }
                            }
                        });


                    }
                });

                alert.setNegativeButton("Quitter", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                edittext.setLayoutParams(lp);
                alert.setView(edittext);
                final AlertDialog dialog = alert.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(false);

                edittext.addTextChangedListener(new TextWatcher() {
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                  int after) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });
            }
        });


        if (tag.equals("admin")) {


            Ordre = new ArrayList<>(Arrays.asList("A Table !", "Range ta chambre !", "Réveil toi !", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3"));
            Image = new ArrayList<>(Arrays.asList(R.drawable.ic_bubble, R.drawable.ic_checked));
            // Calling the RecyclerView
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            // The number of Columns
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new CustomAdapter(SalonActivity.this, Ordre, Image);
            mRecyclerView.setAdapter(mAdapter);
        } else {

            floatAddFriend.setVisibility(View.INVISIBLE);
            floatAddFriend.hide();
        }
    }
}




