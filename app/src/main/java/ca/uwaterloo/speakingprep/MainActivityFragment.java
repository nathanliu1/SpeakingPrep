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
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
    private static ImageView timer = null;
    private TextView timerTV = null;
    private static ImageView replay = null;
    private TextView replayTV = null;
    private static ImageView shuffle = null;
    private TextView shuffleTV = null;
    private static ImageView questionList = null;
    private TextView questionListTV = null;
    private static ImageView save = null;
    private TextView saveTV = null;
    private TextView addAQuestionTV = null;
    private ImageView aboutAuthor = null;
    private ImageView setting = null;
    private static TextView status = null;
    private static TextView question = null;
    private static boolean isRecording = false;
    private boolean isPlaying = false;
    private ActionButton actionButton;
    private static Timer mTimer;
    private ImageView nextQuestion = null;
    private ImageView previousQuestion = null;
    private static AlphaAnimation fadeIn = new AlphaAnimation(0f,1f);
    private static AlphaAnimation fadeOut = new AlphaAnimation(1f,0f);
    private boolean isAnimating = false; // Check if animation is going on
    private static boolean isNext = true; // Check if user wants to go to next question or previous question
    private static int secondElapsed = 0;
    private static int minuteElapsed = 0;
    private static TimerTask updateStatus = null;
    private int preparationTime = 0;
    private int preparationMinute;
    private int preparationSecond;
    private int recordDuration = 0;
    private EditText preparationTimeET;
    private EditText recordDurationET;
    private String selectedCategory = "Uncategorised";
    private boolean isPreparing = false;
    public static int questionCounter = 0;
    private Toast warning;
    private boolean isShuffle = false;
    private List<Question> shuffledQuestion;

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
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    LayoutInflater li = LayoutInflater.from(rootView.getContext());
                    final View promptsView = li.inflate(R.layout.timer, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());

                    // Get EditText
                    preparationTimeET = (EditText) promptsView.findViewById(R.id.preparationTime);
                    recordDurationET = (EditText) promptsView.findViewById(R.id.recordDuration);

                    // Display time when user open timer dialog
                    if (preparationTime != 0) {
                        preparationTimeET.setText("" + preparationTime);
                    }
                    if (recordDuration != 0) {
                        recordDurationET.setText("" + recordDuration);
                    }

                    alertDialogBuilder.setView(promptsView);
                    alertDialogBuilder
                            .setTitle("Set Timer")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Get preparation time
                                            if (preparationTimeET.getText().toString().equals("")) {
                                                preparationTime = 0;
                                            } else {
                                                preparationTime = Integer.parseInt(preparationTimeET.getText().toString());
                                            }

                                            // Get record duration
                                            if (recordDurationET.getText().toString().equals("")) {
                                                recordDuration = 0;
                                            } else {
                                                recordDuration = Integer.parseInt(recordDurationET.getText().toString());
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        timerTV = (TextView)rootView.findViewById(R.id.timer_text);
        timerTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Timer","Set a preparation time and maximum time for recording.",rootView.getContext());
            }
        });

        replay = (ImageView)rootView.findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay();
            }
        });
        replay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });
        replayTV = (TextView)rootView.findViewById(R.id.replay_text);
        replayTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                buildDialog("Replay","Replay your previous recorded answer. You can also enable auto replay in the setting so that you don't need to press this button every time",rootView.getContext());
            }
        });

        shuffle = (ImageView)rootView.findViewById(R.id.shuffle);
        shuffle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    if (!isShuffle) {
                        shuffle.setColorFilter(Color.parseColor("#00CC00"), PorterDuff.Mode.SRC_ATOP);
                        isShuffle = true;
                    } else {
                        shuffle.setColorFilter(null);
                        isShuffle = false;
                    }
                }
            }
        });

        shuffleTV = (TextView)rootView.findViewById(R.id.shuffle_text);
        shuffleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog("Shuffle","Shuffle your questions",rootView.getContext());
            }
        });

        questionList = (ImageView)rootView.findViewById(R.id.question_list);
        questionList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(rootView.getContext(),ListViewActivity.class));
            }
        });
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
                            if (!isAnimating && QuestionCategory.getAllQuestion().size() > questionCounter + 1) {
                                question.startAnimation(fadeOut);
                            } else if (!isAnimating){
                                showToast("You Have Reached The End Of Your List");
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
                            if (!isAnimating && QuestionCategory.getAllQuestion().size() > 1 && questionCounter != 0) {
                                question.startAnimation(fadeOut);
                            } else if (!isAnimating) {
                                showToast("You Have Reached The Beginning Of Your List");
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
                if (!isRecording) {
                    final Dialog dialog = new Dialog(rootView.getContext());
                    dialog.setContentView(R.layout.about_author);
                    dialog.setTitle("About Authors & This App");
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);

                    Button contact = (Button) dialog.findViewById(R.id.contact_us);
                    contact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"chewong@uwaterloo.ca"});
                            try {
                                startActivity(Intent.createChooser(i, "Send us an e-mail"));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Button github = (Button) dialog.findViewById(R.id.github);
                    github.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/chewong/SpeakingPrep"));
                            startActivity(browserIntent);
                        }
                    });
                } else {
                    Toast.makeText(rootView.getContext(),"You have to stop recording first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configure action button
        actionButton = (ActionButton) rootView.findViewById(R.id.action_button);
        actionButton.setImageDrawable(getResources().getDrawable(R.drawable.fab_plus_icon));
        actionButton.setButtonColor(Color.parseColor("#E91E63"));
        actionButton.setButtonColorPressed(Color.parseColor("#D81B60"));
        actionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    LayoutInflater li = LayoutInflater.from(rootView.getContext());
                    final View addQuestionView = li.inflate(R.layout.add_question, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());

                    // Set spinner and onItemSelectedlistener
                    final Spinner category = (Spinner) addQuestionView.findViewById(R.id.spinner);
                    category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String categoryName = ((TextView) view).getText().toString();
                            // If user wants to add a category
                            if (categoryName.equals("<Add A Category>")) {
                                LayoutInflater li = LayoutInflater.from(rootView.getContext());
                                final View addCategoryView = li.inflate(R.layout.add_category, null);
                                final EditText et = (EditText) addCategoryView.findViewById(R.id.editText);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                alertDialogBuilder.setView(addCategoryView);
                                alertDialogBuilder
                                        .setTitle("Add A Category")
                                        .setCancelable(false)
                                        .setPositiveButton("Add",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Check if user inputs nothing
                                                        if (!et.getText().toString().equals("")) {
                                                            // Check if user input something that already exist in the spinner
                                                            if (!QuestionCategory.addCategory(et.getText().toString())) {
                                                                category.setSelection(getIndexSpinner(category, et.getText().toString()));
                                                            }
                                                            selectedCategory = et.getText().toString();
                                                            Log.e(LOG_TAG, selectedCategory);
                                                        } else {
                                                            Toast.makeText(rootView.getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                                                            category.setSelection(0);
                                                        }
                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        category.setSelection(0);
                                                        dialog.cancel();
                                                    }
                                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                selectedCategory = categoryName;
                                Log.e(LOG_TAG, selectedCategory);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    category.setAdapter(new QuestionCategory(rootView.getContext()).getCategory());
                    category.setSelection(getIndexSpinner(category, selectedCategory));

                    // Add a question here
                    final EditText questionText = (EditText) addQuestionView.findViewById(R.id.editText);
                    alertDialogBuilder.setView(addQuestionView);
                    alertDialogBuilder
                            .setTitle("Add A Question")
                            .setCancelable(false)
                            .setPositiveButton("Add",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (!questionText.getText().toString().equals("")) {
                                                 String TempQuestion = questionText.getText().toString();
                                                 if(TempQuestion.contains("\n")) {
                                                    String[] TempQuestionParts = TempQuestion.split("\n");
                                                     for (int i = 0; i < TempQuestionParts.length; i++) {
                                                         QuestionCategory.addQuestion(selectedCategory, new Question(TempQuestionParts[i]));
                                                     }
                                                 } else {
                                                    QuestionCategory.addQuestion(selectedCategory, new Question(TempQuestion));
                                                isNext = true;
                                                 }
                                                question.startAnimation(fadeOut);
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        setting = (ImageView)rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    Intent goToSetting = new Intent(rootView.getContext(), Setting.class);
                    startActivity(goToSetting);
                }
            }
        });

        return rootView;
    }

    private void onRecord() {
        if (!isRecording && !isAnimating) {
            // Create a media recorder and configure it
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncodingBitRate(44100);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // Set other images' alpha to 255/4
            timer.setImageAlpha(255 / 4);
            replay.setImageAlpha(255 / 4);
            shuffle.setImageAlpha(255 / 4);
            questionList.setImageAlpha(255 / 4);
            save.setImageAlpha(255 / 4);

            // Reset time elapsed
            secondElapsed = 0;
            minuteElapsed = 0;

            // Get preparation time, minute and second
            preparationMinute = 0;
            preparationSecond = 0;
            while (preparationTime >= 60) {
                preparationTime -= 60;
                preparationMinute++;
            }
            preparationSecond = preparationTime;

            // Get the original preparation time since we modified it above
            try {
                preparationTime = Integer.parseInt(preparationTimeET.getText().toString());
            } catch (Exception e) {
                preparationTime = 0;
            }

            // Change record button to red
            record.setColorFilter(Color.parseColor("#CC0000"), PorterDuff.Mode.SRC_ATOP);

            // Change status and its color
            status.setTextColor(Color.parseColor("#CC0000"));
            mTimer = new Timer();

            // TimeTask - run this every 1 second with 0 second initial delay
            updateStatus = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Count down
                            if (preparationSecond != 0 || preparationMinute != 0) {
                                isPreparing = true;
                                status.setText(String.format("-%02d:%02d",preparationMinute,preparationSecond));
                                if (preparationSecond == 0 && preparationMinute != 0) {
                                    preparationSecond = 59;
                                    preparationMinute--;
                                } else {
                                    preparationSecond--;
                                }
                            } else {
                                isPreparing = false;
                                if (secondElapsed == 0 && minuteElapsed == 0) {
                                    // Prepare and start recording
                                    try {
                                        mRecorder.prepare();
                                        mRecorder.start();
                                    } catch (Exception e) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Failed to record. Please try again.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    status.setText(String.format("%02d:%02d", minuteElapsed, secondElapsed));
                                } else if (recordDuration != 0 && secondElapsed + minuteElapsed * 60 - 1 == recordDuration) {
                                    // Stop when timer reaches limited record duration
                                    stopRecording();
                                } else {
                                    // Update current time elapsed
                                    status.setText(String.format("%02d:%02d", minuteElapsed, secondElapsed));
                                }
                                // Count up
                                if (secondElapsed == 59) {
                                    secondElapsed = 0;
                                    minuteElapsed++;
                                } else {
                                    secondElapsed++;
                                }
                            }
                        }
                    });
                }
            };
            mTimer.schedule(updateStatus,0,1000);
            isRecording = true;
        } else {
            stopRecording();
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
            // Release media player after finish playing the audio
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPlayer.release();
                    mPlayer = null;
                    isPlaying = false;
                }
            }, mPlayer.getDuration());
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
                if (isNext) {
                    questionCounter++;
                } else if (questionCounter >= 1) {
                    questionCounter--;
                }

                if (!isShuffle) {
                    question.setText(QuestionCategory.getAllQuestion().get(questionCounter).toString());
                } else {

                }

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
        isRecording = false;
    }

    private void buildDialog(String title, String message, Context context) {
        if (!isRecording) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.create().show();
        }
    }

    public static void resume() {
        status.setText("Ready For Recording");
        status.setTextColor(Color.parseColor("#00B300"));
        record.setColorFilter(null);
        secondElapsed = 0;
        minuteElapsed = 0;
        timer.setImageAlpha(255);
        replay.setImageAlpha(255);
        shuffle.setImageAlpha(255);
        questionList.setImageAlpha(255);
        save.setImageAlpha(255);
    }

    private void stopRecording() {
        record.setColorFilter(null);
        status.setTextColor(Color.parseColor("#00B300"));
        status.setText("Ready For Recording");
        if (timer != null && mRecorder != null) {
            mTimer.purge();
            mTimer.cancel();
            if (!isPreparing && isRecording && (secondElapsed > 0 || minuteElapsed > 0)) {
                mRecorder.stop();
                mRecorder.release();
                Toast.makeText(getActivity(),"Record Successful. Available for replay and save",Toast.LENGTH_SHORT).show();
            }
            mRecorder = null;
            mTimer = null;
        }
        isRecording = false;

        // Change images alpha back to normal
        timer.setImageAlpha(255);
        replay.setImageAlpha(255);
        shuffle.setImageAlpha(255);
        questionList.setImageAlpha(255);
        save.setImageAlpha(255);
    }

    private int getIndexSpinner (Spinner spinner, String s) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    private static int getIndexArray (ArrayList<Question> array, String s) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).toString().equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    private void showToast(String text) {
        if (warning == null) {
            warning = Toast.makeText(getActivity(),text,Toast.LENGTH_SHORT);
        }
        warning.setText(text);
        warning.setDuration(Toast.LENGTH_SHORT);
        warning.show();
    }

    public static void previousQuestion(String s) {
        isNext = false;
        Log.e("questionCounter", "" + questionCounter);
        Log.e("ss", "" + getIndexArray(QuestionCategory.getAllQuestion(), s));

        if (getIndexArray(QuestionCategory.getAllQuestion(),s) == questionCounter + 1) {
            question.startAnimation(fadeOut);
        }
    }
}