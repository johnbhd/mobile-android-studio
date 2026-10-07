package com.example.calconverter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class TemperatureConverterActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE = Color.WHITE;
    private static final int COLOR_INACTIVE = Color.parseColor("#9AA5B8");

    private EditText input;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private TextView result;
    private final DecimalFormat format =
            new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_converter);
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
        setTabState(tabWeight, R.id.ivTabWeight, R.id.labelTabWeight, false);
        setTabState(tabTemperature, R.id.ivTabTemperature, R.id.labelTabTemperature, true);

        tabCalc.setOnClickListener(v -> openPage(MainActivity.class));
        tabLength.setOnClickListener(v -> openPage(LengthConverterActivity.class));
        tabWeight.setOnClickListener(v -> openPage(WeightConverterActivity.class));
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
        input = findViewById(R.id.etTemperatureInput);
        fromSpinner = findViewById(R.id.spTemperatureFrom);
        toSpinner = findViewById(R.id.spTemperatureTo);
        result = findViewById(R.id.tvTemperatureResult);

        ArrayAdapter<String> adapter = createSpinnerAdapter(
                getResources().getStringArray(R.array.temperature_units));
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        fromSpinner.setSelection(0);
        toSpinner.setSelection(1);

        findViewById(R.id.btnTemperatureConvert).setOnClickListener(v -> convert());
        findViewById(R.id.btnTemperatureSwap).setOnClickListener(v -> {
            int from = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(from);
            result.setText("");
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
        if (text.isEmpty() || text.equals("-") || text.equals(".") || text.equals("-.")) {
            result.setText("");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            result.setText("");
            input.setError(getString(R.string.error_invalid_number));
            return;
        }

        double celsius;
        switch (fromSpinner.getSelectedItemPosition()) {
            case 1:
                celsius = (value - 32) * 5 / 9;
                break;
            case 2:
                celsius = value - 273.15;
                break;
            default:
                celsius = value;
                break;
        }

        if (celsius < -273.15) {
            result.setText("");
            input.setError(getString(R.string.error_below_absolute_zero));
            return;
        }

        double converted;
        switch (toSpinner.getSelectedItemPosition()) {
            case 1:
                converted = celsius * 9 / 5 + 32;
                break;
            case 2:
                converted = celsius + 273.15;
                break;
            default:
                converted = celsius;
                break;
        }
        result.setText(format.format(converted));
    }

    private ArrayAdapter<String> createSpinnerAdapter(String[] units) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.mt_spinner_item, units);
        adapter.setDropDownViewResource(R.layout.mt_spinner_dropdown_item);
        return adapter;
    }
}
