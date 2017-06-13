package com.ardelian.luxefm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;


class ConnectivityStatus implements Constants {

    private Context mContext;

    ConnectivityStatus(Context mContext) {
        this.mContext = mContext;
    }

    boolean connectionAccess() {
        return isNetworkAvailable() && hasInternetAccess();
    }


    private boolean hasInternetAccess() {
        try {
            return (Runtime.getRuntime().exec(PING_GOOGLE).waitFor() == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}