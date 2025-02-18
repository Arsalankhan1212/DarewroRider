package com.darewro.rider.view.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import com.darewro.rider.R;

public class NotificationSoundService extends Service {
    private MediaPlayer mediaPlayer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SoundService", "Playing notification sound...");

        mediaPlayer = MediaPlayer.create(this, R.raw.instrumental_ring);
        mediaPlayer.setOnCompletionListener(mp -> stopSelf());
        mediaPlayer.start();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
