package com.amrit.practice.musicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;

public class AudioLoader extends AsyncTaskLoader<ArrayList<AudioUtil>> {

    private final File file;
    private final static String LOG_TAG = AudioLoader.class.getSimpleName();

    public AudioLoader(@NonNull @NotNull Context context, File file) {
        super(context);
        this.file = file;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ArrayList<AudioUtil> loadInBackground() {
        return getSongs(file);
    }

    private ArrayList<AudioUtil> getSongs(File root) {
        ArrayList<AudioUtil> songsList = new ArrayList<>();
        File[] files = root.listFiles();
        assert files != null;
        for (File singleFile : files) {
            if(singleFile.getAbsolutePath().startsWith(file + "/Android")) continue;
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                songsList.addAll(getSongs(singleFile));
            } else {
                if(AudioUtil.isAudio(singleFile.getName()) && !singleFile.getName().startsWith("Call@")){
                    AudioUtil audioUtil = getSongMetaData(singleFile);
                    if(audioUtil != null) songsList.add(getSongMetaData(singleFile));
                }
            }
        }
        return songsList;
    }

    public AudioUtil getSongMetaData(File file) {
        Uri uri = Uri.fromFile(file);
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(file.toURI().getPath());

        String title = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if (title == null) return null;

        String album = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artist = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String genre = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);

        if (album == null) album = "Unknown Album";
        if (artist == null) artist = "Unknown Artist";
        if (genre == null) genre = "Unknown Genre";

        int duration = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;

        AudioUtil song = new AudioUtil(title, artist, genre, album, duration, uri.toString());
        metadataRetriever.release();
        return song;
    }
}
