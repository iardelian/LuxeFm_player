package com.ardel.luxfm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentMain extends Fragment implements Constants, MainHandler.OnHandleListener, TrackAdapter.onAdapterListener {

    ArrayList<ListItem> data = new ArrayList<>();
    private static MainHandler h;
    private TrackAdapter trackAdapter;
    AsyncTask at = null;
    TextView loadText;
    MediaPlayer mediaPlayer;
    int playingSong = -1;
    ProgressBar progressBar;
    Intent noInternetActivity;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = getActivity().getApplicationContext();

        h = new MainHandler();
        h.setOnHandleListener(this);
        trackAdapter = new TrackAdapter(context, data);
        ListView list = (ListView) v.findViewById(R.id.listView);
        list.setAdapter(trackAdapter);
        trackAdapter.setOnAdapterListener(this);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        noInternetActivity = new Intent (getContext(),NoInternetActivity.class);
        noInternetActivity.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        loadText = (TextView) v.findViewById(R.id.loading);
        data.clear();
        getFileList();

        return v;
    }


    public boolean isNetworkAvailable() {
        try {
            return (Runtime.getRuntime().exec(PING_GOOGLE).waitFor() == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void getFileList() {


        if (at != null) {
            at.cancel(false);
        }

        if (isNetworkAvailable()) {

            at = new AsyncTask<String, Integer, Void>() {

                @Override
                protected void onPreExecute() {
                    showProgressIcon(true);
                }

                @Override
                protected Void doInBackground(String... params) {

                    String trackTitle;
                    String trackLink;

                    try {

                        Document document = Jsoup
                                .connect(URL_STRING)
                                .userAgent(USERAGENT)
                                .timeout(25000)
                                .get();

                        Elements elements = document.select(".playlist-item");
                        for (Element element : elements) {

                            trackTitle = element.select("[id~=^song-name-right-[0-9]+$]").attr("value") + " - " +
                                    element.select("[id~=^song-name-left-[0-9]+$]").attr("value");

                            trackLink = PREF_SITE + element.select("[id~=^song-path-[0-9]+$]").attr("value").replace(" ", "%20");

                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt(EXTRA_ID, DATA);
                            bundle.putString(EXTRA_TITLE, trackTitle);
                            bundle.putString(EXTRA_LINK, trackLink);
                            msg.setData(bundle);
                            h.sendMessage(msg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void param) {
                    showProgressIcon(false);
                }
            }.execute();

        } else {
            releaseMedia();
            super.onStop();
            startActivity(noInternetActivity);
        }

    }


    @Override
    public void onHandleString(String title, String link) {
        data.add(new ListItem(title, link));
        trackAdapter.notifyDataSetChanged();
    }


    @Override
    public void adapterEvent(int event, int id) {
        final String link = data.get(id).getTrackLink();
        final String name = data.get(id).getArtist() + " - " + data.get(id).getTrack();
        switch (event) {
            case PLAY:

                boolean checked = data.get(id).getChecked();
                if (checked) {
                    playingSong = id;
                    data.get(id).setChecked(false);
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        if (i != id) data.get(i).setChecked(false);
                        else data.get(i).setChecked(true);
                    }
                }
                trackAdapter.notifyDataSetChanged();
                play(id, checked);
                break;


            case LOAD:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

                alertDialog.setTitle(getString(R.string.downloading));

                alertDialog.setMessage(getString(R.string.download) +" "+ name + "?");

                alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                            releaseMedia();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), getString(R.string.unable_download_song), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                alertDialog.show();
                break;
        }

    }

    void showProgressIcon(boolean show){
        if(show) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            loadText.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            loadText.setVisibility(View.INVISIBLE);
        }
    }

    public void releaseMedia(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void play(final int id, boolean checked) { // checked = false if track is playing, and true if not

        if(isNetworkAvailable()) {

            final String link = data.get(id).getTrackLink();

            if (playingSong == id) {

                if (checked && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    return;

                } else {
                    mediaPlayer.start();
                    return;
                }

            } else {
                releaseMedia();
            }

            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(link);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            showProgressIcon(true);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (loadText.getVisibility() == View.VISIBLE && progressBar.getVisibility() == View.VISIBLE) {
                        showProgressIcon(false);
                    }
                    mediaPlayer.start();

                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    data.get(id).setChecked(false);
                    try {
                        if (id+1 < data.size()) {
                            data.get(id+1).setChecked(true);
                            //FIXME CHECK IF I NEED THIS CHECKING
                            //releaseMedia();
                            play(id+1, true);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    trackAdapter.notifyDataSetChanged();
                }

            });
            try {
                mediaPlayer.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.unable_play_song), Toast.LENGTH_SHORT).show();
            }
        }else{
            releaseMedia();
            super.onStop();
            startActivity(noInternetActivity);
        }

    }


}
