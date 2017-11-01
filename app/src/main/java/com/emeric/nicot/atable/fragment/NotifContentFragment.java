package com.emeric.nicot.atable.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.adapter.CustomAdapterNotif;
import com.emeric.nicot.atable.models.FirebaseSalonAdmin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NotifContentFragment extends Fragment implements AdapterCallback {
    ListView LV;
    String mail, userId;
    JSONArray InvitArray = null;
    JSONArray NomSalonArray = null;
    CustomAdapterNotif adapter;
    ArrayList<FirebaseSalonAdmin> salonAdmin;
    private String TAG = "debug Notif";
    private ProgressDialog pDialog;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;


    public void onMethodCallback(String NomSalon, String salonId) {
        Log.d(TAG, "Salon accepté : " + NomSalon);
        //supprime le pending dans la db
        CollectionReference docRef = mFirestore.collection("chats");
        Map<String, Object> updates = new HashMap<>();
        updates.put("pending", "");
        updates.put("membres", userId);
        docRef.document(salonId).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "invitation supprimé");
            }
        });
        RefreshRequest();
        adapter.notifyDataSetChanged();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_notification_list, null);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        salonAdmin = new ArrayList<>();
        LV = (ListView) v.findViewById(R.id.ListView1);
        adapter = new CustomAdapterNotif(getContext(), R.layout.list_item_notif, salonAdmin,
                NotifContentFragment.this);
        LV.setAdapter(adapter);

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "test 1");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();
            RefreshRequest();
        }
        return v;
    }

    public void RefreshRequest() {

        CollectionReference docRef = mFirestore.collection("chats");
        docRef.whereEqualTo("pending", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "test 2");
                        Log.d(TAG, document.getId() + " => " + document.get("nom"));
                        String salonAdm = (String) document.get("nom");
                        String salonIdAdm = document.getId();

                        FirebaseSalonAdmin addedSalonAdmin = new FirebaseSalonAdmin(salonAdm, salonIdAdm);

                        salonAdmin.add(addedSalonAdmin);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting friend request rooms : ", task.getException());
                }
            }
        });
        }

}
