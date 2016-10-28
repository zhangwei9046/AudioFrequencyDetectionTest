package com.example.ygzhang.audiodetectiontest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import com.example.ygzhang.audiodetectiontest.utils.Complex;
import com.example.ygzhang.audiodetectiontest.utils.FFT;
import com.example.ygzhang.audiodetectiontest.utils.FrequencyScanner;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private static final String LOG_TAG = "AudioDetectionTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;

    private EditText mEditText = null;

    private FrequencyScanner frequencyScanner = null;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
//            startPlaying();
        } else {
            stopPlaying();
        }
    }

//    private void startPlaying() {
//        mPlayer = new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            mPlayer.start();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
//        mRecorder = new MediaRecorder();
//        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mRecorder.setOutputFile(mFileName);
//        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        // construct AudioRecord to record audio from microphone with sample rate of 44100Hz
        minSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, minSize);
        System.out.println("minSize:" + minSize);
        recorder.startRecording();
        short[] buffer = new short[minSize];
        recorder.read(buffer, 0, minSize); // record data from mic into buffer
//        byte[] bData = short2byte(buffer);
//        double[] micBufferData = byte2double(bData);
//        Complex[] fftArray = double2Complex(micBufferData);
        frequencyScanner = new FrequencyScanner();
        double frequency = frequencyScanner.extractFrequency(buffer, 44100);
        System.out.println(frequency);
        logToDisplay("Frequency: " + frequency);
//        isRecording = true;
//        recordingThread = new Thread(new Runnable() {
//            public void run() {
//                readAudioStream();
//            }
//        }, "AudioRecorder Thread");
//        recordingThread.start();







//        try {
//            mRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "prepare() failed");
//        }
//
//        mRecorder.start();
    }

    int minSize = 0;

    public void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
//                mEditText = (EditText)MainActivity.this
//                        .findViewById(R.id.FrequencyText);
                mEditText.append(line+"\n");
            }
        });
    }









    private Complex[] double2Complex(double[] micBufferData) {
        int bufferSize = micBufferData.length;
        Complex[] fftTempArray = new Complex[bufferSize];
        for (int i=0; i<bufferSize; i++)
        {
            fftTempArray[i] = new Complex(micBufferData[i], 0);
        }
        Complex[] fftArray = FFT.fft(fftTempArray);
        return fftArray;
    }

    private double[] byte2double(byte[] bData) {
        int bytesRecorded = bData.length;
        double[] micBufferData = new double[4096];
        final int bytesPerSample = 2; // As it is 16bit PCM
        final double amplification = 100.0; // choose a number as you like
        for (int index = 0, floatIndex = 0; index < bytesRecorded - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
            double sample = 0;
            for (int b = 0; b < bytesPerSample; b++) {
                int v = bData[index + b];
                if (b < bytesPerSample - 1 || bytesPerSample == 1) {
                    v &= 0xFF;
                }
                sample += v << (b * 8);
            }
            double sample32 = amplification * (sample / 32768.0);
            micBufferData[floatIndex] = sample32;
        }
        return micBufferData;
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }


//    public static int calculate(int sampleRate, short [] audioData){
//
//        int numSamples = audioData.length;
//        int numCrossing = 0;
//        for (int p = 0; p < numSamples-1; p++)
//        {
//            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
//                    (audioData[p] < 0 && audioData[p + 1] >= 0))
//            {
//                numCrossing++;
//            }
//        }
//
//        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
//        float numCycles = numCrossing/2;
//        float frequency = numCycles/numSecondsRecorded;
//
//        return (int)frequency;
//    }

    private void stopRecording() {
//        mRecorder.stop();
//        mRecorder.release();
//        mRecorder = null;
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    public MainActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audioDetectionTest.3gp";
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        LinearLayout ll = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
//        mPlayButton = new PlayButton(this);
//        ll.addView(mPlayButton,
//                new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        0));
        mEditText = new EditText(this);
        ll.addView(mEditText,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        setContentView(ll);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
