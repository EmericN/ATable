package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.fragment.SalonContentFragment;
import com.emeric.nicot.atable.models.AdapterCallbackRoom;
import com.emeric.nicot.atable.models.FirebaseSalon;

import java.util.ArrayList;

public class CustomAdapterRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<FirebaseSalon> salonAdmin,salonMembre,salonAll ;
    private AdapterCallbackRoom mAdapterCallback;
    private static int[] imageId = {R.drawable.ic_crown};
    private SalonContentFragment salonContentFragment;

    public CustomAdapterRoom(SalonContentFragment salonContentFragment,
                             ArrayList<FirebaseSalon> salonAdmin,
                              ArrayList<FirebaseSalon> salonMembre,
                             AdapterCallbackRoom callback) {

        this.salonContentFragment = salonContentFragment;
        this.salonAdmin = salonAdmin;
        this.salonMembre = salonMembre;
        this.mAdapterCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_salon, viewGroup, false);
        return new CustomAdapterRoom.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        salonAll = new ArrayList<>();
        salonAll.addAll(salonAdmin);
        salonAll.addAll(salonMembre);
        final FirebaseSalon salonPosition = salonAll.get(position);

        if (position < (salonAdmin.size()+salonMembre.size())-salonMembre.size()) {
            ((CustomAdapterRoom.ViewHolder) holder).textViewRoomName.setText(salonPosition.getSalon());
            ((CustomAdapterRoom.ViewHolder) holder).textViewLastMessage.setText(salonPosition.getSalonLastMessage());
            ((CustomAdapterRoom.ViewHolder) holder).clickableImageView.setImageResource(imageId[0]);
        }else{
            ((CustomAdapterRoom.ViewHolder) holder).textViewRoomName.setText(salonPosition.getSalon());
            ((CustomAdapterRoom.ViewHolder) holder).textViewLastMessage.setText(salonPosition.getSalonLastMessage());
        }

        ((CustomAdapterRoom.ViewHolder) holder).clickableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterCallback.onMethodCallbackQuickSticker(salonPosition.getSalon(), salonPosition.getSalonId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return salonAdmin.size()+salonMembre.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewRoomName;
        private TextView textViewLastMessage;
        private ImageView clickableImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            textViewRoomName = itemView.findViewById(R.id.nomSalon);
            textViewLastMessage = itemView.findViewById(R.id.lastMessage);
            clickableImageView = itemView.findViewById(R.id.clickableImageViewCrown);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdapterCallback.onMethodCallbackEnterRoom(salonAll, getAdapterPosition());
                }
            });
        }
    }
}


//TODO work on this customadapter : callback on row click & callback for clickableImageView click !
// TODO try to merge AdapterCallback too !