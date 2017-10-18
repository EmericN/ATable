package com.emeric.nicot.atable.adapter;

import android.content.Context;
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
    public static int[] imageId = {R.drawable.ic_crown, R.drawable.ic_checked};
    private final int layoutResourceId;
    ArrayList<FirebaseSalonAdmin> salonAdmin;
    ArrayList<FirebaseSalonAdmin> salonMembre;
    ArrayList<FirebaseSalonAdmin> salonAll;
    Context context;

    public CustomAdapterSalon(Context context, int layoutResourceId,
                              ArrayList<FirebaseSalonAdmin> salonAdmin,
                              ArrayList<FirebaseSalonAdmin> salonMembre) {

        super(context, layoutResourceId, salonAdmin);
        this.salonAdmin = salonAdmin;
        this.salonMembre = salonMembre;

        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        SalonHolder holder;

        row = LayoutInflater.from(getContext()).inflate(layoutResourceId, parent, false);
        holder = new SalonHolder();
        holder.tv = (TextView) row.findViewById(R.id.nomSalon);
        holder.iv = (ImageView) row.findViewById(R.id.imageViewCrown);

        row.setTag(holder);
        salonAll = new ArrayList<FirebaseSalonAdmin>();
        salonAll.addAll(salonAdmin);
        salonAll.addAll(salonMembre);
        FirebaseSalonAdmin salonAll = this.salonAll.get(position);

        if (position <= salonAdmin.size()) {
            holder.tv.setText(salonAll.getSalon());
            holder.iv.setImageResource(imageId[0]);
        } else {
            holder.tv.setText(salonAll.getSalon());
            holder.iv.setImageResource(imageId[0]);
        }

        return row;
    }

    class SalonHolder {
        TextView tv;
        ImageView iv;
    }
}

