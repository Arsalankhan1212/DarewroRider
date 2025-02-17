package com.darewro.riderApp.view.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.darewro.riderApp.App;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.db.model.ChatMessage;
import com.darewro.riderApp.view.chatLibViewModel.IMessageModel;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.xmpp.XmppClient;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.ByteArrayOutputStream;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class CustomChatOutcomingAdapterViewHolder extends MessagesListAdapter.OutcomingMessageViewHolder<IMessageModel> {

    ImageView status;
    TextView message, time;
    PorterShapeImageView image;
    LinearLayout ll_audio;
    FrameLayout bubble, fl_resend;
    VoicePlayerView voicePlayerView;
    ImageButton resend;

    public CustomChatOutcomingAdapterViewHolder(View itemView) {
        super(itemView);

        message = itemView.findViewById(com.stfalcon.chatkit.R.id.messageText);
        time = itemView.findViewById(com.stfalcon.chatkit.R.id.messageTime);
        status = itemView.findViewById(R.id.messageStatus);
        image = itemView.findViewById(R.id.image);
        voicePlayerView = itemView.findViewById(R.id.voicePlayerView);
        fl_resend = itemView.findViewById(R.id.fl_resend);
        resend = itemView.findViewById(R.id.resend);
        ll_audio = itemView.findViewById(R.id.ll_audio);
        bubble = itemView.findViewById(R.id.bubble);
    }

    @Override
    public void onBind(final IMessageModel iMessageModel) {

        if (iMessageModel.getChatMessage().getDeliveryStatus() == ChatMessage.DeliveryStatus.UNSENT) {
            status.setImageDrawable(null);
            status.setVisibility(View.GONE);
        } else if (iMessageModel.getChatMessage().getDeliveryStatus() == ChatMessage.DeliveryStatus.NEW) {
            status.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.status_new));
            status.setVisibility(View.VISIBLE);
        } else if (iMessageModel.getChatMessage().getDeliveryStatus() == ChatMessage.DeliveryStatus.DELIVERED) {
            status.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.status_delivered));
            status.setVisibility(View.VISIBLE);
        } else if (iMessageModel.getChatMessage().getDeliveryStatus() == ChatMessage.DeliveryStatus.READ) {
            status.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.status_read));
            status.setVisibility(View.VISIBLE);
        } else {
        }

        time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

        if (iMessageModel.getChatMessage().getMediaType() == ChatMessage.MediaType.IMAGE && iMessageModel.getChatMessage() != null && iMessageModel.getChatMessage().getLocalPath() != null && !TextUtils.isEmpty(iMessageModel.getChatMessage().getLocalPath())) {
            ll_audio.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            if(iMessageModel.getChatMessage().getBody()==null || TextUtils.isEmpty(iMessageModel.getChatMessage().getBody())) {
                fl_resend.setVisibility(View.VISIBLE);
                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(XmppClient.chatActivity!=null)
                        {
                            XmppClient.chatActivity.uploadfile(iMessageModel.getChatMessage().getLocalPath(),iMessageModel.getChatMessage().getMessageId());
                        }
                    }
                });
            }
            else {
                fl_resend.setVisibility(View.GONE);
//                bubble.setForeground(null);
            }
            message.setVisibility(View.GONE);
            getImageLoader().loadImage(image, iMessageModel.getChatMessage().getLocalPath(), iMessageModel);
        } else if (iMessageModel.getChatMessage().getMediaType() == ChatMessage.MediaType.AUDIO && iMessageModel.getChatMessage() != null && iMessageModel.getChatMessage().getLocalPath() != null && !TextUtils.isEmpty(iMessageModel.getChatMessage().getLocalPath())) {
            ll_audio.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            message.setVisibility(View.GONE);
            if(iMessageModel.getChatMessage().getBody()==null || TextUtils.isEmpty(iMessageModel.getChatMessage().getBody())) {
                fl_resend.setVisibility(View.VISIBLE);
                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(XmppClient.chatActivity!=null)
                        {
                            XmppClient.chatActivity.uploadfile(iMessageModel.getChatMessage().getLocalPath(),iMessageModel.getChatMessage().getMessageId());
                        }
                    }
                });
            }
            else {
                fl_resend.setVisibility(View.GONE);
//                bubble.setForeground(null);
            }

            voicePlayerView.setAudio(iMessageModel.getChatMessage().getLocalPath());
        } else {
            ll_audio.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
//            bubble.setForeground(null);
            fl_resend.setVisibility(View.GONE);
            message.setText(iMessageModel.getText());

        }

    }

}
