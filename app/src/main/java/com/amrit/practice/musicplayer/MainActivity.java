package com.amrit.practice.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<AudioUtil>> {

    //    request code for permission
    private final int REQUEST_PERMISSION_CODE = 5;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private ListView listView;
    private final int LoaderManger_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.Theme_MusicPlayer);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list_view);
        progressBar = findViewById(R.id.progress);
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
//        calling permissions method
        checkPermissionStatus();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showMusic(){
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        loaderManager.initLoader(LoaderManger_ID, null, this);
    }



    @NonNull
    @NotNull
    @Override
    public Loader<ArrayList<AudioUtil>> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        File file = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(
                getExternalFilesDir(null) // /storage/emulated/0/Android/data/com.amrit.practice.musicplayer/file
                .getParentFile()) // /storage/emulated/0/Android/data/com.amrit.practice.filesByGoogleReplica
                .getParentFile()) // /storage/emulated/0/Android/data
                .getParentFile()) // /storage/emulated/0/Android
                .getParentFile(); // /storage/emulated/0

        return new AudioLoader(getApplicationContext(), file);
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<ArrayList<AudioUtil>> loader, ArrayList<AudioUtil> data) {
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        Log.e(LOG_TAG, "Done till now");
        MainListAdapter adapter = new MainListAdapter(this, data);
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<ArrayList<AudioUtil>> loader) {

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

//    private void registerReceiver() {
//        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @SuppressLint("UseCompatLoadingForDrawables")
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                currentSong.setText(musicNames.get(MusicController.getPosition()));
//
//                String curUri = MusicController.songUri();
//                Bitmap bitmap = null;
//
//                Uri uri = Uri.parse(curUri);
//                try {
//                    bitmap = getContentResolver().loadThumbnail(
//                            uri,
//                            new Size(100, 100),
//                            null);
//                } catch (IOException e) {
//                    Log.e(LOG_TAG, "Bitmap not available");
//                }
//
//                if(bitmap != null) imageView.setImageBitmap(bitmap);
//                else imageView.setImageDrawable(getDrawable(R.drawable.music));
//
//                isPlaying = MusicController.isPlaying();
//                if(isPlaying) homePlayPause.setBackground(getDrawable(R.drawable.pause));
//                else homePlayPause.setBackground(getDrawable(R.drawable.play_arrow));
//            }
//        };
//        registerReceiver(broadcastReceiver, new IntentFilter("update"));
//        registerReceiver(broadcastReceiver, new IntentFilter("change"));
//    }

}
