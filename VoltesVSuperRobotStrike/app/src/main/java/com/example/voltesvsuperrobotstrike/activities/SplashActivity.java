package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.MainActivity;
import com.example.voltesvsuperrobotstrike.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1500L;

    private ValueAnimator splashProgressAnimator;
    private boolean hasOpenedMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        applySystemBarInsets();
        startSplashProgress();
    }

    private void startSplashProgress() {
        ProgressBar progressBar = findViewById(R.id.splash_loading_indicator);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        splashProgressAnimator = ValueAnimator.ofInt(0, 100);
        splashProgressAnimator.setDuration(SPLASH_DURATION_MS);
        splashProgressAnimator.addUpdateListener((animator) -> {
            progressBar.setProgress((Integer) animator.getAnimatedValue());
        });
        splashProgressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation != splashProgressAnimator
                        || isFinishing()
                        || isChangingConfigurations()) {
                    return;
                }
                openMainMenu();
            }
        });
        splashProgressAnimator.start();
    }

    private void applySystemBarInsets() {
        View safeAreaView = findViewById(R.id.splash_safe_area);

        ViewCompat.setOnApplyWindowInsetsListener(safeAreaView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int basePadding = getResources().getDimensionPixelSize(R.dimen.screen_padding);

            view.setPadding(
                    basePadding + systemBars.left,
                    basePadding + systemBars.top,
                    basePadding + systemBars.right,
                    basePadding + systemBars.bottom
            );

            return insets;
        });
    }

    private void openMainMenu() {
        if (hasOpenedMainMenu || isFinishing()) {
            return;
        }

        hasOpenedMainMenu = true;

        Intent intent = new Intent(
                SplashActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        ValueAnimator animator = splashProgressAnimator;
        splashProgressAnimator = null;
        if (animator != null) {
            animator.cancel();
        }
        super.onDestroy();
    }
}
