package com.emeric.nicot.atable;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.emeric.nicot.atable.adapter.CustomAdapterChat;
import com.emeric.nicot.atable.models.ChatMessage;
import com.emeric.nicot.atable.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class SalonActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView, mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayoutManager, mLayloutManager2;
    private RecyclerView.Adapter mAdapter, mAdapter2;
    private ArrayList<String> Ordre, listtest;
    private ArrayList<Integer> Image;
    private String mail, nomSalon, ts, userId, salonId, tag;
    private ArrayAdapter<String> adapter;
    private EditText editTextSend;
    private TextView textV1;
    private ImageButton buttonSend;
    private ListView listViewChat;
    private ProgressDialog pDialog;
    private FirebaseFirestore mFirestore;
    private String TAG = "debug add friend";
    private CollectionReference collectionRefMessage, collectionRefNotification;
    private Message message;
    private Calendar calander;
    private SimpleDateFormat simpledateformat;
    private String Date, userName;


    @SuppressLint("SimpleDateFormat")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nomSalon = extras.getString("NomSalon");
            userId = extras.getString("userId");
            tag = extras.getString("tag");
            salonId = extras.getString("SalonId");
            userName = extras.getString("userName");
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarRoom);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(nomSalon);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        message = new Message();
        calander = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("HH:mm");
        Date = simpledateformat.format(calander.getTime());
        buttonSend = (ImageButton) findViewById(R.id.buttonSend);
        editTextSend = (EditText) findViewById(R.id.editTextSend);
        mRecyclerViewChat = (RecyclerView) findViewById(R.id.recycler_view_chat);
        mLayloutManager2 = new LinearLayoutManager(this);
        mRecyclerViewChat.setLayoutManager(mLayloutManager2);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapter2 = new CustomAdapterChat(this, message, userId);
        mFirestore = FirebaseFirestore.getInstance();
        collectionRefMessage = mFirestore.collection("chats").document(salonId).collection("messages");
        collectionRefNotification = mFirestore.collection("notifications");


        mFirestore.collection("chats").document(salonId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirebaseFirestoreException e) {
                        for (DocumentChange doc : snapshots.getDocumentChanges()) {
                            ChatMessage newMessage = new ChatMessage();
                            switch (doc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "text : " + doc.getDocument().getString("text"));
                                    newMessage.text = doc.getDocument().getString("text");
                                    newMessage.timestamp = doc.getDocument().getString("timestamp");
                                    newMessage.idSender = doc.getDocument().getString("idSender");
                                    newMessage.name = doc.getDocument().getString("name");
                                    message.getListMessageData().add(newMessage);
                                    mAdapter2.notifyDataSetChanged();
                                    mLayloutManager2.scrollToPosition(
                                            message.getListMessageData().size() - 1);
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "" + doc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "" + doc.getDocument().getData());
                                    break;
                            }
                        }
                        mRecyclerViewChat.setAdapter(mAdapter2);
                    }
                });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextSend.getText().toString();
                if (content.length() > 0) {
                    editTextSend.setText("");
                    ChatMessage newMessage = new ChatMessage();

                    Map notification = new HashMap<>();
                    notification.put("roomID", salonId);
                    notification.put("roomName", nomSalon);
                    notification.put("userName", userName);
                    notification.put("message", newMessage.text = content);

                    newMessage.text = content;
                    newMessage.idSender = userId;
                    newMessage.timestamp = Date;
                    newMessage.name = userName;
                    collectionRefMessage.document().set(newMessage);
                    collectionRefNotification.document().set(notification);
                }
            }
        });


        if (tag.equals("admin")) {

      /*     Drawable person_add = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_person_add);
            mToolbar.setOverflowIcon(person_add);*/

            /*Ordre = new ArrayList<>(Arrays.asList("A Table !", "Range ta chambre !", "Réveil toi !", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3", "Test3"));
            Image = new ArrayList<>(Arrays.asList(R.drawable.ic_bubble, R.drawable.ic_checked));
            // Calling the RecyclerView
            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            // The number of Columns
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new CustomAdapter(SalonActivity.this, Ordre, Image);
            mRecyclerView.setAdapter(mAdapter);*/
        } else {
            invalidateOptionsMenu();

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_salon, menu);

        if (tag.equals("member")) {
            menu.findItem(R.id.action_addFriend).setVisible(false);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_addFriend:
                addFriend();
                return true;

            case R.id.action_settings:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFriend() {
        final CollectionReference docRefChat = mFirestore.collection("chats");
        final CollectionReference docRefFriend = mFirestore.collection("users");

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
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


}




