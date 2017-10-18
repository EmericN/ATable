package com.emeric.nicot.atable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.emeric.nicot.atable.models.SalonIdModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Nicot Emeric on 05/07/2017.
 */

public class SalonActivity extends Activity {

    private static final String TAG_SUCCESS = "success";
    private static String url_invitation = "http://192.168.1.24:80/DB/db_invitation.php";
    private RecyclerView mRecyclerView, mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter, mAdapter2;
    private ArrayList<String> Ordre, listtest;
    private ArrayList<Integer> Image;
    private String mail, nomSalon, ts, userId;
    private Integer tag;
    private ArrayAdapter<String> adapter;
    private ArrayList<MessageChat> ListMessage = new ArrayList<>();
    private EditText editTextSend;
    private TextView textV1;
    private Button buttonSend;
    private ListView listViewChat;
    private ProgressDialog pDialog;
    private FirebaseDatabase database;
    private DatabaseReference myRefRelationship, myRefUserId, myRefChats;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon);

        database = FirebaseDatabase.getInstance();
        myRefRelationship = database.getReference("relationship");
        myRefUserId = database.getReference("users");
        myRefChats = database.getReference("chats");

        Bundle extras = getIntent().getExtras();
        nomSalon = extras.getString("NomSalon");
        userId = extras.getString("userId");
        tag = extras.getInt("tag");

        textV1 = (TextView) findViewById(R.id.textViewSalon);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        editTextSend = (EditText) findViewById(R.id.editTextSend);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        LinearLayoutManager mLayloutManager2 = new LinearLayoutManager(this);
        mLayloutManager2.setStackFromEnd(true);
        mRecyclerViewChat.setLayoutManager(mLayloutManager2);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapter2 = new CustomAdapterChat(getApplicationContext(), ListMessage);
        mRecyclerViewChat.setAdapter(mAdapter2);

        textV1.setText(nomSalon);

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

                        // new AddFriend().execute(friend, mail, NomSalon);
                        myRefChats.orderByChild("title").equalTo(nomSalon).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String idSalon = postSnapshot.getKey();

                                    SalonIdModel salonIdModel2 = new SalonIdModel(idSalon);
                                    listtest.add(salonIdModel2.GetSalonId());
                                    Log.d("idSalon : ", salonIdModel2.GetSalonId());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        myRefUserId.orderByChild("nomPrenom").equalTo(separate[0] + "_" +
                                                                      separate[1]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String userIdInvite = postSnapshot.getKey();
                                    Log.d("USERIDINVITE : ", userIdInvite);
                                    myRefRelationship.child(listtest.get(0)).child(userIdInvite).setValue("membre");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        //myRefRelationship.child(ts).child(userId).setValue("membre");
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


        if (tag == 1) {


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




