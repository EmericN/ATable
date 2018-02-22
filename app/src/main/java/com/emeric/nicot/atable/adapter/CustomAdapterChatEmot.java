package com.emeric.nicot.atable.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.Message;


public class CustomAdapterChatEmot extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_SENDER = 0;
    private static final int MESSAGE_RECEIVER = 2;
    private static final String TAG = "debug adapter chat emot";
    private Context context;
    private Message message;
    private String userId;

    public CustomAdapterChatEmot(Context context, Message message, String userId) {
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
            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emot_send, viewGroup, false);
            return new ViewHolder0(v0);
        } else if (viewType == MESSAGE_RECEIVER) {
            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emot_receive, viewGroup, false);
            return new ViewHolder2(v2);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                if (message.getListMessageData().get(position).emot == 2131165330) {
                    ((ViewHolder0) holder).SenderEmot.setImageResource(R.drawable.sticker1);
                }
                ((ViewHolder0) holder).SenderTimestamp.setText(message.getListMessageData().get(position).timestamp);
                break;

            case 2:
                if (message.getListMessageData().get(position).emot == 2131165330) {
                    ((ViewHolder2) holder).ReceiverEmot.setImageResource(R.drawable.sticker1);
                }
                ((ViewHolder2) holder).ReceiverTimestamp.setText(message.getListMessageData().get(position).timestamp);
                ((ViewHolder2) holder).ReceiverName.setText(message.getListMessageData().get(position).name);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return message.getListMessageData().size();
    }

    private class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView SenderTimestamp;
        private ImageView SenderEmot;

        private ViewHolder0(View itemView) {
            super(itemView);
            SenderEmot = (ImageView) itemView.findViewById(R.id.emotSend);
            SenderTimestamp = (TextView) itemView.findViewById(R.id.timerSender);
        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {

        private ImageView ReceiverEmot;
        private TextView ReceiverTimestamp;
        private TextView ReceiverName;

        private ViewHolder2(View itemView) {
            super(itemView);
            ReceiverEmot = (ImageView) itemView.findViewById(R.id.emotReceiver);
            ReceiverTimestamp = (TextView) itemView.findViewById(R.id.timerReceiver);
            ReceiverName = (TextView) itemView.findViewById(R.id.nameReceiver);
        }
    }


}