package com.amrit.practice.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class MusicService extends Service {

    @Override
    public void onCreate() { super.onCreate(); }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent musicPlayer = new Intent(this, MusicActivity.class);

        return super.onStartCommand(intent, flags, startId);
    }
}
