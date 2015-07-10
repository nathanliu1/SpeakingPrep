package ca.uwaterloo.speakingprep;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import android.widget.Button;
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
    private static MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private static ImageView record = null;
    private TextView recordTV = null;
    private ImageView timer = null;
    private TextView timerTV = null;
    private ImageView replay = null;
    private TextView replayTV = null;
    private ImageView shuffle = null;
    private TextView shuffleTV = null;
    private ImageView questionList = null;
    private TextView questionListTV = null;
    private ImageView save = null;
    private TextView saveTV = null;
    private TextView addAQuestionTV = null;
    private ImageView aboutAuthor = null;
    private ImageView setting = null;
    private static TextView status = null;
    private TextView question = null;
    private static boolean isRecording = false;
    private boolean isPlaying = false;
    private ActionButton actionButton;
    private static Timer mTimer;
    private ImageView nextQuestion = null;
    private ImageView previousQuestion = null;
    private AlphaAnimation fadeIn = new AlphaAnimation(0f,1f);
    private AlphaAnimation fadeOut = new AlphaAnimation(1f,0f);
    private boolean isAnimating = false; // Check if animation is going on
    private boolean isNext = true; // Check if user wants to go to next question or previous question
    private static int secondElapsed = 0;
    private static int minuteElapsed = 0;
    private static TimerTask updateStatus = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recordTV = (TextView)rootView.findViewById(R.id.record_text);
        recordTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Record","Record your answer. Press to start recording, press again to stop recording.",rootView.getContext());
            }
        });
        timer = (ImageView)rootView.findViewById(R.id.timer);
        timerTV = (TextView)rootView.findViewById(R.id.timer_text);
        timerTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Timer","Set a preparation time and maximum time for recording.",rootView.getContext());
            }
        });

        replay = (ImageView)rootView.findViewById(R.id.replay);
        replayTV = (TextView)rootView.findViewById(R.id.replay_text);
        replayTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Replay","Replay your previous recorded answer. You can also enable auto replay in the setting so that you don't need to press this button every time",rootView.getContext());
            }
        });

        shuffle = (ImageView)rootView.findViewById(R.id.shuffle);
        shuffleTV = (TextView)rootView.findViewById(R.id.shuffle_text);
        shuffleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog("Shuffle","Shuffle your questions",rootView.getContext());
            }
        });

        questionList = (ImageView)rootView.findViewById(R.id.question_list);
        questionListTV = (TextView)rootView.findViewById(R.id.question_list_text);
        questionListTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Question List","Display a collection of your questions",rootView.getContext());
            }
        });

        save = (ImageView)rootView.findViewById(R.id.save);
        saveTV = (TextView)rootView.findViewById(R.id.save_text);
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog("Save","Save your recorded answer to your internal storage. You can also enable auto save in the setting so that you don't need to press this button every time",rootView.getContext());
            }
        });

        addAQuestionTV = (TextView)rootView.findViewById(R.id.add_a_question_text);
        addAQuestionTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Add A Question","Add a question to your collection",rootView.getContext());
            }
        });

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        status = (TextView) rootView.findViewById(R.id.status);
        question = (TextView)rootView.findViewById(R.id.question);

        // Configure animations
        animateConfig();

        nextQuestion = (ImageView) rootView.findViewById(R.id.next_question);
        nextQuestion.setImageAlpha(255 / 4);
        nextQuestion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // You can only change questions when you are not recording
                if (!isRecording) {
                    switch (event.getAction()) {
                        case (MotionEvent.ACTION_DOWN):
                            nextQuestion.setImageAlpha(255);
                            return true;
                        case (MotionEvent.ACTION_UP):
                            // Next Question
                            nextQuestion.setImageAlpha(255 / 4);
                            isNext = true;
                            if (!isAnimating) {
                                question.startAnimation(fadeOut);
                            }
                            return true;
                    }
                }
                return false;
            }
        });

        previousQuestion = (ImageView) rootView.findViewById(R.id.previous_question);
        previousQuestion.setImageAlpha(255 / 4);
        previousQuestion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // You can only change questions when you are not recording
                if (!isRecording) {
                    switch (event.getAction()) {
                        case (MotionEvent.ACTION_DOWN):
                            previousQuestion.setImageAlpha(255);
                            return true;
                        case (MotionEvent.ACTION_UP):
                            // Previous Question
                            previousQuestion.setImageAlpha(255 / 4);
                            isNext = false;
                            if (!isAnimating) {
                                question.startAnimation(fadeOut);
                            }
                            return true;
                    }
                }
                return false;
            }
        });

        record = (ImageView) rootView.findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord();
            }
        });

        aboutAuthor = (ImageView) rootView.findViewById(R.id.about);
        aboutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(rootView.getContext());
                dialog.setContentView(R.layout.about_author);
                dialog.setTitle("About Authors & This App");
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;

                dialog.show();
                dialog.getWindow().setAttributes(lp);

                Button contact = (Button)dialog.findViewById(R.id.contact_us);
                contact.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"chewong@uwaterloo.ca"});
                        try {
                            startActivity(Intent.createChooser(i, "Send me an e-mail"));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button github = (Button)dialog.findViewById(R.id.github);
                github.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/chewong/SpeakingPrep"));
                        startActivity(browserIntent);
                    }
                });
            }
        });
        // Configure action button
        actionButton = (ActionButton) rootView.findViewById(R.id.action_button);
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

        setting = (ImageView)rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent goToSetting = new Intent(rootView.getContext(),Setting.class);
                startActivity(goToSetting);
            }
        });

        return rootView;
    }

    private void onRecord() {
        if (!isRecording && !isAnimating) {
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
            status.setText(String.format("%02d:%02d", minuteElapsed, secondElapsed));
            mTimer = new Timer();
            updateStatus = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Counter
                            if (secondElapsed == 59) {
                                secondElapsed = 0;
                                minuteElapsed++;
                            } else {
                                secondElapsed++;
                            }
                            status.setText(String.format("%02d:%02d",minuteElapsed,secondElapsed));
                        }
                    });
                }
            };

            mTimer.schedule(updateStatus,1000,1000);
            isRecording = true;
        } else {
            record.setColorFilter(null);
            status.setTextColor(Color.parseColor("#00B300"));
            status.setText("Ready For Recording");
            secondElapsed = 0;
            minuteElapsed = 0;
            if (timer != null && mRecorder != null) {
                mTimer.purge();
                mTimer.cancel();
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
            isRecording = false;
            Toast.makeText(getActivity(),"Record Successful. Available for replay and save",Toast.LENGTH_SHORT).show();
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

    private void animateConfig() {
        // Fade In
        fadeIn.setDuration(750);
        fadeIn.setFillAfter(true);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // Fade Out
        fadeOut.setDuration(750);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isNext)
                    question.setText("Why do you want to work in this company?");
                else
                    question.setText("Can you tell me a little bit about yourself?");
                question.startAnimation(fadeIn);
             }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void release() {
        if(mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
        }
        // Stop recording
        status.setText("Ready For Recording");
        status.setTextColor(Color.parseColor("#00B300"));
        record.setColorFilter(null);
        secondElapsed = 0;
        minuteElapsed = 0;
        isRecording = false;
    }

    private void buildDialog(String title, String message, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Got it",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.create().show();
    }
}