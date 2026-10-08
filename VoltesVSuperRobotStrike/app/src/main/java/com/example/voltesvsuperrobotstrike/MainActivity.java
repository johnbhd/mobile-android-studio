package com.example.voltesvsuperrobotstrike;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.activities.DifficultyActivity;
import com.example.voltesvsuperrobotstrike.activities.HighScoreActivity;
import com.example.voltesvsuperrobotstrike.activities.HowToPlayActivity;
import com.example.voltesvsuperrobotstrike.activities.MachineSelectionActivity;
import com.example.voltesvsuperrobotstrike.activities.SplashActivity;
import com.example.voltesvsuperrobotstrike.audio.GameMusicManager;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_SPLASH_COMPLETE =
            "com.example.voltesvsuperrobotstrike.extra.SPLASH_COMPLETE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().getBooleanExtra(EXTRA_SPLASH_COMPLETE, false)) {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        GameMusicManager.getInstance(this).playMenuMusic();

        applySystemBarInsets();
        setupNavigation();
    }

    private void applySystemBarInsets() {
        View safeAreaView = findViewById(R.id.main_safe_area);

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

    private void setupNavigation() {
        findViewById(R.id.start_game_button).setOnClickListener((view) -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    MachineSelectionActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.how_to_play_button).setOnClickListener((view) -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    HowToPlayActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.high_score_button).setOnClickListener((view) -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    HighScoreActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.exit_button).setOnClickListener((view) -> {
            GameMusicManager.getInstance(MainActivity.this).stopAll();
            finish();
        });
    }
}
