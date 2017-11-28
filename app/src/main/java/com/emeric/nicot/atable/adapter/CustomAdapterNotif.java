package com.emeric.nicot.atable.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.fragment.AdapterCallback;
import com.emeric.nicot.atable.models.FirebaseSalonAdmin;

import java.util.ArrayList;


public class CustomAdapterNotif extends ArrayAdapter<FirebaseSalonAdmin> {

    private static final String AcceptInvitation = "AcceptInvitation";
    private static final String GetInvitation = "GetInvitation";
    private static LayoutInflater inflater = null;
    private final int layoutResourceId;
    private ArrayList<FirebaseSalonAdmin> salonAdmin;
    private AdapterCallback mAdapterCallback;

    public CustomAdapterNotif(Context context,
                              int layoutResourceId, ArrayList<FirebaseSalonAdmin> salonAdmin,
                              AdapterCallback callback) {
        super(context, layoutResourceId, salonAdmin);
        this.salonAdmin = salonAdmin;
        Context context1 = context;
        this.mAdapterCallback = callback;
        this.layoutResourceId = layoutResourceId;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View row;

        row = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        holder.tv = (TextView) row.findViewById(R.id.nomSalonInv);
        final FirebaseSalonAdmin salonFriendRequest = salonAdmin.get(position);
        row.setTag(holder);
        String TAG = "debug notif";
        Log.d(TAG, "Tableau de friend request : " + salonAdmin.size());
        holder.tv.setText(salonFriendRequest.getSalon());

        FloatingActionButton floatAddFriend = (FloatingActionButton) row.findViewById(R.id.floatingActionButtonAccept);

        floatAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CoordinatorLayout cl = (CoordinatorLayout) v.getParent();
                TextView tv = (TextView) cl.findViewById(R.id.nomSalonInv);
                String NomSalon = tv.getText().toString();

                mAdapterCallback.onMethodCallback(NomSalon, salonFriendRequest.getSalonId());
            }
        });


        return row;
    }

    public class Holder {
        TextView tv;
    }
}
