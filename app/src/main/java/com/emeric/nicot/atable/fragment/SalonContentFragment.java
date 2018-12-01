package com.emeric.nicot.atable.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.activity.SalonActivity;
import com.emeric.nicot.atable.adapter.CustomAdapter;
import com.emeric.nicot.atable.adapter.CustomAdapterRoom;
import com.emeric.nicot.atable.models.AdapterCallbackRoom;
import com.emeric.nicot.atable.models.ChatMessage;
import com.emeric.nicot.atable.models.FirebaseSalon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SalonContentFragment extends Fragment implements AdapterCallbackRoom {

    public ArrayList<FirebaseSalon> salonAdmin, salonMembre, salonIdAdmin;
    private FirebaseAuth mAuth;
    private String userId, userName, picUrl;
    private String TAG = "debug firestore";
    private Long tsLong;
    private String date;
    private FirebaseFirestore mFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DocumentReference docRef;
    private CollectionReference CollRef;
    private BottomSheetDialog mBottomSheetDialog;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerViewRoom;
    private RecyclerView.LayoutManager mLayloutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.tab_salon_list, null);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        salonAdmin = new ArrayList<>();
        salonIdAdmin = new ArrayList<>();
        salonMembre = new ArrayList<>();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, HH:mm");
        date = simpledateformat.format(calander.getTime());
        CollRef = mFirestore.collection("chats");
        mLayloutManager = new LinearLayoutManager(getContext());
        mRecyclerViewRoom = v.findViewById(R.id.recycler_view_room);
        mRecyclerViewRoom.setLayoutManager(mLayloutManager);
        mRecyclerViewRoom.setHasFixedSize(true);
        mAdapter = new CustomAdapterRoom(this, salonAdmin, salonMembre,
                this);
        mRecyclerViewRoom.setAdapter(mAdapter);

        FloatingActionButton floatAddSalon = v.findViewById(R.id.FloatButtonAdd);
        swipeRefreshLayout = v.findViewById(R.id.swiperefreshRooms);


        if (currentUser != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();
            refreshRooms();

        } else {
        }

        // Refresh list of rooms
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRooms();
            }
        });

        floatAddSalon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                alert.setTitle("Nom du salon");
                alert.setView(edittext);
                alert.setPositiveButton("Créer", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nomsalon = edittext.getText().toString();
                        docRef = mFirestore.collection("chats").document();
                        Map<String, Object> chatsMap = new HashMap<>();
                        chatsMap.put("nom", nomsalon);
                        chatsMap.put("admin", userId);
                        chatsMap.put("created_at", tsLong);
                        chatsMap.put("members", "");

                        docRef.set(chatsMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                docRef.collection("messages").document();
                                refreshRooms();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Error getting rooms after create one : ",
                                                e.getCause());
                                    }
                                });

                        dialog.dismiss();
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

        return v;
    }

    public void refreshRooms() {
        salonMembre.clear();
        salonAdmin.clear();

        mFirestore.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        userName = document.getString("prenom_nom");
                        picUrl = document.getString("picUrl");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        mFirestore.collection("chats").whereEqualTo("admin", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null && document.exists()) {
                            Log.d(TAG, "ID ADMIN : " + document.getId() + " => Admin : " +
                                       document.get("nom"));
                            String salonAdm = (String) document.get("nom");
                            String salonIdAdm = document.getId();
                            String salonLastMessageAdm = (String) document.get("last_message");
                            FirebaseSalon addedSalonAdmin = new FirebaseSalon(salonAdm, salonIdAdm,
                                    salonLastMessageAdm);
                            salonAdmin.add(addedSalonAdmin);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "No admin rooms : ", task.getException());
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting admin rooms : ", task.getException());
                }
            }
        });

        mFirestore.collection("chats").whereEqualTo("members."+userId, true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null && document.exists()) {
                            Log.d(TAG, "ID MEMBRE : " + document.getId() + " => Membre : " +
                                       document.get("nom"));
                            String salonMemb = (String) document.get("nom");
                            String salonIdMemb = document.getId();
                            String salonLastMessageMemb = (String) document.get("last_message");
                            FirebaseSalon addedSalonMembre = new FirebaseSalon(salonMemb,
                                    salonIdMemb, salonLastMessageMemb);
                            salonMembre.add(addedSalonMembre);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "No member rooms : ", task.getException());
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting member rooms : ", task.getException());
                }
            }
        });

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onMethodCallbackQuickSticker(final String nomSalon, final String salonId) {

        Integer[] image = {R.drawable.emotatable, R.drawable.sticker2};

        mBottomSheetDialog = new BottomSheetDialog(getContext());
        View view = getLayoutInflater().inflate(R.layout.emot_layout, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_emot);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(new CustomAdapter(getContext(), image, new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Integer item) {

                ChatMessage newMessage = new ChatMessage();

                Map<String, Object> notification = new HashMap<>();
                notification.put("roomID", salonId);
                notification.put("roomName", nomSalon);
                notification.put("userName", userName);
                notification.put("message", userName + " a envoyé un sticker.");
                notification.put("emot", item.toString());

                Map<String, Object> last_message = new HashMap<>();
                last_message.put("last_message", userName + " a envoyé un sticker.");

                newMessage.idSender = userId;
                newMessage.date = date;
                newMessage.name = userName;
                newMessage.emot = item.toString();
                mFirestore.collection("chats").document(salonId).collection("messages")
                        .document().set(newMessage);
                mFirestore.collection("notifications").document().set(notification);
                mFirestore.collection("chats").document(salonId).update(last_message);
                mBottomSheetDialog.dismiss();
                // TODO looking for adding persistent listener for chat preview : adapter.notifyDataSetChanged();
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

    @Override
    public void onMethodCallbackEnterRoom(ArrayList<FirebaseSalon> salon, int position) {
        Intent i = new Intent(getContext(), SalonActivity.class);
        FirebaseSalon PosSalon = salon.get(position);
        i.putExtra("nomSalon", PosSalon.getSalon());
        i.putExtra("salonId", PosSalon.getSalonId());
        i.putExtra("userId", userId);
        i.putExtra("userName", userName);
        i.putExtra("picUrl", picUrl);

        if (position < salon.size() - salonMembre.size()) {
            i.putExtra("tag", "admin");
        } else {
            i.putExtra("tag", "member");
        }
        startActivity(i);
    }
}