package com.emeric.nicot.atable;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Nicot Emeric on 12/07/2017.
 */

public class CustomAdapterSalon extends ArrayAdapter<FirebaseSalon> {

    private final int layoutResourceId;
    ArrayList<FirebaseSalon> salon;
    Context context;

    public CustomAdapterSalon(Context context, int layoutResourceId, ArrayList<FirebaseSalon> salon) {

        super(context, layoutResourceId, salon);
        this.salon = salon;
        this.context = context;
        this.layoutResourceId = layoutResourceId;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SalonHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SalonHolder();
            holder.tv = (TextView) row.findViewById(R.id.nomSalon);

            row.setTag(holder);
        } else {

            FirebaseSalon salonAdmin = this.salon.get(position);
            holder.tv.setText(salonAdmin.getSalon());
        }

        return row;
    }

    public class SalonHolder {
        TextView tv;
        ImageView img;
    }
}

