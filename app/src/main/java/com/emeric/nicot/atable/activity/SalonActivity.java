package com.emeric.nicot.atable.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.adapter.CustomAdapter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SalonActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayloutManager;
    private RecyclerView.Adapter mAdapterChat;
    private String nomSalon, userId, salonId, tag, picUrl;
    private EditText editTextSend;
    private FirebaseFirestore mFirestore;
    private String TAG = "debug salon";
    private CollectionReference collectionRefMessage, collectionRefNotification, collectionRefChat,collectionRefUser;
    private Message message;
    private String userName;
    private BottomSheetDialog mBottomSheetDialog;
    private FrameLayout frameLayoutAdminChoice;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salon_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nomSalon = extras.getString("nomSalon");
            userId = extras.getString("userId");
            tag = extras.getString("tag");
            salonId = extras.getString("salonId");
            userName = extras.getString("userName");
            picUrl = extras.getString("picUrl");
        }
        message = new Message();

        Toolbar mToolbar = findViewById(R.id.toolbarRoom);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(nomSalon);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ImageButton buttonSend = findViewById(R.id.buttonSend);
        ImageButton buttonEmot = findViewById(R.id.buttonEmot);
        editTextSend = findViewById(R.id.editTextSend);
        frameLayoutAdminChoice = findViewById(R.id.frame_layout_admin_choice);
        mRecyclerViewChat = findViewById(R.id.recycler_view_chat);
        mLayloutManager = new LinearLayoutManager(this);
        mRecyclerViewChat.setLayoutManager(mLayloutManager);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapterChat = new CustomAdapterChat(this, message, userId, Glide.with(this));
        mFirestore = FirebaseFirestore.getInstance();
        collectionRefMessage = mFirestore.collection("chats").document(salonId).collection("messages");
        collectionRefNotification = mFirestore.collection("notifications");
        collectionRefChat = mFirestore.collection("chats");
        collectionRefUser = mFirestore.collection("users");

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

                            final ChatMessage newMessage = new ChatMessage();
                            newMessage.text = doc.getDocument().getString("text");
                            newMessage.date = doc.getDocument().getString("date");
                            newMessage.idSender = doc.getDocument().getString("idSender");
                            newMessage.name = doc.getDocument().getString("name");
                            newMessage.emot = doc.getDocument().getString("emot");
                            newMessage.picUrl = doc.getDocument().getString("picUrl");

                            message.getListMessageData().add(newMessage);
                            mLayloutManager.scrollToPosition(message.getListMessageData().size() - 1);
                        }
                            mAdapterChat.notifyDataSetChanged();
                            mRecyclerViewChat.setAdapter(mAdapterChat);
                        }
                });

        if (tag.equals("admin")) {

            buttonEmot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showBottomEmotLayout();
                    if(frameLayoutAdminChoice.getVisibility() != View.VISIBLE){
                        frameLayoutAdminChoice.setVisibility(View.VISIBLE);
                    }else{
                        frameLayoutAdminChoice.setVisibility(View.GONE);
                    }

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

                    Long tsLong = System.currentTimeMillis();
                    Date curDate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String DateToStr = format.format(curDate);

                    newMessage.text = content;
                    newMessage.idSender = userId;
                    newMessage.date = DateToStr;
                    newMessage.name = userName;
                    newMessage.emot = null;
                    newMessage.tsLong = tsLong;
                    newMessage.picUrl = picUrl;

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
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_emot);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SalonActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new CustomAdapter(SalonActivity.this, image, new CustomAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Integer item) {

                ChatMessage newMessage = new ChatMessage();
                Long tsLong = System.currentTimeMillis();
                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String DateToStr = format.format(curDate);

                Map<String, Object> notification = new HashMap<>();
                notification.put("roomID", salonId);
                notification.put("roomName", nomSalon);
                notification.put("userName", userName);
                notification.put("message", userName+" a envoyé un sticker.");
                notification.put("emot", item.toString());

                Map<String, Object> last_message = new HashMap<>();
                last_message.put("last_message", userName+" a envoyé un sticker.");

                newMessage.idSender = userId;
                newMessage.date = DateToStr;
                newMessage.name = userName;
                newMessage.emot = item.toString();
                newMessage.tsLong = tsLong;
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
        Intent i = new Intent(getApplicationContext(), FindUserActivity.class);
        i.putExtra("salonId", salonId);
        i.putExtra("nomSalon", nomSalon);
        i.putExtra("userName", userName);
        startActivity(i);
    }

    private void friendlist() {
        //collectionRefChat
        //TODO dialog return friend list
    }
}




