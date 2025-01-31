package com.aman.voicerecognition;

/**
 * @Author: Amanpreet Kaur
 * @Date: 31-12-2024 10:29
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WavClass {
    String tempWavFile = "final_record.wav";
    public MediaRecorder mediaRecorder = null;
    boolean isRecording = false;
    private Context context;
    private File finalFile;
    private static final String TAG = "WavClass";
    private long startTime = 0; // Start time of recording
    private Handler handler = new Handler(Looper.getMainLooper()); // UI Thread Handler
    private TextView timerTextView; // UI component to show time

    public WavClass(Context context) {
        this.context = context;
        createFile();
    }

    private void createFile() {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        try {
            finalFile = File.createTempFile(tempWavFile, ".wav", storageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("MissingPermission")
    public void startRecording(TextView textView) {
        try {
            this.timerTextView = textView;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(finalFile.getAbsolutePath());

            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Log.d(TAG, "Recording started");

            startTime = System.currentTimeMillis(); // Store the start time
            handler.post(updateTimerRunnable); // Start updating time
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to start recording: " + e.getMessage());
        }
    }

    public void stopRecording() {
        try {
            if (mediaRecorder != null && isRecording) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                Log.d(TAG, "Recording stopped");
                handler.removeCallbacks(updateTimerRunnable); // Stop updating time
//                timerTextView.setText("00:00"); // Reset timer
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to stop recording: " + e.getMessage());
        }
    }
    // Runnable to update the timer
    private final Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                String formattedTime = formatTime(elapsedTime);
                timerTextView.setText(formattedTime);
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    // Format milliseconds into mm:ss
    private String formatTime(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getFinalFile() {
        return finalFile.getAbsolutePath();
    }

    /*public WavClass(Context context){
        try{
            this.context = context;
            bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            createImageFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createImageFile() {
        long timeStamp = Calendar.getInstance().getTimeInMillis();
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        filePath = storageDir.getPath();
        try {
            tempFile = File.createTempFile(
                    //String.valueOf(timeStamp),
                    tempRawFile,
                    ".raw",
                    storageDir
            );
            finalFile = File.createTempFile(
                    //String.valueOf(timeStamp),
                    tempWavFile,
                    ".wav",
                    storageDir
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeRawData(){
        try{
            if(filePath != null) {
                byte[] data = new byte[bufferSize];
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                if(fileOutputStream != null){
                    int read;
                    while (isRecording){
                        read = recorder.read(data, 0, bufferSize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                fileOutputStream.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                fileOutputStream.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void wavHeader(FileOutputStream fileOutputStream,long totalAudioLen,long totalDataLen,int channels,long byteRate){
        try {
            byte[] header = new byte[44];
            header[0] = 'R'; // RIFF/WAVE header
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';
            header[12] = 'f'; // 'fmt ' chunk
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = 16; // 4 bytes: size of 'fmt ' chunk
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;
            header[20] = 1; // format = 1
            header[21] = 0;
            header[22] = (byte) channels;
            header[23] = 0;
            header[24] = (byte) ((long) sampleRate & 0xff);
            header[25] = (byte) (((long) sampleRate >> 8) & 0xff);
            header[26] = (byte) (((long) sampleRate >> 16) & 0xff);
            header[27] = (byte) (((long) sampleRate >> 24) & 0xff);
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);
            header[32] = (byte) (2 * 16 / 8); // block align
            header[33] = 0;
            header[34] = bpp; // bits per sample
            header[35] = 0;
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            header[40] = (byte) (totalAudioLen & 0xff);
            header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
            header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
            header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
            fileOutputStream.write(header, 0, 44);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createWavFile(File tempPath,File wavPath){
        try {
            FileInputStream fileInputStream = new FileInputStream(tempPath);
            FileOutputStream fileOutputStream = new FileOutputStream(wavPath);
            byte[] data = new byte[bufferSize];
            int channels = 2;
            long byteRate = bpp * sampleRate * channels / 8;
            long totalAudioLen = fileInputStream.getChannel().size();
            long totalDataLen = totalAudioLen + 36;
            wavHeader(fileOutputStream,totalAudioLen,totalDataLen,channels,byteRate);
            while (fileInputStream.read(data) != -1) {
                fileOutputStream.write(data);
            }
            fileInputStream.close();
            fileOutputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    @SuppressLint("MissingPermission")
    public void startRecording(){
        try{
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channel,audioEncoding, bufferSize);
            int status = recorder.getState();
            if(status == 1){
                recorder.startRecording();
                isRecording = true;
            }
            recordingThread = new Thread(this::writeRawData);
            recordingThread.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void stopRecording(){
        try{
            if(recorder != null) {
                isRecording = false;
                int status = recorder.getState();
                if (status == 1) {
                    recorder.stop();
                }
                recorder.release();
                recordingThread = null;
                createWavFile(tempFile, finalFile);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getFinalFile() {
        return finalFile.getPath();
    }*/
}
