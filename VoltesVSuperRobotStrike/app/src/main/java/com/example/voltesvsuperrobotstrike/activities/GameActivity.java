package com.example.voltesvsuperrobotstrike.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.R;
import com.example.voltesvsuperrobotstrike.game.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        String selectedMachineId = getIntent().getStringExtra(
                DifficultyActivity.EXTRA_SELECTED_MACHINE
        );
        String selectedDifficultyId = getIntent().getStringExtra(
                DifficultyActivity.EXTRA_SELECTED_DIFFICULTY
        );

        if (!isValidMachineId(selectedMachineId)) {
            selectedMachineId = MachineSelectionActivity.MACHINE_CREWZER;
        }

        if (!isValidDifficultyId(selectedDifficultyId)) {
            selectedDifficultyId = DifficultyActivity.DIFFICULTY_NORMAL;
        }

        gameView = findViewById(R.id.gameView);
        gameView.configureGame(selectedMachineId, selectedDifficultyId);

        applySystemBarInsets();
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

    private void applySystemBarInsets() {
        View rootView = findViewById(R.id.game_root);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
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
}
