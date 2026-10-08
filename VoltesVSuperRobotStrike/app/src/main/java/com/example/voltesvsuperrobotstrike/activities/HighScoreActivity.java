package com.example.voltesvsuperrobotstrike.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.R;
import com.example.voltesvsuperrobotstrike.ScorePreferences;
import com.example.voltesvsuperrobotstrike.audio.GameMusicManager;

import java.util.Locale;

public class HighScoreActivity extends AppCompatActivity {

    private TextView highScoreValueView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_high_score);
        GameMusicManager.getInstance(this).playMenuMusic();

        highScoreValueView = findViewById(R.id.high_score_value);
        findViewById(R.id.back_button).setOnClickListener((view) -> finish());
        applySystemBarInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (highScoreValueView != null) {
            highScoreValueView.setText(formatScore(ScorePreferences.getHighScore(this)));
        }
    }

    private void applySystemBarInsets() {
        View safeAreaView = findViewById(R.id.main);

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

    private String formatScore(int score) {
        return String.format(Locale.US, "%06d", Math.max(0, score));
    }
}
