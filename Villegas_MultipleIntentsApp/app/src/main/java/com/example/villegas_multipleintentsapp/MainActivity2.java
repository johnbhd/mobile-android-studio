package com.example.villegas_multipleintentsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    EditText meter;
    EditText lengthResult;
    EditText celsius;
    EditText temperatureResult;

    Button convertLength;
    Button convertTemperature;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        meter = (EditText) findViewById(R.id.editMeter);
        lengthResult = (EditText) findViewById(R.id.resultLength);

        celsius = (EditText) findViewById(R.id.editCelsius);
        temperatureResult = (EditText) findViewById(R.id.resultTemperature);

        convertLength = (Button) findViewById(R.id.buttonLength);
        convertTemperature = (Button) findViewById(R.id.buttonTemperature);
        back = (Button) findViewById(R.id.buttonBack);

        convertLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                String meterInput = meter.getText().toString().trim();

                lengthResult.setText("");

                if (meterInput.isEmpty()) {
                    showAlert("Validation Error", "Please enter meters.");
                    return;
                }

                try {
                    double meterValue = Double.parseDouble(meterInput);
                    double centimeter = meterValue * 100;

                    lengthResult.setText(Double.toString(centimeter) + " cm");

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid number.");
                }
            }
        });

        convertTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                String celsiusInput = celsius.getText().toString().trim();

                temperatureResult.setText("");

                if (celsiusInput.isEmpty()) {
                    showAlert("Validation Error", "Please enter Celsius.");
                    return;
                }

                try {
                    double celsiusValue = Double.parseDouble(celsiusInput);
                    double fahrenheit = (celsiusValue * 9 / 5) + 32;

                    temperatureResult.setText(Double.toString(fahrenheit) + " °F");

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid number.");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {
                finish();
            }
        });
    }

    public void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}