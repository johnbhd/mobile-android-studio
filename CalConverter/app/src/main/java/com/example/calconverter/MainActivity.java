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

public class MainActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE = Color.WHITE;
    private static final int COLOR_INACTIVE = Color.parseColor("#9AA5B8");

    // tabs
    private View tabCalc, tabLength, calcLayout, convLayout;
    private ImageView ivTabCalc, ivTabLength;
    private TextView labelTabCalc, labelTabLength;

    // calculator
    private TextView tvExpression, tvResult;
    private final StringBuilder expr = new StringBuilder();
    private boolean justEvaluated = false;

    // converter
    private EditText etInput;
    private Spinner spFrom, spTo;
    private TextView tvConvResult;

    private final String[] unitNames = {
            "Meters (m)", "Kilometers (km)", "Centimeters (cm)", "Millimeters (mm)",
            "Miles (mi)", "Yards (yd)", "Feet (ft)", "Inches (in)"
    };
    // value ng 1 unit in meters
    private final double[] toMeters = {1, 1000, 0.01, 0.001, 1609.344, 0.9144, 0.3048, 0.0254};

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
        setupConverter();
    }

    // =====================================================
    //                        TABS
    // =====================================================
    private void setupTabs() {
        tabCalc = findViewById(R.id.tabCalc);
        tabLength = findViewById(R.id.tabLength);
        calcLayout = findViewById(R.id.calcLayout);
        convLayout = findViewById(R.id.convLayout);
        ivTabCalc = findViewById(R.id.ivTabCalc);
        ivTabLength = findViewById(R.id.ivTabLength);
        labelTabCalc = findViewById(R.id.labelTabCalc);
        labelTabLength = findViewById(R.id.labelTabLength);

        tabCalc.setOnClickListener(v -> selectTab(true));
        tabLength.setOnClickListener(v -> selectTab(false));
        selectTab(true);
    }

    private void selectTab(boolean calc) {
        calcLayout.setVisibility(calc ? View.VISIBLE : View.GONE);
        convLayout.setVisibility(calc ? View.GONE : View.VISIBLE);

        tabCalc.setBackgroundResource(calc ? R.drawable.mt_tab_selected : R.drawable.mt_tab_normal);
        tabLength.setBackgroundResource(calc ? R.drawable.mt_tab_normal : R.drawable.mt_tab_selected);

        ivTabCalc.setColorFilter(calc ? COLOR_ACTIVE : COLOR_INACTIVE);
        labelTabCalc.setTextColor(calc ? COLOR_ACTIVE : COLOR_INACTIVE);
        ivTabLength.setColorFilter(calc ? COLOR_INACTIVE : COLOR_ACTIVE);
        labelTabLength.setTextColor(calc ? COLOR_INACTIVE : COLOR_ACTIVE);

        if (calc) hideKeyboard();
    }

    // =====================================================
    //                     CALCULATOR
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
                    key.setOnClickListener(v -> onCalcPress(((TextView) v).getText().toString()));
                }
            }
        }
    }

    private boolean isOp(char c) {
        return c == '+' || c == '−' || c == '×' || c == '÷';
    }

    private void refreshDisplay() {
        tvResult.setText(expr.length() == 0 ? "0" : expr.toString());
    }

    private void onCalcPress(String t) {
        switch (t) {
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

            case "+": case "−": case "×": case "÷":
                if (expr.length() == 0) {
                    if (t.equals("−")) expr.append(t);        // negative sa simula
                } else {
                    char last = expr.charAt(expr.length() - 1);
                    if (isOp(last)) {
                        if (expr.length() > 1 || !t.equals("−")) {
                            expr.setCharAt(expr.length() - 1, t.charAt(0)); // palitan operator
                        }
                    } else {
                        expr.append(t);
                    }
                }
                justEvaluated = false;
                break;

            case "%":
                if (expr.length() > 0 && Character.isDigit(expr.charAt(expr.length() - 1))) {
                    expr.append("%");
                }
                justEvaluated = false;
                break;

            case ".":
                if (justEvaluated) { expr.setLength(0); tvExpression.setText(""); justEvaluated = false; }
                if (currentNumberHasDot()) return;
                if (expr.length() == 0 || isOp(expr.charAt(expr.length() - 1))) expr.append("0");
                expr.append(".");
                break;

            default: // digits 0-9
                if (justEvaluated) { expr.setLength(0); tvExpression.setText(""); justEvaluated = false; }
                if (expr.length() > 0 && expr.charAt(expr.length() - 1) == '%') return;
                expr.append(t);
                break;
        }
        refreshDisplay();
    }

    private boolean currentNumberHasDot() {
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (isOp(c)) return false;
            if (c == '.') return true;
        }
        return false;
    }

    // +/- : binabaliktad ang sign ng huling number
    private void toggleSign() {
        int j = expr.length();
        while (j > 0) {
            char c = expr.charAt(j - 1);
            if (Character.isDigit(c) || c == '.' || c == '%') j--;
            else break;
        }
        if (j == expr.length()) return;           // walang number sa dulo

        if (j == 0) {
            expr.insert(0, '−');
        } else {
            char p = expr.charAt(j - 1);
            if (p == '−') {
                if (j - 1 == 0 || isOp(expr.charAt(j - 2))) expr.deleteCharAt(j - 1);
                else expr.setCharAt(j - 1, '+');
            } else if (p == '+') {
                expr.setCharAt(j - 1, '−');
            } else {                               // × or ÷
                expr.insert(j, '−');
            }
        }
    }

    private void evaluate() {
        if (expr.length() == 0) return;
        while (expr.length() > 0 && isOp(expr.charAt(expr.length() - 1))) {
            expr.setLength(expr.length() - 1);     // tanggalin trailing operator
        }
        if (expr.length() == 0) { refreshDisplay(); return; }

        try {
            double value = new Parser(expr.toString()).parse();
            String res = formatNumber(value).replace('-', '−');
            tvExpression.setText(expr.toString() + " =");
            expr.setLength(0);
            expr.append(res);
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

    // Recursive-descent parser (may tamang order of operations)
    private static class Parser {
        private final String s;
        private int pos = 0;

        Parser(String s) { this.s = s; }

        double parse() {
            double v = parseExpression();
            if (pos < s.length()) throw new IllegalArgumentException("Bad input");
            return v;
        }

        private double parseExpression() {
            double v = parseTerm();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '+') { pos++; v += parseTerm(); }
                else if (c == '−') { pos++; v -= parseTerm(); }
                else break;
            }
            return v;
        }

        private double parseTerm() {
            double v = parseFactor();
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if (c == '×') { pos++; v *= parseFactor(); }
                else if (c == '÷') {
                    pos++;
                    double d = parseFactor();
                    if (d == 0) throw new ArithmeticException("div by zero");
                    v /= d;
                } else break;
            }
            return v;
        }

        private double parseFactor() {
            if (pos < s.length() && s.charAt(pos) == '−') {
                pos++;
                return -parseFactor();
            }
            int start = pos;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos) == '.')) pos++;
            if (start == pos) throw new IllegalArgumentException("Number expected");
            double v = Double.parseDouble(s.substring(start, pos));
            while (pos < s.length() && s.charAt(pos) == '%') { pos++; v /= 100.0; }
            return v;
        }
    }

    private String formatNumber(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "Error";
        if (v == 0) return "0";
        return new BigDecimal(v).round(new MathContext(12)).stripTrailingZeros().toPlainString();
    }

    // =====================================================
    //                  LENGTH CONVERTER
    // =====================================================
    private void setupConverter() {
        etInput = findViewById(R.id.etInput);
        spFrom = findViewById(R.id.spFrom);
        spTo = findViewById(R.id.spTo);
        tvConvResult = findViewById(R.id.tvConvResult);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.mt_spinner_item, unitNames);
        adapter.setDropDownViewResource(R.layout.mt_spinner_dropdown_item);
        spFrom.setAdapter(adapter);
        spTo.setAdapter(adapter);
        spFrom.setSelection(0);   // Meters
        spTo.setSelection(1);     // Kilometers

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        };
        spFrom.setOnItemSelectedListener(listener);
        spTo.setOnItemSelectedListener(listener);

        etInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { convert(); }
            @Override public void afterTextChanged(Editable s) { }
        });

        findViewById(R.id.btnSwap).setOnClickListener(v -> {
            int from = spFrom.getSelectedItemPosition();
            spFrom.setSelection(spTo.getSelectedItemPosition());
            spTo.setSelection(from);
        });

        findViewById(R.id.btnConvert).setOnClickListener(v -> {
            hideKeyboard();
            convert();
        });
    }

    private void convert() {
        String input = etInput.getText().toString().trim();
        if (input.isEmpty() || input.equals(".")) {
            tvConvResult.setText("");
            return;
        }
        try {
            double value = Double.parseDouble(input);
            double result = value * toMeters[spFrom.getSelectedItemPosition()]
                    / toMeters[spTo.getSelectedItemPosition()];
            tvConvResult.setText(formatNumber(result));
        } catch (NumberFormatException e) {
            tvConvResult.setText("Invalid input");
        }
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
