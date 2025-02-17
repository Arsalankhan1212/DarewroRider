package com.darewro.riderApp.view.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.darewro.riderApp.data.db.model.ChatMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class FileDownloadImageUtils extends AsyncTask<Void, Integer, Boolean> {

    Context context;
    String url;
    String fileName;
    String messageId;
    ProgressBar progressBar;
    PorterShapeImageView imageView;
    ImageButton resend;

    public FileDownloadImageUtils(Context context, String url, String fileName, String messageId, ProgressBar progressBar, PorterShapeImageView imageView, ImageButton resend) {
        this.context = context;
        this.url = url;
        this.fileName = fileName;
        this.messageId = messageId;
        this.progressBar = progressBar;
        this.imageView = imageView;
        this.resend = resend;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        if (resend != null)
            resend.setVisibility(View.GONE);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {

            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];

            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(new File(AppUtils.getDirectory(), fileName)));
            fos.write(buffer);
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            return false; // swallow a 404
        } catch (IOException e) {
            return false; // swallow a 404
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        //update chat message from message id, set h of downloaded picture


        if (progressBar != null)
            progressBar.setVisibility(View.GONE);
        if (aBoolean) {
            String path = AppUtils.getDirectory() + File.separator + fileName;
            List<ChatMessage> chatMessages = DbUtils.getMessageById(messageId);
            for (ChatMessage chatMessage : chatMessages) {
                chatMessage.setLocalPath(path);
                chatMessage.save();
            }
            if (imageView != null) {
                AppUtils.loadPicture(context, imageView, path);
            }

            if (resend != null) {
                resend.setVisibility(View.GONE);
            }
        } else {
            if (resend != null) {
                resend.setVisibility(View.VISIBLE);
            }

        }
    }

}
