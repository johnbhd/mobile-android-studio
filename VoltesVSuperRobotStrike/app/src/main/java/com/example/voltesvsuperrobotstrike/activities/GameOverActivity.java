package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.MainActivity;
import com.example.voltesvsuperrobotstrike.R;
import com.example.voltesvsuperrobotstrike.ScorePreferences;
import com.example.voltesvsuperrobotstrike.audio.GameMusicManager;

import java.util.Locale;

public class GameOverActivity extends AppCompatActivity {

    public static final String EXTRA_FINAL_SCORE =
            "com.example.voltesvsuperrobotstrike.extra.FINAL_SCORE";
    public static final String EXTRA_HIGH_SCORE =
            "com.example.voltesvsuperrobotstrike.extra.HIGH_SCORE";
    public static final String EXTRA_NEW_HIGH_SCORE =
            "com.example.voltesvsuperrobotstrike.extra.NEW_HIGH_SCORE";
    public static final String EXTRA_SELECTED_MACHINE =
            "com.example.voltesvsuperrobotstrike.extra.SELECTED_MACHINE";
    public static final String EXTRA_DIFFICULTY =
            "com.example.voltesvsuperrobotstrike.extra.DIFFICULTY";

    private int finalScore;
    private int highScore;
    private boolean newHighScore;
    private String selectedMachineId;
    private String selectedDifficultyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);
        GameMusicManager.getInstance(this).playMenuMusic();

        readRunResult();
        bindRunResult();
        setupNavigation();
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        returnToMainMenu();
                    }
                }
        );
        applySystemBarInsets();
    }

    private void readRunResult() {
        finalScore = Math.max(0, getIntent().getIntExtra(EXTRA_FINAL_SCORE, 0));
        int savedHighScore = ScorePreferences.getHighScore(this);
        highScore = Math.max(
                savedHighScore,
                getIntent().getIntExtra(EXTRA_HIGH_SCORE, savedHighScore)
        );
        newHighScore = getIntent().getBooleanExtra(EXTRA_NEW_HIGH_SCORE, false);
        selectedMachineId = getIntent().getStringExtra(EXTRA_SELECTED_MACHINE);
        selectedDifficultyId = getIntent().getStringExtra(EXTRA_DIFFICULTY);

        if (!isValidMachineId(selectedMachineId)) {
            selectedMachineId = MachineSelectionActivity.MACHINE_CREWZER;
        }
        if (!isValidDifficultyId(selectedDifficultyId)) {
            selectedDifficultyId = DifficultyActivity.DIFFICULTY_NORMAL;
        }
    }

    private void bindRunResult() {
        TextView finalScoreView = findViewById(R.id.final_score_value);
        TextView highScoreView = findViewById(R.id.high_score_value);
        TextView newHighScoreView = findViewById(R.id.new_high_score_label);
        TextView runSummaryView = findViewById(R.id.game_over_run_summary);

        finalScoreView.setText(formatScore(finalScore));
        highScoreView.setText(formatScore(highScore));
        newHighScoreView.setVisibility(newHighScore ? View.VISIBLE : View.GONE);
        runSummaryView.setText(getString(
                R.string.game_over_run_summary,
                getMachineDisplayName(selectedMachineId),
                getDifficultyDisplayName(selectedDifficultyId)
        ));
    }

    private void setupNavigation() {
        findViewById(R.id.play_again_button).setOnClickListener((view) -> {
            Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
            intent.putExtra(
                    DifficultyActivity.EXTRA_SELECTED_MACHINE,
                    selectedMachineId
            );
            intent.putExtra(
                    DifficultyActivity.EXTRA_SELECTED_DIFFICULTY,
                    selectedDifficultyId
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.main_menu_button).setOnClickListener((view) -> {
            returnToMainMenu();
        });
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_SPLASH_COMPLETE, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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

    private String getMachineDisplayName(String machineId) {
        switch (machineId) {
            case MachineSelectionActivity.MACHINE_BOMBER:
                return getString(R.string.machine_volt_bomber);
            case MachineSelectionActivity.MACHINE_PANZER:
                return getString(R.string.machine_volt_panzer);
            case MachineSelectionActivity.MACHINE_FRIGATE:
                return getString(R.string.machine_volt_frigate);
            case MachineSelectionActivity.MACHINE_LANDER:
                return getString(R.string.machine_volt_lander);
            case MachineSelectionActivity.MACHINE_CREWZER:
            default:
                return getString(R.string.machine_volt_crewzer);
        }
    }

    private String getDifficultyDisplayName(String difficultyId) {
        switch (difficultyId) {
            case DifficultyActivity.DIFFICULTY_EASY:
                return getString(R.string.difficulty_easy);
            case DifficultyActivity.DIFFICULTY_HARD:
                return getString(R.string.difficulty_hard);
            case DifficultyActivity.DIFFICULTY_NORMAL:
            default:
                return getString(R.string.difficulty_normal);
        }
    }

    private boolean isValidMachineId(String machineId) {
        return MachineSelectionActivity.MACHINE_CREWZER.equals(machineId)
                || MachineSelectionActivity.MACHINE_BOMBER.equals(machineId)
                || MachineSelectionActivity.MACHINE_PANZER.equals(machineId)
                || MachineSelectionActivity.MACHINE_FRIGATE.equals(machineId)
                || MachineSelectionActivity.MACHINE_LANDER.equals(machineId);
    }

    private boolean isValidDifficultyId(String difficultyId) {
        return DifficultyActivity.DIFFICULTY_EASY.equals(difficultyId)
                || DifficultyActivity.DIFFICULTY_NORMAL.equals(difficultyId)
                || DifficultyActivity.DIFFICULTY_HARD.equals(difficultyId);
    }
}
