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

    private ArrayList<Integer> image;
    private Context context;
    private final OnItemClickListener listener;


    public interface OnItemClickListener{
        void onItemClick(Integer item);
    }

    public CustomAdapter(Context context, ArrayList<Integer> image, OnItemClickListener listener) {
        super();
        this.context = context;
        this.image = image;
        this.listener = listener;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.emot, viewGroup, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.emot.setImageResource(image.get(position));
        viewHolder.bind(image.get(position), listener);
    }

    public int getItemCount() {
        return image.size();
    }

     static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView emot;

         private ViewHolder(View itemView) {
            super(itemView);
            emot = (ImageView) itemView.findViewById(R.id.emot);
        }
         private void bind(final Integer item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(item);
                }
            });
        }
    }
}
