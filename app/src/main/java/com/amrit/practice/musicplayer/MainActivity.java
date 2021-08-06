package com.amrit.practice.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //    request code for permission
    private final int REQUEST_PERMISSION_CODE = 5;

    ArrayList<String> musicNames = new ArrayList<>();
    ArrayList<String> musicUri = new ArrayList<>();

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView currentSong;
    private Button homePlayPause;
    private ImageView imageView;

    boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MusicPlayer);
        setContentView(R.layout.activity_main);

//        calling permissions method
        checkPermissionStatus();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showMusic(){

        registerReceiver();

        currentSong = findViewById(R.id.cur_song_name);
        Button homePrevious = findViewById(R.id.home_previous);
        homePlayPause = findViewById(R.id.home_play_pause);
        Button homeNext = findViewById(R.id.home_next);

        LinearLayout linearLayout = findViewById(R.id.linear);

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE
        };

        try(Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Media.TITLE + " ASC"
        )){
            assert cursor != null;

            int idColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int nameColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            while (cursor.moveToNext()){

                long id = cursor.getLong(idColumnIndex);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                String uri = contentUri.toString();
                String name = cursor.getString(nameColumnIndex);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(Uri.parse(uri));
                    if(inputStream != null){
                        musicUri.add(uri);
                        musicNames.add(name);
                    }
                } catch (FileNotFoundException e) {
                    Log.e(LOG_TAG, "File not found");
                }

            }

            cursor.close();

            MainListAdapter adapter = new MainListAdapter(getApplicationContext(), musicNames, musicUri);
            ListView listView = findViewById(R.id.list_view);
            listView.setAdapter(adapter);

            new MusicController(getApplicationContext(), musicNames, musicUri);

            currentSong.setText(musicNames.get(0));

            linearLayout.setOnClickListener(view -> {
                try {
                    boolean musicStarted = MusicController.musicStarted();
                    if(!musicStarted) MusicController.startMusic();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while starting the music!");
                }

                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.slide_up, R.anim.stay_in_back);
            });

            homePrevious.setOnClickListener(view -> {
                try {
                    boolean musicStarted = MusicController.musicStarted();
                    if(!musicStarted) {
                        try {
                            MusicController.startMusic();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Error while playing the music");
                        }
                    }else {
                        MusicController.previousSong();
                        String curSong = MusicController.songName();
                        currentSong.setText(curSong);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while playing the music");
                }
            });

            homePlayPause.setOnClickListener(view -> {
                boolean musicStarted = MusicController.musicStarted();
                if(!musicStarted) {
                    try {
                        MusicController.startMusic();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error while playing the music");
                    }
                }else{
                    isPlaying = MusicController.isPlaying();
                    if(isPlaying) {
                        MusicController.stop();
                        homePlayPause.setBackground(getDrawable(R.drawable.play_arrow));
                    } else {
                        MusicController.resumeMusic();
                        homePlayPause.setBackground(getDrawable(R.drawable.pause));
                    }
                }

            });

            homeNext.setOnClickListener(view -> {
                try {
                    boolean musicStarted = MusicController.musicStarted();
                    if(!musicStarted) {
                        try {
                            MusicController.startMusic();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Error while playing the music");
                        }
                    }else {
                        MusicController.nextSong();
                        String curSong = MusicController.songName();
                        currentSong.setText(curSong);
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while playing the music");
                }
            });

            listView.setOnItemClickListener((adapterView, view, position, l) -> {
                try {
                    MusicController.startMusic(position);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while starting the music!");
                }
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.stay_in_back);

            });

        } catch (Exception e) {
            Log.e(LOG_TAG, "The error is " + e);
        }

        boolean musicStart = MusicController.musicStarted();
        if(musicStart) {
            homePlayPause.setBackground(getDrawable(R.drawable.pause));
            currentSong.setText(MusicController.songName());
        } else homePlayPause.setBackground(getDrawable(R.drawable.play_arrow));

        imageView = findViewById(R.id.bottom_image);

        String curUri = MusicController.songUri();
        Bitmap bitmap = null;

        Uri uri = Uri.parse(curUri);
        try {
            bitmap = getContentResolver().loadThumbnail(
                    uri,
                    new Size(100, 100),
                    null);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Bitmap not available");
        }

        if(bitmap != null) imageView.setImageBitmap(bitmap);
        else imageView.setImageDrawable(getDrawable(R.drawable.music));

    }

    private void registerReceiver() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onReceive(Context context, Intent intent) {
                currentSong.setText(musicNames.get(MusicController.getPosition()));

                String curUri = MusicController.songUri();
                Bitmap bitmap = null;

                Uri uri = Uri.parse(curUri);
                try {
                    bitmap = getContentResolver().loadThumbnail(
                            uri,
                            new Size(100, 100),
                            null);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Bitmap not available");
                }

                if(bitmap != null) imageView.setImageBitmap(bitmap);
                else imageView.setImageDrawable(getDrawable(R.drawable.music));

                isPlaying = MusicController.isPlaying();
                if(isPlaying) homePlayPause.setBackground(getDrawable(R.drawable.pause));
                else homePlayPause.setBackground(getDrawable(R.drawable.play_arrow));
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("update"));
        registerReceiver(broadcastReceiver, new IntentFilter("change"));
    }

    //    list of permissions required
    String[] PERMISSION_LIST = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //  Method for permission handling
    private void checkPermissionStatus() {

//      check weather the permission is given or not
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showMusic();
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                if not permission is denied than show the message to user showing him the benefits of the permission.
                Toast.makeText(this, "Permission is needed to show the Media..", Toast.LENGTH_SHORT).show();
                finish();
            }
//            request permission
            requestPermissions(PERMISSION_LIST, REQUEST_PERMISSION_CODE);
        }
    }

    //    handling the permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showMusic();
            }else{
                Toast.makeText(this,"Permissions not granted!!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
