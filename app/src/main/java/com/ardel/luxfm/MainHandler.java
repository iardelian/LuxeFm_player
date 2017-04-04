package com.ardel.luxfm;

import android.os.Handler;
import android.os.Message;

class MainHandler extends Handler implements Constants {

    private OnHandleListener listener;

    MainHandler() {}

    interface OnHandleListener {
        void onHandleString(String title, String link);
    }

    public void setOnHandleListener(OnHandleListener l) {
        this.listener = l;
    }
    @Override
    public void handleMessage(Message msg) {
        int id = msg.getData().getInt(EXTRA_ID);
        switch (id) {
            case DATA:
                String title = msg.getData().getString(EXTRA_TITLE);
                String link = msg.getData().getString(EXTRA_LINK);
                listener.onHandleString(title, link);
                break;
        }
    }
}
