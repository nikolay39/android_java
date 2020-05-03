/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.classicalmusicquiz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;


import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
//https://exoplayer.dev/ui-components.html
public class QuizActivity extends AppCompatActivity implements View.OnClickListener, Player.EventListener{

    private final String TAG = QuizActivity.class.getCanonicalName();


    private static final int CORRECT_ANSWER_DELAY_MILLIS = 1000;
    private static final String REMAINING_SONGS_KEY = "remaining_songs";
    private int[] mButtonIDs = {R.id.buttonA, R.id.buttonB, R.id.buttonC, R.id.buttonD};
    private ArrayList<Integer> mRemainingSampleIDs;
    private ArrayList<Integer> mQuestionSampleIDs;

    private int mAnswerSampleID;
    private int mCurrentScore;
    private int mHighScore;
    private Button[] mButtons;

    private SimpleExoPlayer mPlayer;
    private PlayerView mPlayerView;

    private PlaybackStateCompat.Builder mStateBulder;
    private static MediaSessionCompat mediaSession;
    private NotificationManager mNotificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // TODO (2): Replace the ImageView with the SimpleExoPlayerView, and remove the method calls on the composerView.
        mPlayerView = (PlayerView) findViewById(R.id.playeView);

        boolean isNewGame = !getIntent().hasExtra(REMAINING_SONGS_KEY);
        Log.i("new game ", "variable "+isNewGame);

        // If it's a new game, set the current score to 0 and load all samples.
        if (isNewGame) {
            QuizUtils.setCurrentScore(this, 0);
            mRemainingSampleIDs = Sample.getAllSampleIDs(this);
            // Otherwise, get the remaining songs from the Intent.
        } else {
            mRemainingSampleIDs = getIntent().getIntegerArrayListExtra(REMAINING_SONGS_KEY);
        }

        // Get current and high scores.
        mCurrentScore = QuizUtils.getCurrentScore(this);
        mHighScore = QuizUtils.getHighScore(this);

        // Generate a question and get the correct answer.
        mQuestionSampleIDs = QuizUtils.generateQuestion(mRemainingSampleIDs);
        mAnswerSampleID = QuizUtils.getCorrectAnswerID(mQuestionSampleIDs);

        // TODO (3): Replace the default artwork in the SimpleExoPlayerView with the question mark drawable.
        // Load the image of the composer for the answer into the ImageView.
        // getDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPlayerView.setDefaultArtwork(getResources().getDrawable(R.drawable.question_mark, this.getTheme()));
        } else {
            mPlayerView.setDefaultArtwork(getResources().getDrawable(R.drawable.question_mark));
        }

        // If there is only one answer left, end the game.
        if (mQuestionSampleIDs.size() < 2) {
            QuizUtils.endGame(this);
            finish();
        }

        // Initialize the buttons with the composers names.
        mButtons = initializeButtons(mQuestionSampleIDs);

        // TODO (4): Create a Sample object using the Sample.getSampleByID() method and passing in mAnswerSampleID;
        Sample answerSample = Sample.getSampleByID(this, mAnswerSampleID);
        // TODO (1): Create a method to initialize the MediaSession. It should create the MediaSessionCompat object,
        //  set the flags for external clients, set the available actions you want to support, and start the session.
        mediaSession = new MediaSessionCompat(this, TAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // no play the no active
        mediaSession.setMediaButtonReceiver(null);

        mStateBulder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mediaSession.setPlaybackState(mStateBulder.build());

        mediaSession.setCallback(new MysessionCallback());
        mediaSession.setActive(true);

        if(answerSample == null) {
            Toast.makeText(this, getString(R.string.sample_list_load_error), Toast.LENGTH_LONG).show();
            return;
        }
        // TODO (5): Create a method called initializePlayer() that takes a Uri as an argument and call it here, passing in the Sample URI.
        initializePlayer(Uri.parse(answerSample.getUri()));


    }
    private void showNotification(PlaybackStateCompat state) {

        int icon;
        String play_pause;
        if(state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);

        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }
        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)
        );
        NotificationCompat.Action restartAction = new NotificationCompat.Action(
                R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Alert Music";
            String description = "notification music";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel("103", name, importance);
            channel.setDescription(description);

            // Add the channel
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }
        Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, "Alert Music")
                .setContentTitle(getString(R.string.guess))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_music_note)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1))
                .build();
        NotificationManagerCompat.from(this).notify(104, notification);


    }


     private void initializePlayer(Uri mediaUrl) {
         if (mPlayer == null) {
             mPlayer = new SimpleExoPlayer.Builder(this).build();
             // Prepare the Media Source

             // Produces DataSource instances through which media data is loaded.
             DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                     Util.getUserAgent(this, "ClassicalMusicQuiz"));
             // This is the MediaSource representing the media to be played.
             MediaSource audioSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaUrl);
             // Prepare the player with the source.
             mPlayer.prepare(audioSource);
             mPlayerView.setPlayer(mPlayer);
             mPlayer.setPlayWhenReady(true);
             mPlayer.addListener(this);
         }
     }


    /**
     * Initializes the button to the correct views, and sets the text to the composers names,
     * and set's the OnClick listener to the buttons.
     *
     * @param answerSampleIDs The IDs of the possible answers to the question.
     * @return The Array of initialized buttons.
     */
    private Button[] initializeButtons(ArrayList<Integer> answerSampleIDs) {
        Button[] buttons = new Button[mButtonIDs.length];
        for (int i = 0; i < answerSampleIDs.size(); i++) {
            Button currentButton = (Button) findViewById(mButtonIDs[i]);
            Sample currentSample = Sample.getSampleByID(this, answerSampleIDs.get(i));
            buttons[i] = currentButton;
            currentButton.setOnClickListener(this);
            if (currentSample != null) {
                currentButton.setText(currentSample.getComposer());
            }
        }
        return buttons;
    }


    /**
     * The OnClick method for all of the answer buttons. The method uses the index of the button
     * in button array to to get the ID of the sample from the array of question IDs. It also
     * toggles the UI to show the correct answer.
     *
     * @param v The button that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onclick show answer");

        // Show the correct answer.
        showCorrectAnswer();



        // Get the button that was pressed.
        Button pressedButton = (Button) v;

        // Get the index of the pressed button
        int userAnswerIndex = -1;
        for (int i = 0; i < mButtons.length; i++) {
            if (pressedButton.getId() == mButtonIDs[i]) {
                userAnswerIndex = i;
            }
        }

        // Get the ID of the sample that the user selected.
        int userAnswerSampleID = mQuestionSampleIDs.get(userAnswerIndex);

        // If the user is correct, increase there score and update high score.
        if (QuizUtils.userCorrect(mAnswerSampleID, userAnswerSampleID)) {
            mCurrentScore++;
            QuizUtils.setCurrentScore(this, mCurrentScore);
            if (mCurrentScore > mHighScore) {
                mHighScore = mCurrentScore;
                QuizUtils.setHighScore(this, mHighScore);
            }
        }

        // Remove the answer sample from the list of all samples, so it doesn't get asked again.
        mRemainingSampleIDs.remove(Integer.valueOf(mAnswerSampleID));

        // Wait some time so the user can see the correct answer, then go to the next question.
        Log.i(TAG, "run handler");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO (9): Stop the playback when you go to the next question.
                mPlayer.stop();
                Intent nextQuestionIntent = new Intent(QuizActivity.this, QuizActivity.class);
                nextQuestionIntent.putExtra(REMAINING_SONGS_KEY, mRemainingSampleIDs);
                finish();
                startActivity(nextQuestionIntent);
            }
        }, CORRECT_ANSWER_DELAY_MILLIS);

    }

    /**
     * Disables the buttons and changes the background colors to show the correct answer.
     */
    private void showCorrectAnswer() {
        for (int i = 0; i < mQuestionSampleIDs.size(); i++) {
            int buttonSampleID = mQuestionSampleIDs.get(i);

            // TODO (10): Change the default artwork in the SimpleExoPlayerView to show the picture of the composer, when the user has answered the question.
            mButtons[i].setEnabled(false);
            mPlayerView.setDefaultArtwork(new BitmapDrawable(getResources(), Sample.getComposerArtBySampleID(this, mAnswerSampleID)));
            if (buttonSampleID == mAnswerSampleID) {
                mButtons[i].getBackground().setColorFilter(ContextCompat.getColor
                                (this, android.R.color.holo_green_light),
                        PorterDuff.Mode.MULTIPLY);
                mButtons[i].setTextColor(Color.WHITE);
            } else {
                mButtons[i].getBackground().setColorFilter(ContextCompat.getColor
                                (this, android.R.color.holo_red_light),
                        PorterDuff.Mode.MULTIPLY);
                mButtons[i].setTextColor(Color.WHITE);

            }
        }
    }

    // TODO (11): Override onDestroy() to stop and release the player when the Activity is destroyed.
    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
        releasePlayer();
        mediaSession.setActive(false);

    }
    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying) {
            // Active playback.
            mStateBulder.setState(PlaybackStateCompat.STATE_PLAYING, mPlayer.getContentPosition(), 1f);
                 Log.i("isPlaying", "true");
        } else {
            mStateBulder.setState(PlaybackStateCompat.STATE_PAUSED, mPlayer.getContentPosition(), 1f);
            Log.i("isPlaying", "false");
        }
                 // Not playing because playback is paused, ended, suppressed, or the player
                 // is buffering, stopped or failed. Check player.getPlaybackState,
                 // player.getPlayWhenReady, player.getPlaybackError and
                 // player.getPlaybackSuppressionReason for details.
          mediaSession.setPlaybackState(mStateBulder.build());
          showNotification(mStateBulder.build());
    }
    private class MysessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mPlayer.setPlayWhenReady(true);
        }
        @Override
        public void onPause() {
            mPlayer.setPlayWhenReady(false);
        }
        @Override
        public void onSkipToPrevious() {
            mPlayer.seekTo(0);
        }
    }
    private void releasePlayer() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
    }
    public static MediaSessionCompat getMediassesion() {
        return mediaSession;
    }
}
