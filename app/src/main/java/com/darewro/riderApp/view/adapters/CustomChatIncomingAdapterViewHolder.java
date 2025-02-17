package com.darewro.riderApp.view.adapters;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.darewro.riderApp.App;
import com.darewro.riderApp.R;
import com.darewro.riderApp.data.db.model.ChatMessage;
import com.darewro.riderApp.view.chatLibViewModel.IMessageModel;
import com.darewro.riderApp.view.utils.AppUtils;
import com.darewro.riderApp.view.utils.FileDownloadAudioUtils;
import com.darewro.riderApp.view.utils.FileDownloadImageUtils;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class CustomChatIncomingAdapterViewHolder extends MessagesListAdapter.IncomingMessageViewHolder<IMessageModel> {

    TextView message, time;
    PorterShapeImageView image;
    LinearLayout ll_audio;
    FrameLayout bubble, fl_resend;
    VoicePlayerView voicePlayerView;
    ProgressBar downloadProgress;
    ImageButton resend;

    public CustomChatIncomingAdapterViewHolder(View itemView) {
        super(itemView);

        message = itemView.findViewById(com.stfalcon.chatkit.R.id.messageText);
        time = itemView.findViewById(R.id.messageTime);
        image = itemView.findViewById(R.id.image);
        fl_resend = itemView.findViewById(R.id.fl_resend);
        resend = itemView.findViewById(R.id.resend);
        ll_audio = itemView.findViewById(R.id.ll_audio);
        voicePlayerView = itemView.findViewById(R.id.voicePlayerView);
        bubble = itemView.findViewById(R.id.bubble);
        downloadProgress = itemView.findViewById(R.id.downloadProgress);

    }

    @Override
    public void onBind(final IMessageModel iMessageModel) {

        if (iMessageModel.getChatMessage().getMediaType() == ChatMessage.MediaType.IMAGE && iMessageModel.getChatMessage() != null) {

            if (iMessageModel.getChatMessage().getLocalPath() != null && !TextUtils.isEmpty(iMessageModel.getChatMessage().getLocalPath())) {
                ll_audio.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
                fl_resend.setVisibility(View.GONE);
                getImageLoader().loadImage(image, iMessageModel.getChatMessage().getLocalPath(), iMessageModel);
                message.setVisibility(View.GONE);
                message.setText(iMessageModel.getText());
                downloadProgress.setVisibility(View.GONE);
                time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

            } else if (iMessageModel.getChatMessage().getBody() != null && !TextUtils.isEmpty(iMessageModel.getChatMessage().getBody())) {
                ll_audio.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
                fl_resend.setVisibility(View.GONE);
                getImageLoader().loadImage(image, iMessageModel.getChatMessage().getBody(), iMessageModel);
                message.setVisibility(View.GONE);
                message.setText(iMessageModel.getText());

                downloadProgress.setVisibility(View.GONE);

                String url = iMessageModel.getChatMessage().getBody();
                final String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadProgress.setVisibility(View.VISIBLE);
                        FileDownloadImageUtils fileDownloadUtils = new FileDownloadImageUtils(App.getInstance().getApplicationContext(), iMessageModel.getChatMessage().getBody(), fileName, iMessageModel.getChatMessage().getMessageId(), downloadProgress, image, resend);
                        fileDownloadUtils.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });

                time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

            }
        } else if (iMessageModel.getChatMessage().getMediaType() == ChatMessage.MediaType.AUDIO && iMessageModel.getChatMessage() != null) {

            ll_audio.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.GONE);


            if (iMessageModel.getChatMessage().getLocalPath() != null && !TextUtils.isEmpty(iMessageModel.getChatMessage().getLocalPath())) {
                fl_resend.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
//                bubble.setForeground(null);
                voicePlayerView.setAudio(iMessageModel.getChatMessage().getLocalPath());
                time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

            } else if (iMessageModel.getChatMessage().getBody() != null && !TextUtils.isEmpty(iMessageModel.getChatMessage().getBody())) {
                fl_resend.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
//                bubble.setForeground(App.getInstance().getResources().getDrawable(R.drawable.fg_custom_outcoming_message));
                voicePlayerView.setAudio(iMessageModel.getChatMessage().getBody());

                String url = iMessageModel.getChatMessage().getBody();
                final String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadProgress.setVisibility(View.VISIBLE);

                        FileDownloadAudioUtils fileDownloadAudioUtils = new FileDownloadAudioUtils(App.getInstance().getApplicationContext(), iMessageModel.getChatMessage().getBody(), fileName, iMessageModel.getChatMessage().getMessageId(), downloadProgress, voicePlayerView, resend);
                        fileDownloadAudioUtils.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });

                time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

            }
            else
            {

                message.setVisibility(View.VISIBLE);
                time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

            }

        } else {

            ll_audio.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
//            bubble.setForeground(null);
            fl_resend.setVisibility(View.GONE);
            message.setVisibility(View.VISIBLE);
            message.setText(iMessageModel.getText());
            time.setText(AppUtils.getConvertedDateFromOneFormatToOther(iMessageModel.getChatMessage().getTimestamp(), AppUtils.FORMAT14, AppUtils.FORMAT5));

        }
    }
}
