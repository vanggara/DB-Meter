package com.example.soundmeter;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView db;
    TextView amp;
    MediaRecorder mRecorder;
    Thread runner;

    final Runnable updater = new Runnable(){

        public void run(){
            updateTv();
        };
    };
    final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amp = (TextView) findViewById(R.id.amp);
        db = (TextView) findViewById(R.id.db);


        if (runner == null)
        {
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(100);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    public void onResume()
    {
        super.onResume();
        startRecorder();
    }

    public void onPause()
    {
        super.onPause();
        stopRecorder();

    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try
            {
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try
            {
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
//        mStatusView.setText(Integer.toString((int)soundDb()) + " dB");
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(String.valueOf(getAmplitude()));
        amp.setText(Integer.toString((int)getAmplitude()) + " amp");
//        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
//        db.setText(Integer.toString((int)soundDb()) + " dB");
    }
    public double soundDb(){
        double db =  20 * Math.log10(getAmplitude());
        if(db>0)
        {
            return db;
        }
        else
        {
            return 0;
        }
    }
    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private int result;

        @Override
        protected String doInBackground(String... params) {

            result = (int) (20 * Math.log10(Double.parseDouble(params[0])));
            if(result > 0 && result <= 20)
            {
                return String.valueOf(result) + " dB";
            }
            else
            {
                return String.valueOf(result) + " dB";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            db.setText(result);
        }


        @Override
        protected void onPreExecute() {
        }


        @Override
        protected void onProgressUpdate(String... text) {
            db.setText(text[0]);
        }
    }
}
