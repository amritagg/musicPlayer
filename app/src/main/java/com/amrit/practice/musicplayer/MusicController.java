package com.amrit.practice.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;

public class MusicController {

    static MediaPlayer mediaPlayer;
    static AudioManager audioManager;
    static AudioFocusRequest mFocusRequest;
    static AudioAttributes mPlaybackAttributes;

    @SuppressLint("StaticFieldLeak")
    static Context mContext;
    static ArrayList<String> names, uris;
    static int position = 0;
    static Intent currentChanges;

    public MusicController(Context context, ArrayList<String> musicNames, ArrayList<String> musicUri){
        mContext = context;
        names = musicNames;
        uris = musicUri;
    }
    
    public static void startMusic() throws IOException {
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        mPlaybackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(mPlaybackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                .build();

        int result = audioManager.requestAudioFocus(mFocusRequest);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(mPlaybackAttributes);

            mediaPlayer.setDataSource(mContext, Uri.parse(uris.get(position)));
            mediaPlayer.prepare();
            mediaPlayer.start();

            NotificationUtil.showNotification(mContext, uris.get(position));

            currentChanges = new Intent(mContext, Broadcaster.class);
            currentChanges.putExtra("change","make_changes");
            mContext.sendBroadcast(currentChanges);

            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                try {
                    nextSong();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void startMusic(int position) throws IOException {
        MusicController.position = position;
        startMusic();
    }

    public static void stop(){
        mediaPlayer.pause();
        mContext.sendBroadcast(currentChanges);
        NotificationUtil.cancelNotification(mContext);
    }

    public static boolean musicStarted(){
        return mediaPlayer != null;
    }

    public static void nextSong() throws IOException {
        if(position == uris.size() - 1) position = 0;
        else position++;
        changeAudio();
    }

    public static int getPosition(){
        return position;
    }

    public static void previousSong() throws IOException {
        if(position == 0) position = uris.size() - 1;
        else position--;
        changeAudio();
    }

    public static void resumeMusic(){

        int result = audioManager.requestAudioFocus(mFocusRequest);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
            mediaPlayer.start();
            mContext.sendBroadcast(currentChanges);
            NotificationUtil.showNotification(mContext, uris.get(position));
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public static String songName(){
        return names.get(position);
    }

    public static String songUri(){
        return uris.get(position);
    }

    private static void changeAudio() throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            audioManager.abandonAudioFocusRequest(mFocusRequest);
        }
        startMusic();
    }

    public static int getFinalDuration(){
        return mediaPlayer.getDuration();
    }

    public static int getCurrentTime(){
        return mediaPlayer.getCurrentPosition();
    }

    public static void seek(int position){
        mediaPlayer.seekTo(position);
    }

    static AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = focusChange -> {
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK || focusChange == AUDIOFOCUS_LOSS){
            stop();
        }else if(focusChange == AUDIOFOCUS_GAIN){
            resumeMusic();
        }
    };
    
}
