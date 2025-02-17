package com.darewro.riderApp.view.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class AudioPlayerUtils {

    private static final String LOG_TAG = "AudioRecord";
    private static AudioPlayerUtils INSTANCE = null;
    private String fileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            stopPlaying();
        }
    };

    public static AudioPlayerUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AudioPlayerUtils();
        }
        return INSTANCE;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    public void startPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
//            mPlayer.reset();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(onCompletionListener);
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
}
