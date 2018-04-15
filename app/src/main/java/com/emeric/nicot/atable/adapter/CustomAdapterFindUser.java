package com.emeric.nicot.atable.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.AdapterCallbackFindUser;
import com.emeric.nicot.atable.models.ListUsers;

public class CustomAdapterFindUser extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ListUsers users;
    private AdapterCallbackFindUser mAdapterCallbackUserFind;
    private String TAG="debug custom adapter user find";

    public CustomAdapterFindUser(Context context,
                                 ListUsers users,
                                 AdapterCallbackFindUser callback){

        this.users = users;
        this.context = context;
        this.mAdapterCallbackUserFind = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_find_user, viewGroup, false);
        return new CustomAdapterFindUser.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((CustomAdapterFindUser.ViewHolder) holder).textViewFindUser.setText(users.getListUsers().get(position).nomPrenom);
        ((ViewHolder) holder).textViewFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterCallbackUserFind.onMethodCallbackFindUser(users.getListUsers().get(position).nomPrenom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.getListUsers().size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFindUser;

        private ViewHolder(View itemView) {
            super(itemView);
            textViewFindUser = itemView.findViewById(R.id.textViewFindUser);
        }
    }
}
