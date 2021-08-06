package com.amrit.practice.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;

public class Broadcaster extends BroadcastReceiver {

    public static final String LOG_TAB = Broadcaster.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.hasExtra("action")){
            String action = intent.getStringExtra("action");

            switch (action) {
                case "previous":
                    try {
                        MusicController.previousSong();
                    } catch (IOException e) {
                        Log.e(LOG_TAB, "Can't play previous");
                    }
                    break;
                case "play_pause":
                    boolean isPlaying = MusicController.isPlaying();
                    if (isPlaying) MusicController.stop();
                    else MusicController.resumeMusic();
                    break;
                case "next":
                    try {
                        MusicController.nextSong();
                    } catch (IOException e) {
                        Log.e(LOG_TAB, "Can't play next");
                    }
                    break;
            }

            Intent update = new Intent("update");
            context.sendBroadcast(update);

        }else if(intent.hasExtra("change")){
            String action2 = intent.getStringExtra("change");

            if(action2.equals("make_changes")) {
                Intent update = new Intent("update");
                context.sendBroadcast(update);
            }
        }
        
    }

}
