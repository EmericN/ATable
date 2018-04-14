package com.emeric.nicot.atable.fragment;


import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.emeric.nicot.atable.adapter.CustomAdapterNotif;
import com.emeric.nicot.atable.models.FirebaseSalonRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class NotifContentFragment extends Fragment {
    private ListView LV;
    private String userId;
    private CustomAdapterNotif adapter;
    private TextView textNotif;
    private ArrayList<FirebaseSalonRequest> salonRequest;
    private String TAG = "debug Notif";
    private ProgressDialog pDialog;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CollectionReference colRef;


   /* public void onMethodCallback(String NomSalon, final String salonId, final String idDoc) {
        Log.d(TAG, "Salon accepté : " + NomSalon);
        // Delete pending invitation
        mFirestore.collection("pending").document(idDoc).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "invitation supprimé");
                Map<String, Object> members = new HashMap<>();
                members.put("userId", userId);
                members.put("roomId", salonId);

                mFirestore.collection("members").document().set(members).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Members added");
                        Toast.makeText(getContext(), "Invitation acceptée" ,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic(salonId);
        salonRequest.clear();
        adapter.notifyDataSetChanged();
        RefreshRequest();
    }*/

    /*public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_notification_list, null);

        textNotif = v.findViewById(R.id.textViewNotif);
        swipeRefreshLayout = v.findViewById(R.id.swiperefreshNotif);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        salonRequest = new ArrayList<>();
        LV = v.findViewById(R.id.ListView1);
        adapter = new CustomAdapterNotif(getContext(), R.layout.list_item_notif, salonRequest,
                NotifContentFragment.this);
        LV.setAdapter(adapter);

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userId = user.getUid();
        }
        textNotif.setText("Invitation en attente");
        RefreshRequest();
        // Refresh list of rooms
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshRequest();
            }
        });
        return v;
    }

    public void RefreshRequest() {

        salonRequest.clear();
        colRef = mFirestore.collection("pending");
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
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error getting friend request rooms : ", task.getException());
                }
            }
        });
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        }*/
}
