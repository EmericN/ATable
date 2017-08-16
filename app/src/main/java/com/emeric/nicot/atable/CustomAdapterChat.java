package com.emeric.nicot.atable;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.id.message;

/**
 * Created by Nicot Emeric on 13/08/2017.
 */

public class CustomAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    public ArrayList<MessageChat> messageChat;

    public class ViewHolder0 extends RecyclerView.ViewHolder {

        public TextView SenderMessage;

        public ViewHolder0(View itemView) {
            super(itemView);
            SenderMessage = (TextView) itemView.findViewById(R.id.msgr);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        public TextView ReceiverMessage;

        public ViewHolder2(View itemView) {
            super(itemView);
            ReceiverMessage = (TextView) itemView.findViewById(R.id.msgr2);
        }
    }

    public CustomAdapterChat(Context context, ArrayList<MessageChat> messageChat) {
        this.context = context;
        this.messageChat = messageChat;

    }

    public int getItemViewType(int position) {
        int side = messageChat.get(position).getSide();

        if (side == 0) {
            return 0;
        } else {
            return 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v0 = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_send, viewGroup, false);
        View v2 = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_receive, viewGroup, false);

        switch (viewType) {
            case 0:
                return new ViewHolder0(v0);
            case 2:
                return new ViewHolder2(v2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageChat MessageChat = messageChat.get(position);
        switch (holder.getItemViewType()) {
            case 0:
                ((ViewHolder0)holder).SenderMessage.setText(MessageChat.getContent());
                break;

            case 2:
                ((ViewHolder2)holder).ReceiverMessage.setText(MessageChat.getContent());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageChat.size();
    }


}