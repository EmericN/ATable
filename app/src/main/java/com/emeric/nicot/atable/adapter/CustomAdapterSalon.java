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
import com.emeric.nicot.atable.models.FirebaseSalonAdmin;

import java.util.ArrayList;

public class CustomAdapterSalon extends ArrayAdapter<FirebaseSalonAdmin> {

    private static final String TAG = "debug array";
    private static int[] imageId = {R.drawable.ic_crown};
    private final int layoutResourceId;
    private ArrayList<FirebaseSalonAdmin> salon,salonAdmin, salonMembre;
    private Context context;
    private OnClickListener mListener;

    public CustomAdapterSalon(Context context, int layoutResourceId,
                              ArrayList<FirebaseSalonAdmin> salon,
                              ArrayList<FirebaseSalonAdmin> salonAdmin,
                              ArrayList<FirebaseSalonAdmin> salonMembre,
                              OnClickListener mListener) {

        super(context, layoutResourceId, salon);
        this.salon = salon;
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
        View row;
        SalonHolder holder;
//TODO check for smoother scrolling view : https://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
        row = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        holder = new SalonHolder();
        holder.tvRoomName = (TextView) row.findViewById(R.id.nomSalon);
        holder.tvLastMessage = (TextView) row.findViewById(R.id.lastMessage);
        holder.iv = (ImageView) row.findViewById(R.id.imageViewCrown);
        holder.civ = (ImageView) row.findViewById(R.id.clickableImageViewCrown);
        row.setTag(holder);

        Log.d(TAG, "taille salon : " + salon.size());
        final FirebaseSalonAdmin salonAll = salon.get(position);

        if (position < salon.size()-salonMembre.size()) {
            holder.tvRoomName.setText(salonAll.getSalon());
            holder.tvLastMessage.setText(salonAll.getSalonLastMessage());
            holder.iv.setImageResource(imageId[0]);
            holder.civ.setImageResource(imageId[0]);
            } else {
                holder.tvRoomName.setText(salonAll.getSalon());
                holder.tvLastMessage.setText(salonAll.getSalonLastMessage());
            }

        holder.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mListener != null){
                    mListener.onClick(salonAll.getSalonId(), salonAll.getSalon());
                }
            }
        });

        return row;
    }

    class SalonHolder {
        TextView tvRoomName;
        TextView tvLastMessage;
        TextView welcomTextView;
        ImageView iv,civ;
    }
}

