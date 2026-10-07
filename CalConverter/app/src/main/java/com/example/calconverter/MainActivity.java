package com.example.calconverter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PAGE_CALCULATOR = 0;
    private static final int PAGE_LENGTH = 1;
    private static final int PAGE_WEIGHT = 2;
    private static final int PAGE_TEMPERATURE = 3;

    private static final int COLOR_ACTIVE = Color.WHITE;
    private static final int COLOR_INACTIVE = Color.parseColor("#9AA5B8");
    private static final char OP_MINUS = '\u2212';
    private static final char OP_MULTIPLY = '\u00D7';
    private static final char OP_DIVIDE = '\u00F7';

    // Top navigation
    private View tabCalc, tabLength, tabWeight, tabTemperature;
    private ImageView ivTabCalc, ivTabLength, ivTabWeight, ivTabTemperature;
    private TextView labelTabCalc, labelTabLength, labelTabWeight, labelTabTemperature;
    private View calcLayout, lengthLayout, weightLayout, temperatureLayout;

    // Calculator
    private TextView tvExpression, tvResult;
    private final StringBuilder expr = new StringBuilder();
    private boolean justEvaluated = false;

    // Length converter
    private EditText etLengthInput;
    private Spinner spLengthFrom, spLengthTo;
    private TextView tvLengthResult;
    private final double[] lengthToMeters = {
            1, 1000, 0.01, 0.001, 1609.344, 0.9144, 0.3048, 0.0254
    };

    // Weight converter
    private EditText etWeightInput;
    private Spinner spWeightFrom, spWeightTo;
    private TextView tvWeightResult;
    private static final double[] KG_PER_UNIT = {
            1.0, 0.001, 0.45359237, 0.028349523125
    };

    // Temperature converter
    private EditText etTemperatureInput;
    private Spinner spTemperatureFrom, spTemperatureTo;
    private TextView tvTemperatureResult;
    private final DecimalFormat temperatureFormat =
            new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));
    private final DecimalFormat weightFormat =
            new DecimalFormat("#.####", DecimalFormatSymbols.getInstance(Locale.US));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.parseColor("#0F1623"));
        getWindow().setNavigationBarColor(Color.parseColor("#0F1623"));
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);

        setupTabs();
        setupCalculator();
        setupLengthConverter();
        setupWeightConverter();
        setupTemperatureConverter();
    }

    // =====================================================
    //                         TABS
    // =====================================================
    private void setupTabs() {
        tabCalc = findViewById(R.id.tabCalc);
        tabLength = findViewById(R.id.tabLength);
        tabWeight = findViewById(R.id.tabWeight);
        tabTemperature = findViewById(R.id.tabTemperature);

        calcLayout = findViewById(R.id.calcLayout);
        lengthLayout = findViewById(R.id.lengthLayout);
        weightLayout = findViewById(R.id.weightLayout);
        temperatureLayout = findViewById(R.id.temperatureLayout);

        ivTabCalc = findViewById(R.id.ivTabCalc);
        ivTabLength = findViewById(R.id.ivTabLength);
        ivTabWeight = findViewById(R.id.ivTabWeight);
        ivTabTemperature = findViewById(R.id.ivTabTemperature);

        labelTabCalc = findViewById(R.id.labelTabCalc);
        labelTabLength = findViewById(R.id.labelTabLength);
        labelTabWeight = findViewById(R.id.labelTabWeight);
        labelTabTemperature = findViewById(R.id.labelTabTemperature);

        tabCalc.setOnClickListener(v -> selectTab(PAGE_CALCULATOR));
        tabLength.setOnClickListener(v -> selectTab(PAGE_LENGTH));
        tabWeight.setOnClickListener(v -> selectTab(PAGE_WEIGHT));
        tabTemperature.setOnClickListener(v -> selectTab(PAGE_TEMPERATURE));
        selectTab(PAGE_CALCULATOR);
    }

    private void selectTab(int page) {
        calcLayout.setVisibility(page == PAGE_CALCULATOR ? View.VISIBLE : View.GONE);
        lengthLayout.setVisibility(page == PAGE_LENGTH ? View.VISIBLE : View.GONE);
        weightLayout.setVisibility(page == PAGE_WEIGHT ? View.VISIBLE : View.GONE);
        temperatureLayout.setVisibility(page == PAGE_TEMPERATURE ? View.VISIBLE : View.GONE);

        setTabState(tabCalc, ivTabCalc, labelTabCalc, page == PAGE_CALCULATOR);
        setTabState(tabLength, ivTabLength, labelTabLength, page == PAGE_LENGTH);
        setTabState(tabWeight, ivTabWeight, labelTabWeight, page == PAGE_WEIGHT);
        setTabState(tabTemperature, ivTabTemperature, labelTabTemperature,
                page == PAGE_TEMPERATURE);
        hideKeyboard();
    }

    private void setTabState(View tab, ImageView icon, TextView label, boolean active) {
        tab.setBackgroundResource(active ? R.drawable.mt_tab_selected : R.drawable.mt_tab_normal);
        icon.setColorFilter(active ? COLOR_ACTIVE : COLOR_INACTIVE);
        label.setTextColor(active ? COLOR_ACTIVE : COLOR_INACTIVE);
    }

    // =====================================================
    //                       CALCULATOR
    // =====================================================
    private void setupCalculator() {
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        ViewGroup keys = findViewById(R.id.calcKeys);
        for (int r = 0; r < keys.getChildCount(); r++) {
            ViewGroup row = (ViewGroup) keys.getChildAt(r);
            for (int c = 0; c < row.getChildCount(); c++) {
                View key = row.getChildAt(c);
                if (key instanceof TextView) {
                    key.setOnClickListener(v ->
                            onCalcPress(((TextView) v).getText().toString()));
                }
            }
        }
    }

    private boolean isOp(char c) {
        return c == '+' || c == OP_MINUS || c == OP_MULTIPLY || c == OP_DIVIDE;
    }

    private void refreshDisplay() {
        tvResult.setText(expr.length() == 0 ? "0" : expr.toString());
    }

    private void onCalcPress(String text) {
        switch (text) {
            case "AC":
                expr.setLength(0);
                tvExpression.setText("");
                justEvaluated = false;
                break;
            case "+/-":
                toggleSign();
                break;
            case "=":
                evaluate();
                return;
            case "+":
            case "\u2212":
            case "\u00D7":
            case "\u00F7":
                if (expr.length() == 0) {
                    if (text.charAt(0) == OP_MINUS) {
                        expr.append(OP_MINUS);
                    }
                } else {
                    char last = expr.charAt(expr.length() - 1);
                    if (isOp(last)) {
                        if (expr.length() > 1 || text.charAt(0) != OP_MINUS) {
                            expr.setCharAt(expr.length() - 1, text.charAt(0));
                        }
                    } else {
                        expr.append(text.charAt(0));
                    }
                }
                justEvaluated = false;
                break;
            case "%":
                if (expr.length() > 0
                        && Character.isDigit(expr.charAt(expr.length() - 1))) {
                    expr.append('%');
                }
                justEvaluated = false;
                break;
            case ".":
                if (justEvaluated) {
                    expr.setLength(0);
                    tvExpression.setText("");
                    justEvaluated = false;
                }
                if (currentNumberHasDot()) {
                    return;
                }
                if (expr.length() == 0 || isOp(expr.charAt(expr.length() - 1))) {
                    expr.append('0');
                }
                expr.append('.');
                break;
            default:
                if (justEvaluated) {
                    expr.setLength(0);
                    tvExpression.setText("");
                    justEvaluated = false;
                }
                if (expr.length() > 0 && expr.charAt(expr.length() - 1) == '%') {
                    return;
                }
                expr.append(text);
                break;
        }
        refreshDisplay();
    }

    private boolean currentNumberHasDot() {
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (isOp(c)) {
                return false;
            }
            if (c == '.') {
                return true;
            }
        }
        return false;
    }

    private void toggleSign() {
        int index = expr.length();
        while (index > 0) {
            char c = expr.charAt(index - 1);
            if (Character.isDigit(c) || c == '.' || c == '%') {
                index--;
            } else {
                break;
            }
        }
        if (index == expr.length()) {
            return;
        }

        if (index == 0) {
            expr.insert(0, OP_MINUS);
        } else {
            char previous = expr.charAt(index - 1);
            if (previous == OP_MINUS) {
                if (index - 1 == 0 || isOp(expr.charAt(index - 2))) {
                    expr.deleteCharAt(index - 1);
                } else {
                    expr.setCharAt(index - 1, '+');
                }
            } else if (previous == '+') {
                expr.setCharAt(index - 1, OP_MINUS);
            } else {
                expr.insert(index, OP_MINUS);
            }
        }
        refreshDisplay();
    }

    private void evaluate() {
        if (expr.length() == 0) {
            return;
        }
        while (expr.length() > 0 && isOp(expr.charAt(expr.length() - 1))) {
            expr.setLength(expr.length() - 1);
        }
        if (expr.length() == 0) {
            refreshDisplay();
            return;
        }

        try {
            double value = new Parser(expr.toString()).parse();
            String result = formatNumber(value).replace('-', OP_MINUS);
            tvExpression.setText(expr + " =");
            expr.setLength(0);
            expr.append(result);
            justEvaluated = true;
            refreshDisplay();
        } catch (ArithmeticException e) {
            tvResult.setText("Cannot divide by 0");
            expr.setLength(0);
            justEvaluated = false;
        } catch (Exception e) {
            tvResult.setText("Error");
            expr.setLength(0);
            justEvaluated = false;
        }
    }

    private static class Parser {
        private final String input;
        private int position = 0;

        Parser(String input) {
            this.input = input;
        }

        double parse() {
            double value = parseExpression();
            if (position < input.length()) {
                throw new IllegalArgumentException("Bad input");
            }
            return value;
        }

        private double parseExpression() {
            double value = parseTerm();
            while (position < input.length()) {
                char operator = input.charAt(position);
                if (operator == '+') {
                    position++;
                    value += parseTerm();
                } else if (operator == OP_MINUS) {
                    position++;
                    value -= parseTerm();
                } else {
                    break;
                }
            }
            return value;
        }

        private double parseTerm() {
            double value = parseFactor();
            while (position < input.length()) {
                char operator = input.charAt(position);
                if (operator == OP_MULTIPLY) {
                    position++;
                    value *= parseFactor();
                } else if (operator == OP_DIVIDE) {
                    position++;
                    double divisor = parseFactor();
                    if (divisor == 0) {
                        throw new ArithmeticException("div by zero");
                    }
                    value /= divisor;
                } else {
                    break;
                }
            }
            return value;
        }

        private double parseFactor() {
            if (position < input.length() && input.charAt(position) == OP_MINUS) {
                position++;
                return -parseFactor();
            }
            int start = position;
            while (position < input.length()
                    && (Character.isDigit(input.charAt(position))
                    || input.charAt(position) == '.')) {
                position++;
            }
            if (start == position) {
                throw new IllegalArgumentException("Number expected");
            }
            double value = Double.parseDouble(input.substring(start, position));
            while (position < input.length() && input.charAt(position) == '%') {
                position++;
                value /= 100.0;
            }
            return value;
        }
    }

    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "Error";
        }
        if (value == 0) {
            return "0";
        }
        return new BigDecimal(value)
                .round(new MathContext(12))
                .stripTrailingZeros()
                .toPlainString();
    }

    // =====================================================
    //                    LENGTH CONVERTER
    // =====================================================
    private void setupLengthConverter() {
        etLengthInput = findViewById(R.id.etLengthInput);
        spLengthFrom = findViewById(R.id.spLengthFrom);
        spLengthTo = findViewById(R.id.spLengthTo);
        tvLengthResult = findViewById(R.id.tvLengthResult);

        ArrayAdapter<String> adapter = createSpinnerAdapter(
                getResources().getStringArray(R.array.length_units));
        spLengthFrom.setAdapter(adapter);
        spLengthTo.setAdapter(adapter);
        spLengthFrom.setSelection(0);
        spLengthTo.setSelection(1);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convertLength();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spLengthFrom.setOnItemSelectedListener(listener);
        spLengthTo.setOnItemSelectedListener(listener);

        etLengthInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                convertLength();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.btnLengthSwap).setOnClickListener(v -> {
            int from = spLengthFrom.getSelectedItemPosition();
            spLengthFrom.setSelection(spLengthTo.getSelectedItemPosition());
            spLengthTo.setSelection(from);
        });
        findViewById(R.id.btnLengthConvert).setOnClickListener(v -> {
            hideKeyboard();
            convertLength();
        });
    }

    private void convertLength() {
        String input = etLengthInput.getText().toString().trim();
        if (input.isEmpty() || input.equals(".")) {
            tvLengthResult.setText("");
            return;
        }
        try {
            double value = Double.parseDouble(input);
            double result = value * lengthToMeters[spLengthFrom.getSelectedItemPosition()]
                    / lengthToMeters[spLengthTo.getSelectedItemPosition()];
            tvLengthResult.setText(formatNumber(result));
        } catch (NumberFormatException e) {
            tvLengthResult.setText("Invalid input");
        }
    }

    // =====================================================
    //                     WEIGHT CONVERTER
    // =====================================================
    private void setupWeightConverter() {
        etWeightInput = findViewById(R.id.etWeightInput);
        spWeightFrom = findViewById(R.id.spWeightFrom);
        spWeightTo = findViewById(R.id.spWeightTo);
        tvWeightResult = findViewById(R.id.tvWeightResult);

        ArrayAdapter<String> adapter = createSpinnerAdapter(
                getResources().getStringArray(R.array.weight_units));
        spWeightFrom.setAdapter(adapter);
        spWeightTo.setAdapter(adapter);
        spWeightFrom.setSelection(0);
        spWeightTo.setSelection(2);

        findViewById(R.id.btnWeightConvert).setOnClickListener(v -> convertWeight());
        findViewById(R.id.btnWeightSwap).setOnClickListener(v -> {
            int from = spWeightFrom.getSelectedItemPosition();
            spWeightFrom.setSelection(spWeightTo.getSelectedItemPosition());
            spWeightTo.setSelection(from);
            tvWeightResult.setText("");
        });

        etWeightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvWeightResult.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvWeightResult.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spWeightFrom.setOnItemSelectedListener(listener);
        spWeightTo.setOnItemSelectedListener(listener);
    }

    private void convertWeight() {
        String input = etWeightInput.getText().toString().trim();
        if (input.isEmpty() || input.equals(".")) {
            tvWeightResult.setText("");
            return;
        }
        try {
            double value = Double.parseDouble(input);
            int from = spWeightFrom.getSelectedItemPosition();
            int to = spWeightTo.getSelectedItemPosition();
            double kilograms = value * KG_PER_UNIT[from];
            double result = kilograms / KG_PER_UNIT[to];
            tvWeightResult.setText(weightFormat.format(result));
        } catch (NumberFormatException e) {
            tvWeightResult.setText("");
            etWeightInput.setError(getString(R.string.error_invalid_number));
        }
    }

    // =====================================================
    //                 TEMPERATURE CONVERTER
    // =====================================================
    private void setupTemperatureConverter() {
        etTemperatureInput = findViewById(R.id.etTemperatureInput);
        spTemperatureFrom = findViewById(R.id.spTemperatureFrom);
        spTemperatureTo = findViewById(R.id.spTemperatureTo);
        tvTemperatureResult = findViewById(R.id.tvTemperatureResult);

        ArrayAdapter<String> adapter = createSpinnerAdapter(
                getResources().getStringArray(R.array.temperature_units));
        spTemperatureFrom.setAdapter(adapter);
        spTemperatureTo.setAdapter(adapter);
        spTemperatureFrom.setSelection(0);
        spTemperatureTo.setSelection(1);

        findViewById(R.id.btnTemperatureConvert)
                .setOnClickListener(v -> convertTemperature());
        findViewById(R.id.btnTemperatureSwap).setOnClickListener(v -> {
            int from = spTemperatureFrom.getSelectedItemPosition();
            spTemperatureFrom.setSelection(spTemperatureTo.getSelectedItemPosition());
            spTemperatureTo.setSelection(from);
            tvTemperatureResult.setText("");
        });

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvTemperatureResult.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spTemperatureFrom.setOnItemSelectedListener(listener);
        spTemperatureTo.setOnItemSelectedListener(listener);
    }

    private void convertTemperature() {
        String input = etTemperatureInput.getText().toString().trim();
        if (input.isEmpty() || input.equals("-") || input.equals(".")
                || input.equals("-.")) {
            tvTemperatureResult.setText("");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            tvTemperatureResult.setText("");
            etTemperatureInput.setError(getString(R.string.error_invalid_number));
            return;
        }

        int from = spTemperatureFrom.getSelectedItemPosition();
        int to = spTemperatureTo.getSelectedItemPosition();
        double celsius;
        switch (from) {
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
            tvTemperatureResult.setText("");
            etTemperatureInput.setError(getString(R.string.error_below_absolute_zero));
            return;
        }

        double result;
        switch (to) {
            case 1:
                result = celsius * 9 / 5 + 32;
                break;
            case 2:
                result = celsius + 273.15;
                break;
            default:
                result = celsius;
                break;
        }
        tvTemperatureResult.setText(temperatureFormat.format(result));
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
