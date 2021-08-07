package com.amrit.practice.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<AudioUtil> audioUtils;

    public MainListAdapter(Context context, ArrayList<AudioUtil> audioUtils) {
        this.context = context;
        this.audioUtils = audioUtils;
    }

    @Override
    public int getCount() {
        return audioUtils.size();
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

        String name = audioUtils.get(position).getTitle();
        songName.setText(name);

//        Bitmap bitmap = null;
//        Uri uri = Uri.parse(audioUtils.get(position).getUri());
//        try {
//            bitmap = context.getContentResolver().loadThumbnail(
//                    uri,
//                    new Size(100, 100),
//                    null);
//        } catch (IOException e) {
//            Log.e(LOG, "Bitmap not available");
//        }
//        if(bitmap != null) imageView.setImageBitmap(bitmap);
//        else
        imageView.setImageDrawable(context.getDrawable(R.drawable.music));

        return convertView;
    }
}
