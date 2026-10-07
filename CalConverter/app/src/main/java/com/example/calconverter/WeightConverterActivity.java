package com.example.calconverter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class WeightConverterActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE = Color.WHITE;
    private static final int COLOR_INACTIVE = Color.parseColor("#9AA5B8");
    private static final double[] KG_PER_UNIT = {
            1.0, 0.001, 0.45359237, 0.028349523125
    };

    private EditText input;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private TextView result;
    private final DecimalFormat format =
            new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_converter);
        applySystemBarColors();
        setupNavigation();
        setupConverter();
    }

    private void applySystemBarColors() {
        getWindow().setStatusBarColor(Color.parseColor("#0F1623"));
        getWindow().setNavigationBarColor(Color.parseColor("#0F1623"));
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
    }

    private void setupNavigation() {
        View tabCalc = findViewById(R.id.tabCalc);
        View tabLength = findViewById(R.id.tabLength);
        View tabWeight = findViewById(R.id.tabWeight);
        View tabTemperature = findViewById(R.id.tabTemperature);

        setTabState(tabCalc, R.id.ivTabCalc, R.id.labelTabCalc, false);
        setTabState(tabLength, R.id.ivTabLength, R.id.labelTabLength, false);
        setTabState(tabWeight, R.id.ivTabWeight, R.id.labelTabWeight, true);
        setTabState(tabTemperature, R.id.ivTabTemperature, R.id.labelTabTemperature, false);

        tabCalc.setOnClickListener(v -> openPage(MainActivity.class));
        tabLength.setOnClickListener(v -> openPage(LengthConverterActivity.class));
        tabTemperature.setOnClickListener(v -> openPage(TemperatureConverterActivity.class));
    }

    private void setTabState(View tab, int iconId, int labelId, boolean active) {
        tab.setBackgroundResource(active
                ? R.drawable.mt_tab_selected
                : R.drawable.mt_tab_normal);
        ImageView icon = findViewById(iconId);
        TextView label = findViewById(labelId);
        icon.setColorFilter(active ? COLOR_ACTIVE : COLOR_INACTIVE);
        label.setTextColor(active ? COLOR_ACTIVE : COLOR_INACTIVE);
    }

    private void openPage(Class<? extends AppCompatActivity> page) {
        Intent intent = new Intent(this, page);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void setupConverter() {
        input = findViewById(R.id.etWeightInput);
        fromSpinner = findViewById(R.id.spWeightFrom);
        toSpinner = findViewById(R.id.spWeightTo);
        result = findViewById(R.id.tvWeightResult);

        ArrayAdapter<String> adapter = createSpinnerAdapter(
                getResources().getStringArray(R.array.weight_units));
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        fromSpinner.setSelection(0);
        toSpinner.setSelection(2);

        findViewById(R.id.btnWeightConvert).setOnClickListener(v -> convert());
        findViewById(R.id.btnWeightSwap).setOnClickListener(v -> {
            int from = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(from);
            result.setText("");
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                result.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                result.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        fromSpinner.setOnItemSelectedListener(listener);
        toSpinner.setOnItemSelectedListener(listener);
    }

    private void convert() {
        String text = input.getText().toString().trim();
        if (text.isEmpty() || text.equals(".")) {
            result.setText("");
            return;
        }
        try {
            double value = Double.parseDouble(text);
            int from = fromSpinner.getSelectedItemPosition();
            int to = toSpinner.getSelectedItemPosition();
            double kilograms = value * KG_PER_UNIT[from];
            result.setText(format.format(kilograms / KG_PER_UNIT[to]));
        } catch (NumberFormatException e) {
            result.setText("");
            input.setError(getString(R.string.error_invalid_number));
        }
    }

    private ArrayAdapter<String> createSpinnerAdapter(String[] units) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.mt_spinner_item, units);
        adapter.setDropDownViewResource(R.layout.mt_spinner_dropdown_item);
        return adapter;
    }
}
