package ca.uwaterloo.speakingprep;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.*;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = "SpeakingPrep";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private ImageView record = null;
    private TextView status = null;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private ActionButton actionButton;
    private Timer timer;
    private ImageView nextQuestion = null;
    private ImageView previousQuestion = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        status = (TextView)rootView.findViewById(R.id.status);

        nextQuestion = (ImageView)rootView.findViewById(R.id.next_question);
        nextQuestion.setImageAlpha(255/4);
        nextQuestion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        nextQuestion.setImageAlpha(255);
                        return true;
                    case (MotionEvent.ACTION_UP):
                        nextQuestion.setImageAlpha(255 / 4);
                        return true;
                }
                return false;
            }
        });

        previousQuestion = (ImageView)rootView.findViewById(R.id.previous_question);
        previousQuestion.setImageAlpha(255/4);
        previousQuestion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        previousQuestion.setImageAlpha(255);
                        return true;
                    case (MotionEvent.ACTION_UP):
                        previousQuestion.setImageAlpha(255 / 4);
                        return true;
                }
                return false;
            }
        });

        record = (ImageView) rootView.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onRecord();
            }
        });

        // Configurate action button
        actionButton = (ActionButton)rootView.findViewById(R.id.action_button);
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.fab_plus_icon));
        actionButton.setButtonColor(Color.parseColor("#E91E63"));
        actionButton.setButtonColorPressed(Color.parseColor("#D81B60"));

        //playButton = (Button) rootView.findViewById(R.id.play);
        //playButton.setOnClickListener(new View.OnClickListener(){
        //    @Override
        //    public void onClick(View v) {
        //        onPlay();
        //    }
        //});

        return rootView;
    }

    private void onRecord() {
        if (!isRecording) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Failed to record. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Change record button to red
            record.setColorFilter(Color.parseColor("#CC0000"), PorterDuff.Mode.SRC_ATOP);

            // Change status and its color
            status.setTextColor(Color.parseColor("#CC0000"));
            status.setText("Recording.");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (status.getText().toString()) {
                                case "Recording.":
                                    status.setText("Recording..");
                                    break;
                                case "Recording..":
                                    status.setText("Recording...");
                                    break;
                                default:
                                    status.setText("Recording.");
                                    break;
                            }
                        }
                    });
                }
            },1000,1000);
            isRecording = true;
        } else {
            record.setColorFilter(null);
            status.setTextColor(Color.parseColor("#00B300"));
            status.setText("Ready For Recording");
            timer.cancel();
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            isRecording = false;
        }
    }

    private void onPlay() {
        if (!isPlaying) {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(mFileName);
                mPlayer.prepare();
                mPlayer.start();
                isPlaying = true;
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
        } else {
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
        }
    }
}
