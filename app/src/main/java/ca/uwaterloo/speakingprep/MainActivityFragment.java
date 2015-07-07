package ca.uwaterloo.speakingprep;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.software.shell.fab.*;

import java.io.IOException;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = "SpeakingPrep";
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private ImageView record = null;
    private boolean isRecording = false;
    private boolean isPlaying = false;
    private ActionButton actionButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

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
        actionButton.setRippleEffectEnabled(true);


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
                Log.e(LOG_TAG, "prepare() failed");
            }

            isRecording = true;
        } else {
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
