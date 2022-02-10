package com.amrit.practice.musicplayer;

public class AudioUtil {

    private String title, artist, genre, album, uri;
    private int duration;

    public AudioUtil(String title, String artist, String genre, String album, int duration, String uri) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static boolean isAudio(String name){

        String[] ext = {".mp3", ".wav", ".amr", ".flac", ".aac", ".ogg", ".opus", ".m4a"};
        for (String s : ext) if (name.endsWith(s)) return true;
        return false;
    }

}
