package com.ardelian.luxefm;

import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Constants {

    Boolean doubleBackToExitPressedOnce = false;
    FragmentMain fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("");
        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/titleFont.ttf"));
        title.setTextSize(85);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragmentMain = new FragmentMain();
        ft.add(R.id.container, fragmentMain);
        ft.commit();

    }


    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.exit_dialog),
                    Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.container);
            fragmentMain.releaseMedia();
            fragmentMain.stopAudioFocus();
            super.onBackPressed();
        }
    }


}
