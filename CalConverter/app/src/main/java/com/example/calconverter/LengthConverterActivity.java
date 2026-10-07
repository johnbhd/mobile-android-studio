package com.example.calconverter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.math.BigDecimal;
import java.math.MathContext;

public class LengthConverterActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE = Color.WHITE;
    private static final int COLOR_INACTIVE = Color.parseColor("#9AA5B8");

    private EditText input;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private TextView result;

    private final double[] metersPerUnit = {
            1, 1000, 0.01, 0.001, 1609.344, 0.9144, 0.3048, 0.0254
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length_converter);
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
        setTabState(tabLength, R.id.ivTabLength, R.id.labelTabLength, true);
        setTabState(tabWeight, R.id.ivTabWeight, R.id.labelTabWeight, false);
        setTabState(tabTemperature, R.id.ivTabTemperature, R.id.labelTabTemperature, false);

        tabCalc.setOnClickListener(v -> openPage(MainActivity.class));
        tabWeight.setOnClickListener(v -> openPage(WeightConverterActivity.class));
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
        input = findViewById(R.id.etLengthInput);
        fromSpinner = findViewById(R.id.spLengthFrom);
        toSpinner = findViewById(R.id.spLengthTo);
        result = findViewById(R.id.tvLengthResult);

        ArrayAdapter<String> adapter = createSpinnerAdapter(
                getResources().getStringArray(R.array.length_units));
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        fromSpinner.setSelection(0);
        toSpinner.setSelection(1);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        fromSpinner.setOnItemSelectedListener(listener);
        toSpinner.setOnItemSelectedListener(listener);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                convert();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.btnLengthSwap).setOnClickListener(v -> {
            int from = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(from);
        });
        findViewById(R.id.btnLengthConvert).setOnClickListener(v -> {
            hideKeyboard();
            convert();
        });
    }

    private void convert() {
        String text = input.getText().toString().trim();
        if (text.isEmpty() || text.equals(".")) {
            result.setText("");
            return;
        }
        try {
            double value = Double.parseDouble(text);
            double converted = value * metersPerUnit[fromSpinner.getSelectedItemPosition()]
                    / metersPerUnit[toSpinner.getSelectedItemPosition()];
            result.setText(formatNumber(converted));
        } catch (NumberFormatException e) {
            result.setText("Invalid input");
        }
    }

    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value == 0) {
            return value == 0 ? "0" : "Error";
        }
        return new BigDecimal(value)
                .round(new MathContext(12))
                .stripTrailingZeros()
                .toPlainString();
    }

    private ArrayAdapter<String> createSpinnerAdapter(String[] units) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.mt_spinner_item, units);
        adapter.setDropDownViewResource(R.layout.mt_spinner_dropdown_item);
        return adapter;
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
