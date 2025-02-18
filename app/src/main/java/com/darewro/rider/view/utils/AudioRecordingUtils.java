package com.darewro.rider.view.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

public class AudioRecordingUtils {

    private static final String LOG_TAG = "AudioRecord";
    private static AudioRecordingUtils INSTANCE = null;
    private String fileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private int second = -1,minute=0,hour=0;
    private CountDownTimer countDownTimer;

    public String getFileName() {
        return fileName;
    }

    public static AudioRecordingUtils getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AudioRecordingUtils();
        }
        return INSTANCE;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void onRecord(boolean start) {
        if (start) {
            //startRecording();
        } else {
            //stopRecording();
        }
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    public void startPlaying() {
        second = 0;
        mPlayer = new MediaPlayer();
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

    public void startRecording(TextView textView) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setOutputFile(fileName);
        showTimer(textView);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "prepare() failed");
        }

        try {
            mRecorder.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void stopRecording() {
        try {
            if (mRecorder != null) {

                if(countDownTimer!=null)
                    countDownTimer.cancel();

                hour=0;
                minute=0;
                second=-1;

                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
                startPlaying();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showTimer(TextView textView) {
        countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                second++;
                textView.setText(recorderTime());
            }
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }

    //recorder time
    public String recorderTime() {
        if (second == 60) {
            minute++;
            second = 0;
        }
        if (minute == 60) {
            hour++;
            minute = 0;
        }
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
