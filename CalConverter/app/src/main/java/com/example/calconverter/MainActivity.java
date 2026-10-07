package com.example.calconverter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.math.BigDecimal;
import java.math.MathContext;

public class MainActivity extends AppCompatActivity {

    private static final int COLOR_ACTIVE = Color.WHITE;
    private static final int COLOR_INACTIVE = Color.parseColor("#9AA5B8");
    private static final char OP_MINUS = '\u2212';
    private static final char OP_MULTIPLY = '\u00D7';
    private static final char OP_DIVIDE = '\u00F7';

    private TextView tvExpression;
    private TextView tvResult;
    private final StringBuilder expression = new StringBuilder();
    private boolean justEvaluated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applySystemBarColors();
        setupNavigation();
        setupCalculator();
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

        setTabState(tabCalc, R.id.ivTabCalc, R.id.labelTabCalc, true);
        setTabState(tabLength, R.id.ivTabLength, R.id.labelTabLength, false);
        setTabState(tabWeight, R.id.ivTabWeight, R.id.labelTabWeight, false);
        setTabState(tabTemperature, R.id.ivTabTemperature, R.id.labelTabTemperature, false);

        tabLength.setOnClickListener(v -> openPage(LengthConverterActivity.class));
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

    private void setupCalculator() {
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        ViewGroup keys = findViewById(R.id.calcKeys);
        for (int rowIndex = 0; rowIndex < keys.getChildCount(); rowIndex++) {
            ViewGroup row = (ViewGroup) keys.getChildAt(rowIndex);
            for (int keyIndex = 0; keyIndex < row.getChildCount(); keyIndex++) {
                View key = row.getChildAt(keyIndex);
                if (key instanceof TextView) {
                    key.setOnClickListener(v ->
                            onCalculatorPress(((TextView) v).getText().toString()));
                }
            }
        }
    }

    private boolean isOperator(char value) {
        return value == '+' || value == OP_MINUS
                || value == OP_MULTIPLY || value == OP_DIVIDE;
    }

    private void refreshDisplay() {
        tvResult.setText(expression.length() == 0 ? "0" : expression.toString());
    }

    private void onCalculatorPress(String text) {
        switch (text) {
            case "AC":
                expression.setLength(0);
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
                if (expression.length() == 0) {
                    if (text.charAt(0) == OP_MINUS) {
                        expression.append(OP_MINUS);
                    }
                } else {
                    char last = expression.charAt(expression.length() - 1);
                    if (isOperator(last)) {
                        if (expression.length() > 1 || text.charAt(0) != OP_MINUS) {
                            expression.setCharAt(expression.length() - 1, text.charAt(0));
                        }
                    } else {
                        expression.append(text.charAt(0));
                    }
                }
                justEvaluated = false;
                break;
            case "%":
                if (expression.length() > 0
                        && Character.isDigit(expression.charAt(expression.length() - 1))) {
                    expression.append('%');
                }
                justEvaluated = false;
                break;
            case ".":
                if (justEvaluated) {
                    expression.setLength(0);
                    tvExpression.setText("");
                    justEvaluated = false;
                }
                if (currentNumberHasDecimal()) {
                    return;
                }
                if (expression.length() == 0
                        || isOperator(expression.charAt(expression.length() - 1))) {
                    expression.append('0');
                }
                expression.append('.');
                break;
            default:
                if (justEvaluated) {
                    expression.setLength(0);
                    tvExpression.setText("");
                    justEvaluated = false;
                }
                if (expression.length() > 0
                        && expression.charAt(expression.length() - 1) == '%') {
                    return;
                }
                expression.append(text);
                break;
        }
        refreshDisplay();
    }

    private boolean currentNumberHasDecimal() {
        for (int index = expression.length() - 1; index >= 0; index--) {
            char value = expression.charAt(index);
            if (isOperator(value)) {
                return false;
            }
            if (value == '.') {
                return true;
            }
        }
        return false;
    }

    private void toggleSign() {
        int index = expression.length();
        while (index > 0) {
            char value = expression.charAt(index - 1);
            if (Character.isDigit(value) || value == '.' || value == '%') {
                index--;
            } else {
                break;
            }
        }
        if (index == expression.length()) {
            return;
        }

        if (index == 0) {
            expression.insert(0, OP_MINUS);
        } else {
            char previous = expression.charAt(index - 1);
            if (previous == OP_MINUS) {
                if (index - 1 == 0 || isOperator(expression.charAt(index - 2))) {
                    expression.deleteCharAt(index - 1);
                } else {
                    expression.setCharAt(index - 1, '+');
                }
            } else if (previous == '+') {
                expression.setCharAt(index - 1, OP_MINUS);
            } else {
                expression.insert(index, OP_MINUS);
            }
        }
        refreshDisplay();
    }

    private void evaluate() {
        if (expression.length() == 0) {
            return;
        }
        while (expression.length() > 0
                && isOperator(expression.charAt(expression.length() - 1))) {
            expression.setLength(expression.length() - 1);
        }
        if (expression.length() == 0) {
            refreshDisplay();
            return;
        }

        try {
            double value = new Parser(expression.toString()).parse();
            String result = formatNumber(value).replace('-', OP_MINUS);
            tvExpression.setText(expression + " =");
            expression.setLength(0);
            expression.append(result);
            justEvaluated = true;
            refreshDisplay();
        } catch (ArithmeticException e) {
            tvResult.setText("Cannot divide by 0");
            expression.setLength(0);
            justEvaluated = false;
        } catch (Exception e) {
            tvResult.setText("Error");
            expression.setLength(0);
            justEvaluated = false;
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
}
