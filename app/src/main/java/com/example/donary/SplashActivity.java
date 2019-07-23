package com.example.donary;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    //Time to display the Slide
    private static int SPLASH_TIME_OUT = 2000;

    //set Text text
    TextView titleMain;
    Typeface tf1;

    //set Animation to logo and Text
    Animation smalltobig, nothingtocome;
    ImageView logomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        titleMain = (TextView) findViewById(R.id.mainTitle);

        //set animation to logo and text
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        logomain = (ImageView) findViewById(R.id.mainLogo);
        logomain.startAnimation(smalltobig);

        nothingtocome = AnimationUtils.loadAnimation(this, R.anim.nothingtocome);
        titleMain.startAnimation(nothingtocome);

        //set splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signInIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(signInIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
        setStatusBarTransparent();
    }

    //prefer this. add items in values>styles file
    private void setStatusBarTransparent(){
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
