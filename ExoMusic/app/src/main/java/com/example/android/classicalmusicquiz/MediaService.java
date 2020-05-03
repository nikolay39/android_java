package com.example.android.classicalmusicquiz;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.media.session.MediaButtonReceiver;

import static com.example.android.classicalmusicquiz.QuizActivity.getMediassesion;

public class MediaService extends Service {

    // Binder given to clients
    private final IBinder binder = new MediaBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return new PlayerServiceBinder();
        return binder;
    }

    public class MediaBinder extends Binder {
        MediaService getService() {
            return MediaService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(getMediassesion(), intent);
        return super.onStartCommand(intent, flags, startId);
    }
}

