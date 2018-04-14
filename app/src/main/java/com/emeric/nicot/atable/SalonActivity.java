package com.emeric.nicot.atable;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emeric.nicot.atable.adapter.CustomAdapter;
import com.emeric.nicot.atable.adapter.CustomAdapterChat;
import com.emeric.nicot.atable.models.ChatMessage;
import com.emeric.nicot.atable.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class SalonActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayloutManager2;
    private RecyclerView.Adapter mAdapterChat;
    private String nomSalon, ts, userId, salonId, tag;
    private EditText editTextSend;
    private FirebaseFirestore mFirestore;
    private String TAG = "debug add friend";
    private CollectionReference collectionRefMessage, collectionRefNotification, collectionRefChat, collectionRefUser;
    private Message message;
    private String Date, userName;
    private BottomSheetDialog mBottomSheetDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nomSalon = extras.getString("NomSalon");
            userId = extras.getString("userId");
            tag = extras.getString("tag");
            salonId = extras.getString("SalonId");
            userName = extras.getString("userName");
        }

        Toolbar mToolbar = findViewById(R.id.toolbarRoom);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(nomSalon);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        message = new Message();
        final Long tsLong = System.currentTimeMillis();
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm");
        Date = simpledateformat.format(calander.getTime());
        ImageButton buttonSend = findViewById(R.id.buttonSend);
        ImageButton buttonEmot = findViewById(R.id.buttonEmot);
        editTextSend = findViewById(R.id.editTextSend);
        mRecyclerViewChat = findViewById(R.id.recycler_view_chat);
        mLayloutManager2 = new LinearLayoutManager(this);
        mRecyclerViewChat.setLayoutManager(mLayloutManager2);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapterChat = new CustomAdapterChat(this, message, userId);
        mFirestore = FirebaseFirestore.getInstance();
        collectionRefMessage = mFirestore.collection("chats").document(salonId).collection("messages");
        collectionRefNotification = mFirestore.collection("notifications");
        collectionRefChat = mFirestore.collection("chats");
        collectionRefUser = mFirestore.collection("users");
        DocumentReference docRefChat = mFirestore.collection("chats").document(salonId);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mFirestore.collection("chats").document(salonId).collection("messages")
                .orderBy("tsLong", Query.Direction.ASCENDING)
                .limit(20)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange doc : value.getDocumentChanges()) {

                            ChatMessage newMessage = new ChatMessage();
                            Log.d(TAG, "Message : " + doc.getDocument().getString("text"));
                            Log.d(TAG, "Timestamp : " + doc.getDocument().getString("timestamp"));
                            newMessage.text = doc.getDocument().getString("text");
                            newMessage.timestamp = doc.getDocument().getString("timestamp");
                            newMessage.idSender = doc.getDocument().getString("idSender");
                            newMessage.name = doc.getDocument().getString("name");
                            newMessage.emot = doc.getDocument().getString("emot");

                            message.getListMessageData().add(newMessage);
                            mLayloutManager2.scrollToPosition(
                                    message.getListMessageData().size() - 1);
                        }
                            mAdapterChat.notifyDataSetChanged();
                            mRecyclerViewChat.setAdapter(mAdapterChat);
                        }
                });

            //TODO Bug quand le recyclervier est vide ...
          /*  mRecyclerViewChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                           int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        mRecyclerViewChat.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerViewChat.smoothScrollToPosition(
                                        mRecyclerViewChat.getAdapter().getItemCount() -
                                        1);
                            }
                        }, 100);
                    }
                }
            });*/


        /*mFirestore.collection("chats").document(salonId).collection("messages")
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
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                        mRecyclerViewChat.setAdapter(mAdapter2);
                    }
                });*/

        if (tag.equals("admin")) {

            buttonEmot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showBottomEmotLayout();
                }
            });
        } else {
            invalidateOptionsMenu();
            buttonEmot.setClickable(false);
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editTextSend.getText().toString();
                if (content.length() > 0) {
                    editTextSend.setText("");
                    ChatMessage newMessage = new ChatMessage();

                    Map<String, Object> notification = new HashMap<>();
                    notification.put("roomID", salonId);
                    notification.put("roomName", nomSalon);
                    notification.put("userName", userName);
                    notification.put("message", newMessage.text = content);

                    Map<String, Object> last_message = new HashMap<>();
                    last_message.put("last_message", newMessage.text = content);

                    newMessage.text = content;
                    newMessage.idSender = userId;
                    newMessage.timestamp = Date;
                    newMessage.name = userName;
                    newMessage.emot = null;
                    newMessage.tsLong = tsLong;
                    collectionRefMessage.document().set(newMessage);
                    collectionRefNotification.document().set(notification);
                    collectionRefChat.document(salonId).update(last_message);
                }
            }
        });
    }

    private void showBottomEmotLayout() {

        Integer [] image = {R.drawable.emotatable, R.drawable.sticker2};

        mBottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.emot_layout, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_emot);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new CustomAdapter(SalonActivity.this, image, new CustomAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Integer item) {

                ChatMessage newMessage = new ChatMessage();

                Map<String, Object> notification = new HashMap<>();
                notification.put("roomID", salonId);
                notification.put("roomName", nomSalon);
                notification.put("userName", userName);
                notification.put("message", userName+" a envoyé un sticker.");
                notification.put("emot", item.toString());

                Map<String, Object> last_message = new HashMap<>();
                last_message.put("last_message", userName+" a envoyé un sticker.");

                newMessage.idSender = userId;
                newMessage.timestamp = Date;
                newMessage.name = userName;
                newMessage.emot = item.toString();
                collectionRefMessage.document().set(newMessage);
                collectionRefNotification.document().set(notification);
                collectionRefChat.document(salonId).update(last_message);
                mBottomSheetDialog.dismiss();
            }
        }));
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
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
                friendlist();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addFriend() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setTitle("Ajouter une personne :");
        alert.setView(edittext);
        alert.setPositiveButton("Ajouter", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String friend = edittext.getText().toString();
                String[] separate = friend.split(" ");
                Log.d(TAG, separate[0] + "_" + separate[1]);

                collectionRefUser.whereEqualTo("nom_prenom", separate[0] + "_" + separate[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("nom"));

                                Map<String, Object> InviteMap = new HashMap<>();
                                InviteMap.put("idPending", document.getId());
                                InviteMap.put("roomId", salonId);
                                InviteMap.put("roomName", nomSalon);

                                mFirestore.collection("pending").document().set(InviteMap);
                                Toast.makeText(getApplicationContext(), "Invitation envoyé à "+friend,
                                        Toast.LENGTH_LONG).show();
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

    private void friendlist() {
        //collectionRefChat
        //TODO dialog return friend list

    }

}




