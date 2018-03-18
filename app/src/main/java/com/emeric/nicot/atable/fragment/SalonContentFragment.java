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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.SalonActivity;
import com.emeric.nicot.atable.adapter.CustomAdapter;
import com.emeric.nicot.atable.adapter.CustomAdapterSalon;
import com.emeric.nicot.atable.models.ChatMessage;
import com.emeric.nicot.atable.models.FirebaseSalonAdmin;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SalonContentFragment extends Fragment {

    public ArrayList<FirebaseSalonAdmin> salon,salonAdmin,salonMembre,salonIdAdmin,salonIdMembre;
    CustomAdapterSalon adapter;
    private ListView LV;
    private FirebaseAuth mAuth;
    private String userId;
    private String userName;
    private String TAG = "debug firestore";
    private Long tsLong;
    private String date;
    private FirebaseFirestore mFirestore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DocumentReference docRef;
    private CollectionReference CollRef;
    private BottomSheetDialog mBottomSheetDialog;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        salon = new ArrayList<>();
        salonAdmin = new ArrayList<>();
        salonIdAdmin = new ArrayList<>();
        salonMembre = new ArrayList<>();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE, HH:mm");
        date = simpledateformat.format(calander.getTime());
        docRef = mFirestore.collection("chats").document();
        CollRef = mFirestore.collection("chats");

        View v = inflater.inflate(R.layout.tab_salon_list, null);
        FloatingActionButton floatAdd = (FloatingActionButton) v.findViewById(R.id.FloatButtonAdd);
        LV = (ListView) v.findViewById(R.id.ListView);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefreshRooms);

        if (currentUser != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();

        } else {
        }

        // Refresh list of rooms
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRooms();
            }
        });

        refreshRooms();

        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getContext(), SalonActivity.class);
                FirebaseSalonAdmin PossalonAdmin = salon.get(position);
                i.putExtra("NomSalon", PossalonAdmin.getSalon());
                i.putExtra("SalonId", PossalonAdmin.getSalonId());
                i.putExtra("userId", userId);
                i.putExtra("userName", userName);

                if (position < salon.size()-salonMembre.size()) {
                    i.putExtra("tag", "admin");
                } else {
                    i.putExtra("tag", "member");
                }
                startActivity(i);
            }
        });

        floatAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                alert.setTitle("Nom du salon");
                alert.setView(edittext);
                alert.setPositiveButton("Créer", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String nomsalon = edittext.getText().toString();

                        Map<String, Object> chatsMap = new HashMap<>();
                        chatsMap.put("nom", nomsalon);
                        chatsMap.put("admin", userId);
                        chatsMap.put("created_at", tsLong);

                        docRef.set(chatsMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                docRef.collection("messages").document();
                                salon.clear();
                                salonMembre.clear();
                                salonAdmin.clear();
                                refreshRooms();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Error getting rooms after create one : ", e.getCause());
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


        salon.clear();
        salonMembre.clear();
        salonAdmin.clear();
        adapter = new CustomAdapterSalon(getContext(), R.layout.list_item_salon, salon, salonAdmin, salonMembre, new CustomAdapterSalon.OnClickListener() {
            @Override
            public void onClick(final String salonId, final String nomSalon) {
                
                Integer [] image = {R.drawable.emotatable, R.drawable.sticker2};

                mBottomSheetDialog = new BottomSheetDialog(getContext());
                View view = getLayoutInflater().inflate(R.layout.emot_layout, null);
                RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_emot);
                recyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(new CustomAdapter(getContext(), image, new CustomAdapter.OnItemClickListener(){
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
                        newMessage.timestamp = date;
                        newMessage.name = userName;
                        newMessage.emot = item.toString();
                        mFirestore.collection("chats").document(salonId).collection("messages").document().set(newMessage);
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

        });
        LV.setAdapter(adapter);

        mFirestore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        userName = document.getString("prenom");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        mFirestore.collection("chats").whereEqualTo("admin", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document != null && document.exists()) {
                            Log.d(TAG, document.getId() + " => Admin : " + document.get("nom"));
                            String salonAdm = (String) document.get("nom");
                            String salonIdAdm = document.getId();
                            String salonLastMessageAdm = (String) document.get("last_message");
                            FirebaseSalonAdmin addedSalonAdmin = new FirebaseSalonAdmin(salonAdm, salonIdAdm, salonLastMessageAdm);
                            salonAdmin.add(addedSalonAdmin);
                            adapter.notifyDataSetChanged();
                        }else {
                            Log.d(TAG, "No admin rooms : ", task.getException());
                        }
                    }
                    salon.addAll(salonAdmin);
                } else {
                    Log.d(TAG, "Error getting admin rooms : ", task.getException());
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                // GET all membre rooms
                mFirestore.collection("members").whereEqualTo("userId", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document != null && document.exists()) {
                                    String roomId = (String) document.get("roomId");
                                    mFirestore.collection("chats").document(roomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null && document.exists()) {
                                                    Log.d(TAG, document.getId() + " => Membre : " +
                                                               document.get("nom"));
                                                    String salonMemb = (String) document.get("nom");
                                                    String salonIdMemb = document.getId();
                                                    String salonLastMessageMemb = (String) document.get("last_message");
                                                    FirebaseSalonAdmin addedSalonMembre = new FirebaseSalonAdmin(salonMemb, salonIdMemb, salonLastMessageMemb);
                                                    salonMembre.add(addedSalonMembre);
                                                    adapter.notifyDataSetChanged();
                                                }else{
                                                    Log.d(TAG, "aucun doc");
                                                }
                                            } else {
                                                Log.d(TAG, "erreur sur le listener");
                                            }
                                            salon.addAll(salonMembre);

                                        }
                                    });
                                }
                                else{
                                    Log.d(TAG, "No member room detected : ", task.getException());
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting membre rooms : ", task.getException());
                        }
                    }
                });
            }
        });


        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
