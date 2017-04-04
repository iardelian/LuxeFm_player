package com.ardel.luxfm;

class ListItem {

    private String trackLink;
    private String artist;
    private String track;
    private boolean checked;

    ListItem(String title, String trackLink) {
        this.trackLink = trackLink;

        artist = title.substring(0, title.indexOf(" - "));
        track = title.substring(title.indexOf(" - ")+3,title.length());
    }

    String getArtist() {
        return artist;
    }

    public String getTrack() {
        return track;
    }

    String getTrackLink() {
        return trackLink;
    }

    void setChecked(boolean b) {
        checked = b;
    }

    boolean getChecked() {
        return checked;
    }
}