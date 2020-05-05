package com.razi.furnitar.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.razi.furnitar.R;
import com.razi.furnitar.Utils.UserPreference;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPreference.getInstance().pref = getApplicationContext().getSharedPreferences("WowTrack", 0);
        setContentView(R.layout.activity_splash_activity);
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.blink);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(700);
        final ImageView splash = (ImageView) findViewById(R.id.splash);
        splash.startAnimation(animation);
        new CountDownTimer(800, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {

                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();

            }
        }.start();
    }
}
