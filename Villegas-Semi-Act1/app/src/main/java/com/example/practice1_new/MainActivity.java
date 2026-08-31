package com.example.practice1_new;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText num1;
    EditText num2;
    EditText result;
    Button compute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        num1 = (EditText) findViewById(R.id.edit1);
        num2 = (EditText) findViewById(R.id.edit2);
        result = (EditText) findViewById(R.id.edit3);
        compute = (Button) findViewById(R.id.button1);

        compute.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View y) {
                String firstInput = num1.getText().toString().trim();
                String secondInput = num2.getText().toString().trim();

                result.setText("");

                inputValidation(firstInput, secondInput);

                try {
                    int no1 = Integer.parseInt(firstInput);
                    int no2 = Integer.parseInt(secondInput);

                    int sum = no1 + no2;

                    result.setText(Integer.toString(sum));

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "please enter valid number");
                }

            }
        });
    }
    public void inputValidation(String firstInput, String secondInput) {
        if (firstInput.isEmpty() && secondInput.isEmpty()) {
            showAlert("Validation Error", "Please enter the first and second numbers.");
            return;
        }
        if (firstInput.isEmpty()) {
            showAlert("Validation Error", "Please enter the first number.");
            return;
        }
        if (secondInput.isEmpty()) {
            showAlert("Validation Error", "Please enter the second number.");
            return;
        }
    }
    public void showAlert(String tite, String message) {
        new AlertDialog.Builder(this)
                .setTitle(tite)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}