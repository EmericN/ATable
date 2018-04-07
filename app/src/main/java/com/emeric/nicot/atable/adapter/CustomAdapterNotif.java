package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.AdapterCallback;
import com.emeric.nicot.atable.models.FirebaseSalonRequest;

import java.util.ArrayList;


public class CustomAdapterNotif extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FirebaseSalonRequest> salonRequest;
    private AdapterCallback mAdapterCallback;

    public CustomAdapterNotif(Context context,
                              ArrayList<FirebaseSalonRequest> salonRequest,
                              AdapterCallback callback) {

        this.salonRequest = salonRequest;
        this.context = context;
        this.mAdapterCallback = callback;
    }

    /*public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View row;

        row = LayoutInflater.from(convertView.getContext()).inflate(layoutResourceId, parent, false);
        holder.tv = (TextView) row.findViewById(R.id.nomSalonInv);
        final FirebaseSalonRequest salonFriendRequest = salonRequest.get(position);
        row.setTag(holder);
        String TAG = "debug notif";
        Log.d(TAG, "Tableau de friend request : " + salonRequest.size());
        holder.tv.setText(salonFriendRequest.getSalon());

        FloatingActionButton floatAddFriend = row.findViewById(R.id.floatingActionButtonAccept);

        floatAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CoordinatorLayout cl = (CoordinatorLayout) v.getParent();
                TextView tv = cl.findViewById(R.id.nomSalonInv);
                String NomSalon = tv.getText().toString();

                mAdapterCallback.onMethodCallback(NomSalon, salonFriendRequest.getSalonId(), salonFriendRequest.getIdDoc());
            }
        });


        return row;
    }*/

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
                mAdapterCallback.onMethodCallback(salonRequest.get(position).getSalon(), salonRequest.get(position).getSalonId(), salonRequest.get(position).getIdDoc());
                salonRequest.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,salonRequest.size());
            }
        });

        ((ViewHolder) holder).imageButtonCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO delete invitation
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
