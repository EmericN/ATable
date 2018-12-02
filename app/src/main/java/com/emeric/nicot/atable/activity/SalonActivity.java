package com.emeric.nicot.atable.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.adapter.CustomAdapter;
import com.emeric.nicot.atable.adapter.CustomAdapterChat;
import com.emeric.nicot.atable.models.ChatMessage;
import com.emeric.nicot.atable.models.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SalonActivity extends AppCompatActivity {

    private static String TAG2= "debug salon";
    private BottomSheetDialog mBottomSheetDialog;
    private CollectionReference mCollectionRefNotification;
    private CollectionReference mCollectionRefChat;
    private CollectionReference mCollectionRefMessage;
    private EditText mEditTextSend;
    private FrameLayout mFrameLayoutAdminChoice;
    private Message message;
    private RecyclerView.Adapter mAdapterChat;
    private RecyclerView.LayoutManager mLayloutManager;
    private RecyclerView mRecyclerViewChat;
    private String userName;
    private String TAG = "debug salon";
    private String nomSalon, userId, salonId, tag, picUrl;


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

        Toolbar mToolbar = findViewById(R.id.toolbar_room);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(nomSalon);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ImageButton mButtonSend = findViewById(R.id.button_send);
        ImageButton mButtonEmot = findViewById(R.id.button_emot);
        Button mButtonCamera = findViewById(R.id.button_camera);
        Button mButtonSticker = findViewById(R.id.button_sticker);
        mEditTextSend = findViewById(R.id.editText_send);
        mFrameLayoutAdminChoice = findViewById(R.id.frame_layout_admin_choice);
        mRecyclerViewChat = findViewById(R.id.recycler_view_chat);
        mLayloutManager = new LinearLayoutManager(this);
        /*((LinearLayoutManager) mLayloutManager).setReverseLayout(true);
        ((LinearLayoutManager) mLayloutManager).setStackFromEnd(true);*/
        mRecyclerViewChat.setLayoutManager(mLayloutManager);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapterChat = new CustomAdapterChat(this, message, userId, Glide.with(this));
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mCollectionRefMessage = mFirestore.collection("chats").document(salonId).collection("messages");
        mCollectionRefNotification = mFirestore.collection("notifications");
        mCollectionRefChat = mFirestore.collection("chats");
        CollectionReference collectionRefUser = mFirestore.collection("users");

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mFirestore.collection("chats").document(salonId).collection("messages")
                .orderBy("tsLong", Query.Direction.ASCENDING)
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
                            newMessage.picture = doc.getDocument().getString("picture");
                            newMessage.picUrl = doc.getDocument().getString("picUrl");

                            message.getListMessageData().add(newMessage);
                        }
                            mLayloutManager.scrollToPosition(message.getListMessageData().size() -1);
                            mAdapterChat.notifyDataSetChanged();
                            mRecyclerViewChat.setAdapter(mAdapterChat);
                        }
                });

        mRecyclerViewChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4,
                                       int i5, int i6, int i7) {
                if ( i3 < i7) {
                    mRecyclerViewChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerViewChat.scrollToPosition(message.getListMessageData().size() -1);
                        }
                    }, 0);
                }
            }
        });

        if (tag.equals("admin")) {

            mButtonEmot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mFrameLayoutAdminChoice.getVisibility() != View.VISIBLE){
                        if (v != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                        }
                        mFrameLayoutAdminChoice.setVisibility(View.VISIBLE);
                    }else{
                        mFrameLayoutAdminChoice.setVisibility(View.GONE);
                    }

                }
            });
        } else {
            invalidateOptionsMenu();
            mButtonEmot.setClickable(false);
        }

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEditTextSend.getText().toString();
                if (content.length() > 0) {
                    mEditTextSend.setText("");

                    Long tsLong = System.currentTimeMillis();
                    Date curDate = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    String dateToStr = format.format(curDate);

                    ChatMessage newMessage = new ChatMessage();

                    Map<String, Object> last_message = new HashMap<>();
                    last_message.put("last_message", newMessage.text = content);
                    last_message.put("created_at", newMessage.tsLong = tsLong);

                    newMessage.text = content;
                    newMessage.idSender = userId;
                    newMessage.date = dateToStr;
                    newMessage.name = userName;
                    newMessage.emot = null;
                    newMessage.picture = null;
                    newMessage.tsLong = tsLong;
                    newMessage.picUrl = picUrl;

                    if(true) {
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("roomID", salonId);
                        notification.put("roomName", nomSalon);
                        notification.put("userName", userName);
                        notification.put("message", newMessage.text = content);

                        mCollectionRefNotification.document().set(notification);
                    }

                    mCollectionRefMessage.document().set(newMessage);
                    mCollectionRefChat.document(salonId).update(last_message);
                }
            }
        });

        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFrameLayoutAdminChoice.setVisibility(View.GONE);

                if(checkCameraHardware(SalonActivity.this)){
                    Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                    i.putExtra("nomSalon", nomSalon);
                    i.putExtra("salonId", salonId);
                    i.putExtra("userId", userId);
                    i.putExtra("userName", userName);
                    i.putExtra("picUrl", picUrl);
                    startActivity(i);
                }else{
                    Log.d(TAG, "debug camera error hardware");
                }
            }
        });

        mButtonSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomEmotLayout();
                mFrameLayoutAdminChoice.setVisibility(View.GONE);
            }
        });
    }

    private void showBottomEmotLayout() {
        Integer [] image = {R.drawable.emotatable, R.drawable.sticker2};

        mBottomSheetDialog = new BottomSheetDialog(SalonActivity.this);
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
                mCollectionRefMessage.document().set(newMessage);
                mCollectionRefNotification.document().set(notification);
                mCollectionRefChat.document(salonId).update(last_message);
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
        //mCollectionRefChat
        //TODO dialog return friend list
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}




