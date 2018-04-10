package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.FirebaseSalon;

import java.util.ArrayList;

public class CustomAdapterSalon extends ArrayAdapter<FirebaseSalon> {

    private static final String TAG = "debug array";
    private static int[] imageId = {R.drawable.ic_crown};
    private final int layoutResourceId;
    private ArrayList<FirebaseSalon> salon,salonAdmin, salonMembre;
    private Context context;
    private OnClickListener mListener;

    public CustomAdapterSalon(Context context, int layoutResourceId,
                              ArrayList<FirebaseSalon> salonAdmin,
                              ArrayList<FirebaseSalon> salonMembre,
                              OnClickListener mListener) {

        super(context, layoutResourceId);

        this.salonAdmin = salonAdmin;
        this.salonMembre = salonMembre;
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.mListener = mListener;
    }

    public interface OnClickListener {
        void onClick(String salonId, String nomSalon);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Log.d(TAG, "HELLO !");
        View row;
        SalonHolder holder;
//TODO check for smoother scrolling view : https://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
        row = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        holder = new SalonHolder();
        holder.tvRoomName =  row.findViewById(R.id.nomSalon);
        holder.tvLastMessage = row.findViewById(R.id.lastMessage);
        holder.civ = row.findViewById(R.id.clickableImageViewCrown);
        row.setTag(holder);

        Log.d(TAG, "taille salon admin : " + salonAdmin.size());
        Log.d(TAG, "taille salon membre : " + salonMembre.size());

        final FirebaseSalon salonA = salonAdmin.get(position);
        final FirebaseSalon salonM = salonMembre.get(position);

        if (position < (salonAdmin.size()+salonMembre.size())-salonMembre.size()) {
            holder.tvRoomName.setText(salonA.getSalon());
            holder.tvLastMessage.setText(salonA.getSalonLastMessage());
            holder.civ.setImageResource(imageId[0]);
            } else {
                holder.tvRoomName.setText(salonM.getSalon());
                holder.tvLastMessage.setText(salonM.getSalonLastMessage());
            }

        holder.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mListener != null){
                    mListener.onClick(salonA.getSalonId(), salonA.getSalon());
                }
            }
        });

        return row;
    }

    class SalonHolder {
        TextView tvRoomName;
        TextView tvLastMessage;
        ImageView civ;
    }
}

