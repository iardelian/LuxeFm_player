package com.ardelian.luxefm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements Constants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentMain = new Intent(this, MainActivity.class);
        Intent intentNoInternet = new Intent(this, NoInternetActivity.class);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isNetworkAvailable()) {
            startActivity(intentMain);
        }else{
            startActivity(intentNoInternet);
        }
        finish();
    }

    public boolean isNetworkAvailable() {
        try {
            return (Runtime.getRuntime().exec(PING_GOOGLE).waitFor() == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}