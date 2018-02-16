package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emeric.nicot.atable.R;

import java.util.ArrayList;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    ArrayList<Integer> Image;
    Context context;

    public CustomAdapter(Context context, ArrayList<Integer> Image) {
        super();
        this.context = context;
        this.Image = Image;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bubble, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.flag.setImageResource(Image.get(i));
    }

    public int getItemCount() {
        return Image.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView flag;

        public ViewHolder(View itemView) {
            super(itemView);

            flag = (ImageView) itemView.findViewById(R.id.flag);
        }
    }
}
