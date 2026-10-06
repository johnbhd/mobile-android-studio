package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.MainActivity;
import com.example.voltesvsuperrobotstrike.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 1500L;

    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private boolean hasOpenedMainMenu;

    private final Runnable openMainMenuRunnable = new Runnable() {
        @Override
        public void run() {
            openMainMenu();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        applySystemBarInsets();
        splashHandler.postDelayed(openMainMenuRunnable, SPLASH_DURATION_MS);
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
        splashHandler.removeCallbacks(openMainMenuRunnable);
        super.onDestroy();
    }
}
