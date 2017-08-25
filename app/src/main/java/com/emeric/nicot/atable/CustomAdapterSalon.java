package com.emeric.nicot.atable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nicot Emeric on 12/07/2017.
 */

public class CustomAdapterSalon extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<String> salon, salon2, salonAll;
    Context context;
    int[] imageId;

    public CustomAdapterSalon(Context context, ArrayList<String> salon, ArrayList<String> salon2, int[] imageId) {
        // TODO Auto-generated constructor stub
        this.salon = salon;
        this.salon2 = salon2;
        this.context = context;
        this.imageId = imageId;
        salonAll = new ArrayList<String>();
        salonAll.addAll(salon);
        salonAll.addAll(salon2);
        Arrays.toString(salonAll.toArray());
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return salonAll.size();
    }

    public int getCount1() {
        return salon.size();
    }

    @Override
    public Object getItem(int position) {
        return salonAll.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.nomSalon);
        holder.img = (ImageView) rowView.findViewById(R.id.imageViewCrown);
        holder.tv.setText(salonAll.get(position));
        if (position < salon.size()) {
            holder.img.setImageResource(imageId[0]);
        } else {
            holder.img.setImageResource(imageId[1]);
        }
        return rowView;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }
}

