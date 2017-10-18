package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.fragment.AdapterCallback;

import java.util.ArrayList;

/**
 * Created by Nicot Emeric on 27/07/2017.
 */

public class CustomAdapterNotif extends BaseAdapter {

    private static final String AcceptInvitation = "AcceptInvitation";
    private static final String GetInvitation = "GetInvitation";
    private static LayoutInflater inflater = null;
    ArrayList<String> invitation, invitation2, NomSalon;
    Context context;
    private AdapterCallback mAdapterCallback;

    public CustomAdapterNotif(Context context, ArrayList<String> NomSalon, AdapterCallback callback) {
        // TODO Auto-generated constructor stub
        this.invitation = invitation;
        this.invitation2 = invitation2;
        this.NomSalon = NomSalon;
        this.context = context;
        this.mAdapterCallback = callback;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return NomSalon.size();
    }

    @Override
    public Object getItem(int position) {
        return NomSalon.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.list_item_notif, null);
        holder.tv = (TextView) rowView.findViewById(R.id.nomSalonInv);
        holder.tv.setText(NomSalon.get(position));

        FloatingActionButton floatAddFriend = (FloatingActionButton) rowView.findViewById(R.id.floatingActionButtonAccept);

        floatAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CoordinatorLayout cl = (CoordinatorLayout) v.getParent();
                TextView tv = (TextView) cl.findViewById(R.id.nomSalonInv);
                String NomSalon = tv.getText().toString();

                mAdapterCallback.onMethodCallback(NomSalon);
            }
        });


        return rowView;
    }

    public class Holder {
        TextView tv;
    }
}
