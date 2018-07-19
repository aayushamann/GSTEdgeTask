package com.gstedge.gstedgetask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 1500;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.splash_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    int pos = email.indexOf('@');
                    String username = email.substring(0, pos);
                    Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SplashScreen.this, SignUpActivity.class));
                }
                progressBar.setVisibility(View.GONE);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
