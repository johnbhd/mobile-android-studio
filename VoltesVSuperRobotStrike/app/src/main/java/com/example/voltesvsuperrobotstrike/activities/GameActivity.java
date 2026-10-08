package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voltesvsuperrobotstrike.R;
import com.example.voltesvsuperrobotstrike.ScorePreferences;
import com.example.voltesvsuperrobotstrike.game.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;
    private boolean gameOverScreenStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        gameView.setGameOverListener(this::handleGameOver);
        configureGameFromIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        gameOverScreenStarted = false;
        configureGameFromIntent(intent);
    }

    private void configureGameFromIntent(Intent intent) {
        String selectedMachineId = intent.getStringExtra(
                DifficultyActivity.EXTRA_SELECTED_MACHINE
        );
        String selectedDifficultyId = intent.getStringExtra(
                DifficultyActivity.EXTRA_SELECTED_DIFFICULTY
        );

        if (!isValidMachineId(selectedMachineId)) {
            selectedMachineId = MachineSelectionActivity.MACHINE_CREWZER;
        }

        if (!isValidDifficultyId(selectedDifficultyId)) {
            selectedDifficultyId = DifficultyActivity.DIFFICULTY_NORMAL;
        }

        gameView.configureGame(selectedMachineId, selectedDifficultyId);
    }

    private void handleGameOver(
            int finalScore,
            String selectedMachineId,
            String selectedDifficultyId
    ) {
        runOnUiThread(() -> {
            if (gameOverScreenStarted || isFinishing() || isDestroyed()) {
                return;
            }

            gameOverScreenStarted = true;
            gameView.pauseGame();

            int previousHighScore = ScorePreferences.getHighScore(this);
            boolean isNewHighScore = finalScore > previousHighScore;
            int highScore = Math.max(finalScore, previousHighScore);
            if (isNewHighScore) {
                ScorePreferences.saveHighScore(this, finalScore);
            }

            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra(GameOverActivity.EXTRA_FINAL_SCORE, finalScore);
            intent.putExtra(GameOverActivity.EXTRA_HIGH_SCORE, highScore);
            intent.putExtra(GameOverActivity.EXTRA_NEW_HIGH_SCORE, isNewHighScore);
            intent.putExtra(GameOverActivity.EXTRA_SELECTED_MACHINE, selectedMachineId);
            intent.putExtra(GameOverActivity.EXTRA_DIFFICULTY, selectedDifficultyId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (gameView != null) {
            gameView.resumeGame();
        }
    }

    @Override
    protected void onPause() {
        if (gameView != null) {
            gameView.pauseGame();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (gameView != null) {
            gameView.setGameOverListener(null);
            gameView.releaseGame();
        }

        super.onDestroy();
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
