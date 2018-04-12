package com.emeric.nicot.atable;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.emeric.nicot.atable.adapter.CustomAdapterNotif;
import com.emeric.nicot.atable.models.AdapterCallback;
import com.emeric.nicot.atable.models.FirebaseSalonRequest;
import com.emeric.nicot.atable.models.Members;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity  implements AdapterCallback {

    private RecyclerView mRecyclerViewChat;
    private RecyclerView.LayoutManager mLayloutManager;
    private RecyclerView.Adapter mAdapter;
    private String TAG = "debug notif";
    private String userId;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private ArrayList<FirebaseSalonRequest> salonRequest;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Members existingMembers;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);

        Toolbar mToolbar = findViewById(R.id.toolbarNotif);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Notifications");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("userId");
        }
        salonRequest = new ArrayList<>();
        mRecyclerViewChat = findViewById(R.id.recycler_view_notif);
        mLayloutManager = new LinearLayoutManager(this);
        mRecyclerViewChat.setLayoutManager(mLayloutManager);
        mRecyclerViewChat.setHasFixedSize(true);
        mAdapter = new CustomAdapterNotif(this, salonRequest,this);
        mRecyclerViewChat.setAdapter(mAdapter);
        mFirestore = FirebaseFirestore.getInstance();
        mSwipeRefreshLayout = findViewById(R.id.swiperefreshNotif);

        RefreshRequest();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRequest();
            }
        });
    }

    private void RefreshRequest() {
        CollectionReference colRef = mFirestore.collection("pending");
        colRef.whereEqualTo("idPending", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.get("roomName") + " => " + document.get("roomId"));
                        String salonAdm = (String) document.get("roomName");
                        String salonIdAdm = (String) document.get("roomId");
                        String idDoc = document.getId();

                        FirebaseSalonRequest addedSalonAdmin = new FirebaseSalonRequest(salonAdm, salonIdAdm, idDoc);

                        salonRequest.add(addedSalonAdmin);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting friend request rooms : ", task.getException());
                }
            }
        });

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void onMethodCallback(String NomSalon, final String salonId, final String idDoc) {
        //Firestore don't implement update method on Object so I get all existing members and I add the new one
        Log.d(TAG, "Salon accepté : " + NomSalon);
        mFirestore.collection("chats").document(salonId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "GET DATA FROM DB CHATS : " + document.get("members"));
                        existingMembers = document.toObject(Members.class);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

         Map<String, Object> allMembers = new HashMap<>();
         HashMap<String,Boolean> members = existingMembers.getMembers();
         members.put(userId,true);
         allMembers.put("members",members);

         mFirestore.collection("chats").document(salonId).update(allMembers).addOnSuccessListener(new OnSuccessListener<Void>() {
              @Override
              public void onSuccess(Void aVoid) {
                  Log.d(TAG, "Members added");
                  mFirestore.collection("pending").document(idDoc).delete(); //Delete pending row
                  Toast.makeText(NotificationActivity.this, "Invitation acceptée",
                          Toast.LENGTH_LONG).show();
              }
         });

        FirebaseMessaging.getInstance().subscribeToTopic(salonId);
        mAdapter.notifyDataSetChanged();
    }
}
