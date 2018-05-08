package com.emeric.nicot.atable.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
    private RequestManager glideRequest;

    public CustomAdapterChat(Context context, Message message, String userId, RequestManager glide) {
        this.context = context;
        this.message = message;
        this.userId = userId;
        this.glideRequest = glide;
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
                            View v0 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_send, viewGroup, false);
                            return new ViewHolder0(v0);
            case MESSAGE_RECEIVER:
                            View v2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_receive, viewGroup, false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((ViewHolder0) holder).senderMessage.setText(message.getListMessageData().get(position).text);
                ((ViewHolder0) holder).senderTimestamp.setText(message.getListMessageData().get(position).date);
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked)
                                .circleCrop()
                                .dontAnimate())
                        .into(((ViewHolder0) holder).senderImageView);

                break;

            case 2:
                ((ViewHolder2) holder).receiverMessage.setText(message.getListMessageData().get(position).text);
                ((ViewHolder2) holder).receiverTimestamp.setText(message.getListMessageData().get(position).date);
                ((ViewHolder2) holder).receiverName.setText(message.getListMessageData().get(position).name);
                break;

            case 4:
                ((ViewHolder4) holder).senderTimestampEmot.setText(message.getListMessageData().get(position).date);
                ((ViewHolder4) holder).senderEmot.setImageResource(Integer.parseInt(message.getListMessageData().get(position).emot));
                //Glide.with(context).load(message.getListMessageData().get(position).picUrl).into(((ViewHolder4) holder).senderImageView);
                break;

            case 6:
                ((ViewHolder6) holder).receiverEmot.setImageResource(Integer.parseInt(message.getListMessageData().get(position).emot));
                ((ViewHolder6) holder).receiverTimestampEmot.setText(message.getListMessageData().get(position).date);
                ((ViewHolder6) holder).receiverNameEmot.setText(message.getListMessageData().get(position).name);
                //Glide.with(context).load(message.getListMessageData().get(position).picUrl).into(((ViewHolder6) holder).receiverImageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return message.getListMessageData().size();
    }

    private class ViewHolder0 extends RecyclerView.ViewHolder {

        private TextView senderMessage;
        private TextView senderTimestamp;
        private ImageView senderImageView;

        private ViewHolder0(View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.msgr);
            senderTimestamp = itemView.findViewById(R.id.timerSender);
            senderImageView = itemView.findViewById(R.id.imageView_profil_pic_sender);
        }
    }

    private class ViewHolder2 extends RecyclerView.ViewHolder {

        private TextView receiverMessage;
        private TextView receiverTimestamp;
        private TextView receiverName;
        private ImageView receiverImageView;

        private ViewHolder2(View itemView) {
            super(itemView);
            receiverMessage = itemView.findViewById(R.id.msgr2);
            receiverTimestamp = itemView.findViewById(R.id.timerReceiver);
            receiverName = itemView.findViewById(R.id.nameReceiver);
            receiverImageView = itemView.findViewById(R.id.imageView_profil_pic_receiver);
        }
    }

    private class ViewHolder4 extends RecyclerView.ViewHolder {

        private TextView senderTimestampEmot;
        private ImageView senderEmot;

        private ViewHolder4(View itemView) {
            super(itemView);
            senderEmot = itemView.findViewById(R.id.emotSend);
            senderTimestampEmot = itemView.findViewById(R.id.timerSenderEmot);
        }
    }

    private class ViewHolder6 extends RecyclerView.ViewHolder {

        private ImageView receiverEmot;
        private TextView receiverTimestampEmot;
        private TextView receiverNameEmot;

        private ViewHolder6(View itemView) {
            super(itemView);
            receiverEmot = itemView.findViewById(R.id.emotReceiver);
            receiverTimestampEmot = itemView.findViewById(R.id.timerReceiverEmot);
            receiverNameEmot = itemView.findViewById(R.id.nameReceiverEmot);
        }
    }


}