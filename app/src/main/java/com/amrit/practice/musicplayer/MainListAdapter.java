package com.amrit.practice.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainListAdapter extends BaseAdapter {

    private static final String LOG = MainListAdapter.class.getSimpleName();

    private final ArrayList<String> musicName;
    private final Context context;
    private final ArrayList<String> musicUri;

    public MainListAdapter(Context context, ArrayList<String> musicName, ArrayList<String> musicUri) {
        this.musicName = musicName;
        this.context = context;
        this.musicUri = musicUri;
    }

    @Override
    public int getCount() {
        return musicName.size();
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

        TextView songName = convertView.findViewById(R.id.song_name);
        songName.setSelected(true);
        ImageView imageView = convertView.findViewById(R.id.image_view);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        String name = musicName.get(position);
        songName.setText(name);

        Bitmap bitmap = null;

        Uri uri = Uri.parse(musicUri.get(position));
        try {
            bitmap = context.getContentResolver().loadThumbnail(
                    uri,
                    new Size(100, 100),
                    null);
        } catch (IOException e) {
            Log.e(LOG, "Bitmap not available");
        }

        if(bitmap != null) imageView.setImageBitmap(bitmap);
        else imageView.setImageDrawable(context.getDrawable(R.drawable.music));

        return convertView;
    }
}
