package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.AdapterCallbackNotif;
import com.emeric.nicot.atable.models.FirebaseSalonRequest;

import java.util.ArrayList;


public class CustomAdapterNotif extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FirebaseSalonRequest> salonRequest;
    private AdapterCallbackNotif mAdapterCallbackNotif;

    public CustomAdapterNotif(Context context,
                              ArrayList<FirebaseSalonRequest> salonRequest,
                              AdapterCallbackNotif callback) {

        this.salonRequest = salonRequest;
        this.context = context;
        this.mAdapterCallbackNotif = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_notif, viewGroup, false);
        return new CustomAdapterNotif.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((CustomAdapterNotif.ViewHolder) holder).textViewNotif.setText(salonRequest.get(position).getSalon());
        ((CustomAdapterNotif.ViewHolder) holder).imageButtonTick.setImageResource(R.drawable.ic_checked);
        ((CustomAdapterNotif.ViewHolder) holder).imageButtonCross.setImageResource(R.drawable.ic_error);

        ((ViewHolder) holder).imageButtonTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterCallbackNotif.onMethodCallbackTick(salonRequest.get(position).getSalon(), salonRequest.get(position).getSalonId(), salonRequest.get(position).getIdDoc());
                salonRequest.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,salonRequest.size());
            }
        });

        ((ViewHolder) holder).imageButtonCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterCallbackNotif.onMethodCallbackCross(salonRequest.get(position).getSalon(), salonRequest.get(position).getIdDoc());
                salonRequest.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,salonRequest.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return salonRequest.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNotif;
        private ImageButton imageButtonTick;
        private ImageButton imageButtonCross;

        private ViewHolder(View itemView) {
            super(itemView);
            textViewNotif = itemView.findViewById(R.id.nomSalonInv);
            imageButtonTick = itemView.findViewById(R.id.image_button_tick);
            imageButtonCross = itemView.findViewById(R.id.image_button_cross);
        }
    }
}
