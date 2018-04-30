package com.example.RestoApp.screens.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.example.RestoApp.R;
import com.example.RestoApp.data.local.LocalStorageManager;
import com.example.RestoApp.models.User;
import com.example.RestoApp.screens.authentication.AuthenticationActivity;
import com.example.RestoApp.screens.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    LocalStorageManager localStorageManager;


    private ValueAnimator valueAnimator;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImageView = findViewById(R.id.logo);

        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                logoImageView.setAlpha(value);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final MediaPlayer mp = MediaPlayer.create(SplashActivity.this, R.raw.mac_startup);
                        mp.start();


                gotoNextScreen();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.removeAllListeners();
    }

    private void gotoNextScreen() {
        Intent intent;

        localStorageManager = LocalStorageManager.getInstance(getApplicationContext());
        User user = localStorageManager.getUser();
        if (user == null) {
            intent = new Intent(this, AuthenticationActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
