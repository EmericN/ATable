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


public class CustomAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_SENDER = 0;
    private static final int MESSAGE_RECEIVER = 2;
    private static final int MESSAGE_SENDER_EMOT = 4;
    private static final int MESSAGE_RECEIVER_EMOT = 6;
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

        if (message.getListMessageData().get(position).emot == null) {
            return message.getListMessageData().get(position).idSender.equals(userId) ? MESSAGE_SENDER
                    : MESSAGE_RECEIVER;
        }else{
            return message.getListMessageData().get(position).idSender.equals(userId) ? MESSAGE_SENDER_EMOT
                    : MESSAGE_RECEIVER_EMOT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType){
            case MESSAGE_SENDER:
                            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_send, viewGroup, false);
                            return new ViewHolder0(v0);
            case MESSAGE_RECEIVER:
                            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_receive, viewGroup, false);
                            return new ViewHolder2(v2);
            case MESSAGE_SENDER_EMOT:
                            View v4 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emot_send, viewGroup, false);
                            return new ViewHolder4(v4);
            case MESSAGE_RECEIVER_EMOT:
                            View v6 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emot_receive, viewGroup, false);
                            return new ViewHolder6(v6);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((ViewHolder0) holder).SenderMessage.setText(message.getListMessageData().get(position).text);
                ((ViewHolder0) holder).SenderTimestamp.setText(message.getListMessageData().get(position).date);
                break;

            case 2:
                ((ViewHolder2) holder).ReceiverMessage.setText(message.getListMessageData().get(position).text);
                ((ViewHolder2) holder).ReceiverTimestamp.setText(message.getListMessageData().get(position).date);
                ((ViewHolder2) holder).ReceiverName.setText(message.getListMessageData().get(position).name);
                break;

            case 4:
                ((ViewHolder4) holder).SenderTimestampEmot.setText(message.getListMessageData().get(position).date);
                ((ViewHolder4) holder).SenderEmot.setImageResource(Integer.parseInt(message.getListMessageData().get(position).emot));
                break;

            case 6:
                ((ViewHolder6) holder).ReceiverEmot.setImageResource(Integer.parseInt(message.getListMessageData().get(position).emot));
                ((ViewHolder6) holder).ReceiverTimestampEmot.setText(message.getListMessageData().get(position).date);
                ((ViewHolder6) holder).ReceiverNameEmot.setText(message.getListMessageData().get(position).name);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return message.getListMessageData().size();
    }

    private class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView SenderMessage;
        private TextView SenderTimestamp;

        private ViewHolder0(View itemView) {
            super(itemView);
            SenderMessage = itemView.findViewById(R.id.msgr);
            SenderTimestamp = itemView.findViewById(R.id.timerSender);
        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView ReceiverMessage;
        private TextView ReceiverTimestamp;
        private TextView ReceiverName;

        private ViewHolder2(View itemView) {
            super(itemView);
            ReceiverMessage = itemView.findViewById(R.id.msgr2);
            ReceiverTimestamp = itemView.findViewById(R.id.timerReceiver);
            ReceiverName = itemView.findViewById(R.id.nameReceiver);
        }
    }

    private class ViewHolder4 extends RecyclerView.ViewHolder {

        private TextView SenderTimestampEmot;
        private ImageView SenderEmot;

        private ViewHolder4(View itemView) {
            super(itemView);
            SenderEmot = itemView.findViewById(R.id.emotSend);
            SenderTimestampEmot = itemView.findViewById(R.id.timerSenderEmot);
        }
    }

    private class ViewHolder6 extends RecyclerView.ViewHolder {

        private ImageView ReceiverEmot;
        private TextView ReceiverTimestampEmot;
        private TextView ReceiverNameEmot;

        private ViewHolder6(View itemView) {
            super(itemView);
            ReceiverEmot = itemView.findViewById(R.id.emotReceiver);
            ReceiverTimestampEmot = itemView.findViewById(R.id.timerReceiverEmot);
            ReceiverNameEmot = itemView.findViewById(R.id.nameReceiverEmot);
        }
    }


}