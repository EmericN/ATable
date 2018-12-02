package com.emeric.nicot.atable.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.emeric.nicot.atable.R;
import com.emeric.nicot.atable.models.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;


public class CustomAdapterChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MESSAGE_SENDER = 0;
    private static final int MESSAGE_RECEIVER = 2;
    private static final int MESSAGE_SENDER_EMOT = 4;
    private static final int MESSAGE_RECEIVER_EMOT = 6;
    private static final int MESSAGE_SENDER_PICTURE = 8;
    private static final int MESSAGE_RECEIVER_PICTURE = 10;
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
        if (message.getListMessageData().get(position).emot == null && message.getListMessageData().get(position).picture == null) {
            return message.getListMessageData().get(position).idSender.equals(userId) ? MESSAGE_SENDER
                    : MESSAGE_RECEIVER;
        }
        if(message.getListMessageData().get(position).emot != null){
            return message.getListMessageData().get(position).idSender.equals(userId) ? MESSAGE_SENDER_EMOT
                    : MESSAGE_RECEIVER_EMOT;
        }
        if(message.getListMessageData().get(position).picture != null){
            return message.getListMessageData().get(position).idSender.equals(userId) ? MESSAGE_SENDER_PICTURE
                                                                                      : MESSAGE_RECEIVER_PICTURE;
        }
        return position;
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
            case MESSAGE_SENDER_PICTURE:
                            View v8 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picture_send, viewGroup, false);
                            return new ViewHolder8(v8);
            case MESSAGE_RECEIVER_PICTURE:
                            View v10 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picture_receive, viewGroup, false);
                            return new ViewHolder10(v10);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((ViewHolder0) holder).senderMessage.setText(message.getListMessageData().get(position).text);
                ((ViewHolder0) holder).senderTimestamp.setText(message.getListMessageData().get(position).date);
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .circleCrop()
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder0) holder).senderImageView);

                break;

            case 2:
                ((ViewHolder2) holder).receiverMessage.setText(message.getListMessageData().get(position).text);
                ((ViewHolder2) holder).receiverTimestamp.setText(message.getListMessageData().get(position).date);
                ((ViewHolder2) holder).receiverName.setText(message.getListMessageData().get(position).name);
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .circleCrop()
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder2) holder).receiverImageView);
                break;

            case 4:
                ((ViewHolder4) holder).senderTimestampEmot.setText(message.getListMessageData().get(position).date);
                ((ViewHolder4) holder).senderEmot.setImageResource(Integer.parseInt(message.getListMessageData().get(position).emot));
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .circleCrop()
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder4) holder).senderImageViewEmot);
                break;

            case 6:
                ((ViewHolder6) holder).receiverEmot.setImageResource(Integer.parseInt(message.getListMessageData().get(position).emot));
                ((ViewHolder6) holder).receiverTimestampEmot.setText(message.getListMessageData().get(position).date);
                ((ViewHolder6) holder).receiverNameEmot.setText(message.getListMessageData().get(position).name);
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .circleCrop()
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder6) holder).receiverImageViewEmot);
                break;

            case 8:
                ((ViewHolder8) holder).senderTimestampPicture.setText(message.getListMessageData().get(position).date);
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .circleCrop()
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder8) holder).senderImageViewPicture);
                glideRequest.load("http://192.168.1.24/ATable/image_upload/"+message.getListMessageData().get(position).picture+".jpg")
                        .apply(new RequestOptions()
                                .transform(new RoundedCorners(50))
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder8) holder).senderPicture);
                break;

            case 10:
                ((ViewHolder10) holder).receiverTimestampPicture.setText(message.getListMessageData().get(position).date);
                ((ViewHolder10) holder).receiverNamePicture.setText(message.getListMessageData().get(position).name);
                glideRequest.load(message.getListMessageData().get(position).picUrl)
                        .apply(new RequestOptions().override(80, 80)
                                .circleCrop()
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder10) holder).receiverImageViewPicture);
                glideRequest.load("http://192.168.1.24/ATable/image_upload/"+message.getListMessageData().get(position).picture+".jpg")
                        .apply(new RequestOptions()
                                .transform(new RoundedCorners(50))
                                .placeholder(R.drawable.ic_checked)
                                .error(R.drawable.ic_checked))
                        .into(((ViewHolder10) holder).receiverPicture);
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
        private ImageView senderImageViewEmot;

        private ViewHolder4(View itemView) {
            super(itemView);
            senderEmot = itemView.findViewById(R.id.emotSend);
            senderTimestampEmot = itemView.findViewById(R.id.timerSenderEmot);
            senderImageViewEmot = itemView.findViewById(R.id.imageView_profil_pic_sender_emot);
        }
    }

    private class ViewHolder6 extends RecyclerView.ViewHolder {

        private ImageView receiverEmot;
        private TextView receiverTimestampEmot;
        private TextView receiverNameEmot;
        private ImageView receiverImageViewEmot;

        private ViewHolder6(View itemView) {
            super(itemView);
            receiverEmot = itemView.findViewById(R.id.emotReceiver);
            receiverTimestampEmot = itemView.findViewById(R.id.timerReceiverEmot);
            receiverNameEmot = itemView.findViewById(R.id.nameReceiverEmot);
            receiverImageViewEmot = itemView.findViewById(R.id.imageView_profil_pic_receiver_emot);
        }
    }

    private class ViewHolder8 extends RecyclerView.ViewHolder {

        private TextView senderTimestampPicture;
        private ImageView senderPicture;
        private ImageView senderImageViewPicture;

        private ViewHolder8(View itemView) {
            super(itemView);
            senderPicture = itemView.findViewById(R.id.pictureSend);
            senderTimestampPicture = itemView.findViewById(R.id.timerSenderPicture);
            senderImageViewPicture = itemView.findViewById(R.id.imageView_profil_pic_sender_picture);
        }
    }

    private class ViewHolder10 extends RecyclerView.ViewHolder {

        private ImageView receiverPicture;
        private TextView receiverTimestampPicture;
        private TextView receiverNamePicture;
        private ImageView receiverImageViewPicture;

        private ViewHolder10(View itemView) {
            super(itemView);
            receiverPicture = itemView.findViewById(R.id.pictureReceiver);
            receiverTimestampPicture = itemView.findViewById(R.id.timerReceiverPicture);
            receiverNamePicture = itemView.findViewById(R.id.nameReceiverPicture);
            receiverImageViewPicture = itemView.findViewById(R.id.imageView_profil_pic_receiver_picture);
        }
    }
}