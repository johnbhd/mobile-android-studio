package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.MainActivity;
import com.example.voltesvsuperrobotstrike.R;
import com.example.voltesvsuperrobotstrike.audio.GameMusicManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 3000L;
    private static final long SPLASH_PROGRESS_FRAME_DELAY_MS = 16L;

    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private ProgressBar splashProgressBar;
    private long splashStartedAtMillis;
    private boolean hasOpenedMainMenu;
    private final Runnable splashProgressRunnable = new Runnable() {
        @Override
        public void run() {
            updateSplashProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        GameMusicManager.getInstance(this).playMenuMusic();

        applySystemBarInsets();
        startSplashProgress();
    }

    private void startSplashProgress() {
        splashProgressBar = findViewById(R.id.splash_loading_indicator);
        splashProgressBar.setMax(100);
        splashProgressBar.setProgress(1);
        splashStartedAtMillis = SystemClock.uptimeMillis();
        splashHandler.removeCallbacks(splashProgressRunnable);
        splashHandler.post(splashProgressRunnable);
    }

    private void updateSplashProgress() {
        if (isFinishing() || isChangingConfigurations()) {
            return;
        }

        long elapsedMillis = SystemClock.uptimeMillis() - splashStartedAtMillis;
        float completion = Math.min(1f, elapsedMillis / (float) SPLASH_DURATION_MS);
        int progress = 1 + Math.round(completion * 99f);
        splashProgressBar.setProgress(progress);

        if (completion < 1f) {
            splashHandler.postDelayed(
                    splashProgressRunnable,
                    SPLASH_PROGRESS_FRAME_DELAY_MS
            );
            return;
        }

        openMainMenu();
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
        intent.putExtra(MainActivity.EXTRA_SPLASH_COMPLETE, true);

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        splashHandler.removeCallbacks(splashProgressRunnable);
        super.onDestroy();
    }
}
