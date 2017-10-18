package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emeric.nicot.atable.R;

import java.util.ArrayList;

/**
 * Created by Nicot Emeric on 07/07/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    ArrayList<String> Ordre;
    ArrayList<Integer> Image;
    Context context;

    public CustomAdapter(Context context, ArrayList<String> Ordre, ArrayList<Integer> Image) {
        super();
        this.context = context;
        this.Ordre = Ordre;
        this.Image = Image;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bubble, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.textviewBubble.setText(Ordre.get(i));
        viewHolder.flag.setImageResource(Image.get(0));
    }

    public int getItemCount() {
        return Ordre.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textviewBubble;
        public ImageView flag;

        public ViewHolder(View itemView) {
            super(itemView);

            textviewBubble = (TextView) itemView.findViewById(R.id.textviewBubble);
            flag = (ImageView) itemView.findViewById(R.id.flag);
        }
    }
}
