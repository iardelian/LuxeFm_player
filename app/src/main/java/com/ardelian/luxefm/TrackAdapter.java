package com.ardelian.luxefm;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

class TrackAdapter extends BaseAdapter implements Constants {

    private final ArrayList<ListItem> data;
    private Context context;
    private onAdapterListener listener;

    TrackAdapter(Context context, ArrayList<ListItem> data) {
        this.data = data;
        this.context = context;
    }

    interface onAdapterListener {
        void adapterEvent(int event, int id);
    }

    void setOnAdapterListener(onAdapterListener l) {
        this.listener = l;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (v == null) v = inflater.inflate(R.layout.list_item, parent, false);
        TextView artistInfo = (TextView) v.findViewById(R.id.artist);
        TextView trackInfo = (TextView) v.findViewById(R.id.track);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/luxFont.ttf");
        artistInfo.setText(data.get(position).getArtist());
        SpannableString content = new SpannableString(data.get(position).getTrack());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        trackInfo.setText(content);
        artistInfo.setTypeface(font);
        trackInfo.setTypeface(font);
        RadioButton radioPlay = (RadioButton) v.findViewById(R.id.radioPlay);
        radioPlay.setChecked(data.get(position).getChecked());
        ImageButton buttonLoad = (ImageButton) v.findViewById(R.id.imageButtonLoad);

        radioPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    listener.adapterEvent(PLAY, position);
            }
        });

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.adapterEvent(LOAD, position);
            }
        });
        return v;
    }
}
