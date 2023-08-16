package com.example.fimsdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fimsdelivery.sharedpreferences.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class SplashActivity extends AppCompatActivity {
    //    VARIABLES
    Animation topAnim, bottomAnim;
    TextView title, slogan;
    MaterialCardView cardLogo;
    ProgressBar progressBar;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

//        ANIMATIONS
        topAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_animation);

//        HOOKS
        title = findViewById(R.id.title);
        slogan = findViewById(R.id.slogan);
        cardLogo = findViewById(R.id.cardLogo);
        progressBar = findViewById(R.id.progress);

        cardLogo.setAnimation(topAnim);
        title.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        SessionManager sessionManager = new SessionManager(SplashActivity.this);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int jumpTime = 0;
                try {
                    while (jumpTime < progressBar.getMax()) {
                        Thread.sleep(100);
                        jumpTime += 5;
                        progressBar.setProgress(jumpTime);
                    }
                    if (sessionManager.checkLogin()) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    finish();
                }
                catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}