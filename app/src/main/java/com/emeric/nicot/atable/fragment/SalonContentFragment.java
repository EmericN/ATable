package com.example.emericnicot.atabledevtest.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.SalonActivity;
import com.emeric.nicot.atable.adapter.CustomAdapterSalon;
import com.emeric.nicot.atable.models.FirebaseSalonAdmin;
import com.emeric.nicot.atable.models.FirebaseSalonMembre;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicot Emeric on 27/06/2017.
 */

public class SalonContentFragment extends Fragment {
    public ArrayList<FirebaseSalonAdmin> salonAdmin;
    public ArrayList<FirebaseSalonMembre> salonMembre;
    ListView LV;
    String mail;
    CustomAdapterSalon adapter;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRefRelationship, myRefMessage, myRefChat, myRefGetChat, myRefGetChatTs;
    private String userId, ts, TAG = "debug firestore";
    private Long tsLong;
    private FirebaseFirestore mFirestore;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        myRefRelationship = database.getReference("relationship");
        myRefChat = database.getReference("chats");
        myRefMessage = database.getReference("messages");
        tsLong = System.currentTimeMillis();
        ts = tsLong.toString();
        salonAdmin = new ArrayList<FirebaseSalonAdmin>();
        salonMembre = new ArrayList<FirebaseSalonMembre>();
        CollectionReference docRef = mFirestore.collection("chats");

        View v = inflater.inflate(R.layout.tab_salon_list, null);
        FloatingActionButton floatAdd = (FloatingActionButton) v.findViewById(R.id.FloatButtonAdd);
        LV = (ListView) v.findViewById(R.id.ListView1);


        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();

        } else {

        }

        adapter = new CustomAdapterSalon(getContext(), R.layout.list_item, salonAdmin, salonMembre);
        LV.setAdapter(adapter);
// GET all admin rooms

        docRef.whereEqualTo("admin", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        Log.d(TAG, document.getId() + " => " + document.get("nom"));
                        String salonAdm = (String) document.get("nom");
                        FirebaseSalonAdmin addedSalonAdmin = new FirebaseSalonAdmin(salonAdm);
                        salonAdmin.add(addedSalonAdmin);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting admin rooms : ", task.getException());
                }
            }
        });
// GET all membre rooms
        docRef.whereEqualTo("membre", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        Log.d(TAG, document.getId() + " => " + document.get("nom"));
                        String salonMemb = (String) document.get("nom");
                        FirebaseSalonMembre addedSalonMembre = new FirebaseSalonMembre(salonMemb);
                        salonMembre.add(addedSalonMembre);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting membre rooms : ", task.getException());
                }
            }
        });



        LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent i = new Intent(getContext(), SalonActivity.class);
                FirebaseSalonAdmin PossalonAdmin = salonAdmin.get(position);
                i.putExtra("NomSalon", PossalonAdmin.getSalon());
                i.putExtra("userId", userId);
                if (position < salonAdmin.size()) {
                    i.putExtra("tag", 1);
                } else {
                    i.putExtra("tag", 2);
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

                        //myRefChat = /chats/
                        Map<String, Object> roomsMap = new HashMap<>();
                        roomsMap.put("admin", nomsalon);
                        Map<String, Object> chatsMap = new HashMap<>();
                        chatsMap.put("nom", nomsalon);
                        chatsMap.put("admin", userId);
                        chatsMap.put("membres", "");

                        mFirestore.collection("chats").document().set(chatsMap);
                        mFirestore.collection("users").document(userId).update(roomsMap);
                        FirebaseSalonAdmin salonAdd = new FirebaseSalonAdmin(nomsalon);
                        salonAdmin.add(salonAdd);
                        adapter.notifyDataSetChanged();
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
}
