package com.example.voltesvsuperrobotstrike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.voltesvsuperrobotstrike.R;

public class MachineSelectionActivity extends AppCompatActivity {

    private static final String MACHINE_CREWZER = "volt_crewzer";
    private static final String MACHINE_BOMBER = "volt_bomber";
    private static final String MACHINE_PANZER = "volt_panzer";
    private static final String MACHINE_FRIGATE = "volt_frigate";
    private static final String MACHINE_LANDER = "volt_lander";
    private static final String STATE_SELECTED_MACHINE = "selected_machine_state";

    private final View[] machineCards = new View[5];
    private final Button[] selectButtons = new Button[5];

    private String selectedMachineId = MACHINE_CREWZER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_machine_selection);

        applySystemBarInsets();
        setupMachineCards();

        if (savedInstanceState != null) {
            String restoredMachineId = savedInstanceState.getString(STATE_SELECTED_MACHINE);

            if (isValidMachineId(restoredMachineId)) {
                selectedMachineId = restoredMachineId;
            }
        }

        selectMachine(selectedMachineId);

        findViewById(R.id.back_button).setOnClickListener((view) -> {
            finish();
        });

        findViewById(R.id.continue_button).setOnClickListener((view) -> {
            Intent intent = new Intent(
                    MachineSelectionActivity.this,
                    DifficultyActivity.class
            );

            intent.putExtra(
                    DifficultyActivity.EXTRA_SELECTED_MACHINE,
                    selectedMachineId
            );

            startActivity(intent);
        });
    }

    private void setupMachineCards() {
        bindMachineCard(
                0,
                R.id.crewzer_card,
                MACHINE_CREWZER,
                R.string.machine_volt_crewzer,
                R.string.pilot_steve_armstrong,
                R.drawable.volt_crewzer,
                R.drawable.pilot_steve_armstrong,
                4,
                3,
                R.string.machine_type_fast
        );

        bindMachineCard(
                1,
                R.id.bomber_card,
                MACHINE_BOMBER,
                R.string.machine_volt_bomber,
                R.string.pilot_mark_gordon,
                R.drawable.volt_bomber,
                R.drawable.pilot_mark_gordon,
                3,
                3,
                R.string.machine_type_balanced
        );

        bindMachineCard(
                2,
                R.id.panzer_card,
                MACHINE_PANZER,
                R.string.machine_volt_panzer,
                R.string.pilot_big_bert_armstrong,
                R.drawable.volt_panzer,
                R.drawable.pilot_big_bert_armstrong,
                2,
                5,
                R.string.machine_type_power
        );

        bindMachineCard(
                3,
                R.id.frigate_card,
                MACHINE_FRIGATE,
                R.string.machine_volt_frigate,
                R.string.pilot_little_jon_armstrong,
                R.drawable.volt_frigate,
                R.drawable.pilot_little_jon_armstrong,
                3,
                2,
                R.string.machine_type_balanced
        );

        bindMachineCard(
                4,
                R.id.lander_card,
                MACHINE_LANDER,
                R.string.machine_volt_lander,
                R.string.pilot_jamie_robinson,
                R.drawable.volt_lander,
                R.drawable.pilot_jamie_robinson,
                2,
                2,
                R.string.machine_type_support
        );
    }

    private void bindMachineCard(
            int index,
            int cardId,
            String machineId,
            int machineNameResId,
            int pilotNameResId,
            int machineImageResId,
            int pilotImageResId,
            int speed,
            int attack,
            int typeResId
    ) {
        View card = findViewById(cardId);
        ImageView machineImage = card.findViewById(R.id.machine_image);
        ImageView pilotImage = card.findViewById(R.id.pilot_image);
        TextView machineName = card.findViewById(R.id.machine_name);
        TextView pilotName = card.findViewById(R.id.pilot_name);
        TextView speedValue = card.findViewById(R.id.speed_value);
        TextView attackValue = card.findViewById(R.id.attack_value);
        TextView typeValue = card.findViewById(R.id.type_value);
        Button selectButton = card.findViewById(R.id.select_button);

        card.setTag(machineId);
        machineImage.setImageResource(machineImageResId);
        pilotImage.setImageResource(pilotImageResId);
        machineName.setText(machineNameResId);
        pilotName.setText(pilotNameResId);
        speedValue.setText(getString(R.string.machine_stat_value, speed));
        attackValue.setText(getString(R.string.machine_stat_value, attack));
        typeValue.setText(typeResId);

        configureStatBar(card.findViewById(R.id.speed_stat_bar), speed);
        configureStatBar(card.findViewById(R.id.attack_stat_bar), attack);

        machineCards[index] = card;
        selectButtons[index] = selectButton;

        card.setOnClickListener((view) -> {
            selectMachine((String) view.getTag());
        });

        selectButton.setOnClickListener((view) -> {
            selectMachine((String) card.getTag());
        });
    }

    private void configureStatBar(LinearLayout statBar, int filledSegments) {
        for (int index = 0; index < statBar.getChildCount(); index++) {
            int backgroundResId = index < filledSegments
                    ? R.drawable.machine_stat_segment_filled
                    : R.drawable.machine_stat_segment_empty;

            statBar.getChildAt(index).setBackgroundResource(backgroundResId);
        }
    }

    private void selectMachine(String machineId) {
        if (!isValidMachineId(machineId)) {
            return;
        }

        selectedMachineId = machineId;

        for (int index = 0; index < machineCards.length; index++) {
            View card = machineCards[index];
            boolean isSelected = machineId.equals(card.getTag());

            card.setSelected(isSelected);
            selectButtons[index].setSelected(isSelected);
            selectButtons[index].setText(
                    isSelected ? R.string.action_selected : R.string.action_select
            );
        }
    }

    private boolean isValidMachineId(String machineId) {
        return MACHINE_CREWZER.equals(machineId)
                || MACHINE_BOMBER.equals(machineId)
                || MACHINE_PANZER.equals(machineId)
                || MACHINE_FRIGATE.equals(machineId)
                || MACHINE_LANDER.equals(machineId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_SELECTED_MACHINE, selectedMachineId);
        super.onSaveInstanceState(outState);
    }

    private void applySystemBarInsets() {
        View rootView = findViewById(R.id.machine_selection_root);

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
