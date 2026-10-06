package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.R;

public class DifficultyActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_MACHINE = "selected_machine_id";
    public static final String EXTRA_SELECTED_DIFFICULTY = "selected_difficulty";
    public static final String DIFFICULTY_EASY = "easy";
    public static final String DIFFICULTY_NORMAL = "normal";
    public static final String DIFFICULTY_HARD = "hard";

    private static final String STATE_SELECTED_MACHINE = "selected_machine_state";
    private static final String STATE_SELECTED_DIFFICULTY = "selected_difficulty_state";

    private final View[] difficultyCards = new View[3];
    private final Button[] difficultyButtons = new Button[3];
    private final String[] difficultyIds = {
            DIFFICULTY_EASY,
            DIFFICULTY_NORMAL,
            DIFFICULTY_HARD
    };

    private String selectedMachineId = MachineSelectionActivity.MACHINE_CREWZER;
    private String selectedDifficultyId = DIFFICULTY_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_difficulty);

        applySystemBarInsets();
        readSelectionState(savedInstanceState);
        bindMachineSummary();
        setupDifficultyOptions();
        selectDifficulty(selectedDifficultyId);

        findViewById(R.id.difficulty_back_button).setOnClickListener((view) -> {
            finish();
        });

        findViewById(R.id.start_mission_button).setOnClickListener((view) -> {
            Intent intent = new Intent(
                    DifficultyActivity.this,
                    GameActivity.class
            );

            intent.putExtra(EXTRA_SELECTED_MACHINE, selectedMachineId);
            intent.putExtra(EXTRA_SELECTED_DIFFICULTY, selectedDifficultyId);

            startActivity(intent);
        });
    }

    private void readSelectionState(Bundle savedInstanceState) {
        String intentMachineId = getIntent().getStringExtra(EXTRA_SELECTED_MACHINE);

        if (isValidMachineId(intentMachineId)) {
            selectedMachineId = intentMachineId;
        }

        if (savedInstanceState == null) {
            return;
        }

        String restoredMachineId = savedInstanceState.getString(STATE_SELECTED_MACHINE);
        String restoredDifficultyId = savedInstanceState.getString(STATE_SELECTED_DIFFICULTY);

        if (isValidMachineId(restoredMachineId)) {
            selectedMachineId = restoredMachineId;
        }

        if (isValidDifficultyId(restoredDifficultyId)) {
            selectedDifficultyId = restoredDifficultyId;
        }
    }

    private void bindMachineSummary() {
        ImageView machineImage = findViewById(R.id.selected_machine_image);
        TextView machineName = findViewById(R.id.selected_machine_name);
        TextView pilotName = findViewById(R.id.selected_machine_pilot);

        int machineImageResId;
        int machineNameResId;
        int pilotNameResId;

        switch (selectedMachineId) {
            case MachineSelectionActivity.MACHINE_BOMBER:
                machineImageResId = R.drawable.volt_bomber;
                machineNameResId = R.string.machine_volt_bomber;
                pilotNameResId = R.string.pilot_mark_gordon;
                break;
            case MachineSelectionActivity.MACHINE_PANZER:
                machineImageResId = R.drawable.volt_panzer;
                machineNameResId = R.string.machine_volt_panzer;
                pilotNameResId = R.string.pilot_big_bert_armstrong;
                break;
            case MachineSelectionActivity.MACHINE_FRIGATE:
                machineImageResId = R.drawable.volt_frigate;
                machineNameResId = R.string.machine_volt_frigate;
                pilotNameResId = R.string.pilot_little_jon_armstrong;
                break;
            case MachineSelectionActivity.MACHINE_LANDER:
                machineImageResId = R.drawable.volt_lander;
                machineNameResId = R.string.machine_volt_lander;
                pilotNameResId = R.string.pilot_jamie_robinson;
                break;
            case MachineSelectionActivity.MACHINE_CREWZER:
            default:
                machineImageResId = R.drawable.volt_crewzer;
                machineNameResId = R.string.machine_volt_crewzer;
                pilotNameResId = R.string.pilot_steve_armstrong;
                break;
        }

        machineImage.setImageResource(machineImageResId);
        machineImage.setContentDescription(getString(machineNameResId));
        machineName.setText(machineNameResId);
        pilotName.setText(pilotNameResId);
    }

    private void setupDifficultyOptions() {
        difficultyCards[0] = findViewById(R.id.easy_card);
        difficultyCards[1] = findViewById(R.id.normal_card);
        difficultyCards[2] = findViewById(R.id.hard_card);

        difficultyButtons[0] = findViewById(R.id.easy_select_button);
        difficultyButtons[1] = findViewById(R.id.normal_select_button);
        difficultyButtons[2] = findViewById(R.id.hard_select_button);

        for (int index = 0; index < difficultyIds.length; index++) {
            String difficultyId = difficultyIds[index];
            View card = difficultyCards[index];
            Button button = difficultyButtons[index];

            card.setTag(difficultyId);
            button.setTag(difficultyId);

            card.setOnClickListener((view) -> {
                selectDifficulty((String) view.getTag());
            });

            button.setOnClickListener((view) -> {
                selectDifficulty((String) view.getTag());
            });
        }
    }

    private void selectDifficulty(String difficultyId) {
        if (!isValidDifficultyId(difficultyId)) {
            return;
        }

        selectedDifficultyId = difficultyId;

        for (int index = 0; index < difficultyCards.length; index++) {
            boolean isSelected = difficultyId.equals(difficultyCards[index].getTag());

            difficultyCards[index].setSelected(isSelected);
            difficultyButtons[index].setSelected(isSelected);
            difficultyButtons[index].setText(
                    isSelected ? R.string.action_selected : R.string.action_select
            );
        }
    }

    private boolean isValidDifficultyId(String difficultyId) {
        return DIFFICULTY_EASY.equals(difficultyId)
                || DIFFICULTY_NORMAL.equals(difficultyId)
                || DIFFICULTY_HARD.equals(difficultyId);
    }

    private boolean isValidMachineId(String machineId) {
        return MachineSelectionActivity.MACHINE_CREWZER.equals(machineId)
                || MachineSelectionActivity.MACHINE_BOMBER.equals(machineId)
                || MachineSelectionActivity.MACHINE_PANZER.equals(machineId)
                || MachineSelectionActivity.MACHINE_FRIGATE.equals(machineId)
                || MachineSelectionActivity.MACHINE_LANDER.equals(machineId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_SELECTED_MACHINE, selectedMachineId);
        outState.putString(STATE_SELECTED_DIFFICULTY, selectedDifficultyId);
        super.onSaveInstanceState(outState);
    }

    private void applySystemBarInsets() {
        View rootView = findViewById(R.id.difficulty_root);

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
