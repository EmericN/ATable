package com.emeric.nicot.atable.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.Message;


public class CustomAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_SENDER = 0;
    private static final int MESSAGE_RECEIVER = 2;
    private static final String TAG = "debug adapter chat";
    private Context context;
    private Message message;
    private String userId;

    public CustomAdapterChat(Context context, Message message, String userId) {
        this.context = context;
        this.message = message;
        this.userId = userId;

    }

    public int getItemViewType(int position) {
        return message.getListMessageData().get(position).idSender.equals(userId) ? MESSAGE_SENDER
                                                                                  : MESSAGE_RECEIVER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == MESSAGE_SENDER) {
            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send, viewGroup, false);
            return new ViewHolder0(v0);
        } else if (viewType == MESSAGE_RECEIVER) {
            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_receive, viewGroup, false);
            return new ViewHolder2(v2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((ViewHolder0) holder).SenderMessage.setText(message.getListMessageData().get(position).text);
                break;

            case 2:
                ((ViewHolder2) holder).ReceiverMessage.setText(message.getListMessageData().get(position).text);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return message.getListMessageData().size();
    }

    private class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView SenderMessage;

        private ViewHolder0(View itemView) {
            super(itemView);
            SenderMessage = (TextView) itemView.findViewById(R.id.msgr);
        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView ReceiverMessage;

        private ViewHolder2(View itemView) {
            super(itemView);
            ReceiverMessage = (TextView) itemView.findViewById(R.id.msgr2);
        }
    }


}