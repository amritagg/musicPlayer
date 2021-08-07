package com.amrit.practice.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class MusicActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private ArrayList<String> audioUris, audioLocations, audioNames;
    private ArrayList<Integer> audioSize;
    private long[] audio_dates;
    private int position;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        playerView = findViewById(R.id.player_view);

        Intent intent = getIntent();
        audioUris = intent.getStringArrayListExtra("uris");
        position = intent.getIntExtra("position", 0);
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(audioUris.get(position));
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void hideSystemUI() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

}

//public class MusicActivity extends AppCompatActivity {
//
//    public static final String LOG_TAG = MusicActivity.class.getSimpleName();
//
//    Button play_pause, previous, next;
//
//    ImageView imageView;
//
//    TextView song_name, current_time, final_time;
//    SeekBar mSeekBar;
//
//    private final Handler handler = new Handler();
//    private final Handler seekHandler = new Handler();
//
//    private BroadcastReceiver broadcastReceiver;
//
//    private boolean isPlaying;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setTheme(R.style.Theme_MusicPlayer);
//        setContentView(R.layout.activity_music);
//
//        init();
//        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
//        showMusic();
//
//        play_pause.setOnClickListener(view -> play_pause());
//
//        next.setOnClickListener(view -> {
//            try {
//                next();
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Can't Play next Song");
//            }
//        });
//
//        previous.setOnClickListener(view -> {
//            try {
//                previous();
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Can't Play previous Song");
//            }
//        });
//        registerReceiver();
//
//    }
//
//    private void init(){
//        play_pause = findViewById(R.id.play_pause);
//        previous = findViewById(R.id.previous);
//        next = findViewById(R.id.next);
//
//        song_name = findViewById(R.id.name);
//        current_time = findViewById(R.id.current_position);
//        final_time = findViewById(R.id.final_position);
//
//        mSeekBar = findViewById(R.id.seek_bar);
//
//        imageView = findViewById(R.id.song_album);
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    private void showMusic(){
//        int finalTime = MusicController.getFinalDuration();
//        mSeekBar.setMax(finalTime/1000);
//        isPlaying = MusicController.isPlaying();
//
//        if(isPlaying) play_pause.setBackground(getDrawable(R.drawable.pause));
//        else play_pause.setBackground(getDrawable(R.drawable.play_arrow));
//
//        final_time.setText(time(finalTime));
//        seekBarRunnable.run();
//        runnable.run();
//        String name = MusicController.songName();
//        song_name.setText(name);
//        showImage();
//    }
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    private void showImage(){
//
//        Bitmap bitmap = null;
//
//        String uri_string = MusicController.songUri();
//        Uri uri = Uri.parse(uri_string);
//        try {
//            bitmap = getContentResolver().loadThumbnail(
//                    uri,
//                    new Size(350, 350),
//                    null);
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Bitmap not available");
//        }
//
//        if(bitmap != null) imageView.setImageBitmap(bitmap);
//        else imageView.setImageDrawable(getDrawable(R.drawable.music));
//    }
//
//
//    @SuppressLint("UseCompatLoadingForDrawables")
//    public void play_pause(){
//        isPlaying = MusicController.isPlaying();
//
//        if(isPlaying){
//            MusicController.stop();
//            play_pause.setBackground(getDrawable(R.drawable.play_arrow));
//        }else{
//            MusicController.resumeMusic();
//            play_pause.setBackground(getDrawable(R.drawable.pause));
//        }
//    }
//
//    private void stopMusic(){
//        handler.removeCallbacks(runnable);
//        seekHandler.removeCallbacks(seekBarRunnable);
//        MusicController.stop();
//    }
//
//    private String time(int time_int){
//        time_int = time_int/1000;
//        String output = "";
//        int sec = time_int % 60;
//        int min = time_int / 60;
//
//        output += min + ":";
//
//        if(sec >= 10) output += sec + "";
//        else output += "0" + sec;
//        return output;
//    }
//
//    public Runnable runnable = new Runnable() {
//        @SuppressLint("SetTextI18n")
//        @Override
//        public void run() {
//            int currentTime = MusicController.getCurrentTime();
//            current_time.setText(time(currentTime));
//            handler.postDelayed(runnable, 1000);
//        }
//    };
//
//    public Runnable seekBarRunnable = new Runnable() {
//        @Override
//        public void run() {
//            int currentTime = MusicController.getCurrentTime();
//            mSeekBar.setProgress(currentTime/1000);
//            seekHandler.postDelayed(seekBarRunnable, 1000);
//        }
//    };
//
//    private final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if(fromUser){
//                int currentTime = MusicController.getCurrentTime();
//                seekBar.setProgress(progress);
//                MusicController.seek(progress*1000);
//                current_time.setText(time(currentTime));
//            }
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            isPlaying = MusicController.isPlaying();
//            stopMusic();
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            if(isPlaying) resume();
//        }
//    };
//
//    private void resume(){
//        runnable.run();
//        seekBarRunnable.run();
//        MusicController.resumeMusic();
//    }
//
//    private void next() throws IOException {
//        MusicController.nextSong();
//        showMusic();
//    }
//
//    private void previous() throws IOException {
//        MusicController.previousSong();
//        showMusic();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        } return super.onOptionsItemSelected(item);
//    }
//
//    private void registerReceiver() {
//        broadcastReceiver = new BroadcastReceiver() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                showMusic();
//            }
//        };
//        registerReceiver(broadcastReceiver, new IntentFilter("update"));
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(broadcastReceiver != null) {
//            unregisterReceiver(broadcastReceiver);
//        }
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.stay_in_back, R.anim.slide_down);
//    }
//
//}
