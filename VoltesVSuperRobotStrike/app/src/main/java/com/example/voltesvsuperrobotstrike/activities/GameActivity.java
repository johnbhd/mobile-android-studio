package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.voltesvsuperrobotstrike.game.GameView;

public class GameActivity extends AppCompatActivity {

    private static final String STATE_MENU_PAUSED = "menu_paused_state";

    private GameView gameView;
    private View pauseButton;
    private TextView skillButton;
    private View pauseOverlay;
    private TextView pauseMachineName;
    private TextView pauseDifficultyName;
    private ImageView pausePilotImage;
    private String selectedMachineId;
    private String selectedDifficultyId;
    private boolean gameOverScreenStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        gameView = findViewById(R.id.gameView);
        pauseButton = findViewById(R.id.pause_button);
        skillButton = findViewById(R.id.skill_button);
        pauseOverlay = findViewById(R.id.pause_overlay);
        pauseMachineName = findViewById(R.id.pause_machine_name);
        pauseDifficultyName = findViewById(R.id.pause_difficulty_name);
        pausePilotImage = findViewById(R.id.pause_pilot_image);

        applySystemBarInsets();
        setupPauseNavigation();
        skillButton.setOnClickListener(view -> gameView.requestSkillActivation());
        gameView.setSkillStateListener(this::handleSkillStateChanged);
        gameView.setGameOverListener(this::handleGameOver);
        configureGameFromIntent(getIntent());

        if (savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_MENU_PAUSED, false)) {
            showPauseMenu();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        gameOverScreenStarted = false;
        configureGameFromIntent(intent);
        hidePauseMenuAndResume();
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

        this.selectedMachineId = selectedMachineId;
        this.selectedDifficultyId = selectedDifficultyId;
        gameView.configureGame(selectedMachineId, selectedDifficultyId);
        updatePauseMenuSessionInfo();
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
            hidePauseUi();
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

    private void setupPauseNavigation() {
        pauseButton.setOnClickListener(view -> {
            showPauseMenu();
        });

        findViewById(R.id.resume_button).setOnClickListener(view -> {
            hidePauseMenuAndResume();
        });

        findViewById(R.id.restart_mission_button).setOnClickListener(view -> {
            restartMission();
        });

        findViewById(R.id.change_machine_button).setOnClickListener(view -> {
            changeMachine();
        });

        findViewById(R.id.pause_main_menu_button).setOnClickListener(view -> {
            returnToMainMenu();
        });

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (gameView == null || gameView.isGameOver()) {
                            return;
                        }

                        if (gameView.isMenuPaused()) {
                            hidePauseMenuAndResume();
                        } else {
                            showPauseMenu();
                        }
                    }
                }
        );
    }

    private void showPauseMenu() {
        if (gameView == null
                || gameView.isGameOver()
                || gameView.isMenuPaused()) {
            return;
        }

        updatePauseMenuSessionInfo();
        gameView.setMenuPaused(true);
        pauseButton.setVisibility(View.GONE);
        skillButton.setVisibility(View.GONE);
        pauseOverlay.setVisibility(View.VISIBLE);
        pauseOverlay.requestFocus();
    }

    private void hidePauseMenuAndResume() {
        pauseOverlay.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);

        if (gameView != null) {
            gameView.setMenuPaused(false);
        }
        if (gameView != null && !gameView.isGameOver()) {
            skillButton.setVisibility(View.VISIBLE);
        }
    }

    private void hidePauseUi() {
        pauseOverlay.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        skillButton.setVisibility(View.GONE);
    }

    private void handleSkillStateChanged(
            String skillLabel,
            GameView.SkillState skillState,
            int remainingSeconds
    ) {
        runOnUiThread(() -> {
            if (skillButton == null || isFinishing() || isDestroyed()) {
                return;
            }

            boolean ready = skillState == GameView.SkillState.READY;
            skillButton.setEnabled(ready && gameView.isSkillInputReady());
            if (skillState == GameView.SkillState.READY) {
                skillButton.setText(skillLabel);
                skillButton.setContentDescription(
                        getString(R.string.game_skill_button_ready_description, skillLabel)
                );
            } else {
                skillButton.setText(getString(
                        R.string.game_skill_button_timer,
                        skillLabel,
                        remainingSeconds
                ));
                int descriptionResId = skillState == GameView.SkillState.ACTIVE
                        ? R.string.game_skill_button_active_description
                        : R.string.game_skill_button_cooldown_description;
                skillButton.setContentDescription(
                        getString(descriptionResId, skillLabel, remainingSeconds)
                );
            }
        });
    }

    private void restartMission() {
        Intent intent = new Intent(this, GameActivity.class);
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

        gameView.pauseGame();
        startActivity(intent);
    }

    private void changeMachine() {
        Intent intent = new Intent(this, MachineSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        gameView.pauseGame();
        startActivity(intent);
        finish();
    }

    private void returnToMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(
                MainActivity.EXTRA_SPLASH_COMPLETE,
                true
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        gameView.pauseGame();
        startActivity(intent);
        finish();
    }

    private void updatePauseMenuSessionInfo() {
        if (pauseMachineName == null || pauseDifficultyName == null) {
            return;
        }

        pauseMachineName.setText(getMachineDisplayName(selectedMachineId));
        pauseDifficultyName.setText(getDifficultyDisplayName(selectedDifficultyId));
        pausePilotImage.setImageResource(getPilotImageResource(selectedMachineId));
        pausePilotImage.setContentDescription(
                getString(getPilotNameResource(selectedMachineId))
        );
    }

    private void applySystemBarInsets() {
        View rootView = findViewById(R.id.game_root);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            FrameLayout.LayoutParams pauseButtonParams =
                    (FrameLayout.LayoutParams) pauseButton.getLayoutParams();
            int hudMargin = getResources().getDimensionPixelSize(
                    R.dimen.game_hud_margin
            );
            pauseButtonParams.topMargin = systemBars.top + hudMargin;
            pauseButtonParams.rightMargin = systemBars.right + hudMargin;
            pauseButton.setLayoutParams(pauseButtonParams);

            FrameLayout.LayoutParams skillButtonParams =
                    (FrameLayout.LayoutParams) skillButton.getLayoutParams();
            skillButtonParams.bottomMargin = systemBars.bottom + hudMargin;
            skillButtonParams.rightMargin = systemBars.right + hudMargin;
            skillButton.setLayoutParams(skillButtonParams);
            return insets;
        });
        ViewCompat.requestApplyInsets(rootView);
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
            gameView.setSkillStateListener(null);
            gameView.releaseGame();
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(
                STATE_MENU_PAUSED,
                gameView != null && gameView.isMenuPaused()
        );
        super.onSaveInstanceState(outState);
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

    private int getPilotImageResource(String machineId) {
        switch (machineId) {
            case MachineSelectionActivity.MACHINE_BOMBER:
                return R.drawable.pilot_mark_gordon;
            case MachineSelectionActivity.MACHINE_PANZER:
                return R.drawable.pilot_big_bert_armstrong;
            case MachineSelectionActivity.MACHINE_FRIGATE:
                return R.drawable.pilot_little_jon_armstrong;
            case MachineSelectionActivity.MACHINE_LANDER:
                return R.drawable.pilot_jamie_robinson;
            case MachineSelectionActivity.MACHINE_CREWZER:
            default:
                return R.drawable.pilot_steve_armstrong;
        }
    }

    private int getPilotNameResource(String machineId) {
        switch (machineId) {
            case MachineSelectionActivity.MACHINE_BOMBER:
                return R.string.pilot_mark_gordon;
            case MachineSelectionActivity.MACHINE_PANZER:
                return R.string.pilot_big_bert_armstrong;
            case MachineSelectionActivity.MACHINE_FRIGATE:
                return R.string.pilot_little_jon_armstrong;
            case MachineSelectionActivity.MACHINE_LANDER:
                return R.string.pilot_jamie_robinson;
            case MachineSelectionActivity.MACHINE_CREWZER:
            default:
                return R.string.pilot_steve_armstrong;
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
