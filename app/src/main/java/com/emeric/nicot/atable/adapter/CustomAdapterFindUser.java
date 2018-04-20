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

import java.util.ArrayList;

public class CustomAdapterFindUser extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private AdapterCallbackFindUser mAdapterCallbackUserFind;
    private String TAG="debug custom adapter user find";
    private ArrayList<String> response;

    public CustomAdapterFindUser(Context context,
                                 ArrayList<String> response,
                                 AdapterCallbackFindUser callback){

        this.context = context;
        this.response = response;
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
        ((CustomAdapterFindUser.ViewHolder) holder).textViewFindUser.setText(response.get(position));
        ((ViewHolder) holder).textViewFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapterCallbackUserFind.onMethodCallbackFindUser(response.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewFindUser;

        private ViewHolder(View itemView) {
            super(itemView);
            textViewFindUser = itemView.findViewById(R.id.textViewFindUser);
        }
    }
}
