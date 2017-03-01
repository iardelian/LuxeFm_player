package com.ardel.luxfm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;


public class NoInternetActivity extends AppCompatActivity implements Constants {

    ImageButton refreshInternet;
    Boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_error);
        refreshInternet = (ImageButton) findViewById(R.id.refresh);
        refreshInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()){
                    Intent intent = new Intent(NoInternetActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(NoInternetActivity.this,getString(R.string.internet_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean isNetworkAvailable() {
        try {
            return (Runtime.getRuntime().exec(PING_GOOGLE).waitFor() == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,getResources().getString(R.string.exit_dialog),
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

}
