package com.example.villegas_multipleintentsapp;

import android.content.Intent;
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
    Button product;
    Button difference;
    Button quotient;
    Button clear;
    Button converter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        num1 = (EditText) findViewById(R.id.edit1);
        num2 = (EditText) findViewById(R.id.edit2);
        result = (EditText) findViewById(R.id.edit3);

        compute = (Button) findViewById(R.id.button1);
        product = (Button) findViewById(R.id.button2);
        difference = (Button) findViewById(R.id.button3);
        quotient = (Button) findViewById(R.id.button4);
        clear = (Button) findViewById(R.id.button5);
        converter = (Button) findViewById(R.id.button6);


        compute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                String firstInput = num1.getText().toString().trim();
                String secondInput = num2.getText().toString().trim();

                result.setText("");

                if (!inputValidation(firstInput, secondInput)) {
                    return;
                }

                try {
                    int no1 = Integer.parseInt(firstInput);
                    int no2 = Integer.parseInt(secondInput);

                    int sum = no1 + no2;

                    result.setText(Integer.toString(sum));

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers.");
                }
            }
        });



        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                String firstInput = num1.getText().toString().trim();
                String secondInput = num2.getText().toString().trim();

                result.setText("");

                if (!inputValidation(firstInput, secondInput)) {
                    return;
                }

                try {
                    int no1 = Integer.parseInt(firstInput);
                    int no2 = Integer.parseInt(secondInput);

                    int productResult = no1 * no2;

                    result.setText(Integer.toString(productResult));

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers.");
                }
            }
        });


        difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                String firstInput = num1.getText().toString().trim();
                String secondInput = num2.getText().toString().trim();

                result.setText("");

                if (!inputValidation(firstInput, secondInput)) {
                    return;
                }

                try {
                    int no1 = Integer.parseInt(firstInput);
                    int no2 = Integer.parseInt(secondInput);

                    int differenceResult = no1 - no2;

                    result.setText(Integer.toString(differenceResult));

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers.");
                }
            }
        });


        quotient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                String firstInput = num1.getText().toString().trim();
                String secondInput = num2.getText().toString().trim();

                result.setText("");

                if (!inputValidation(firstInput, secondInput)) {
                    return;
                }

                try {
                    int no1 = Integer.parseInt(firstInput);
                    int no2 = Integer.parseInt(secondInput);

                    if (no2 == 0) {
                        showAlert("Math Error", "Cannot divide by zero.");
                        return;
                    }

                    double quotientResult = (double) no1 / no2;

                    result.setText(Double.toString(quotientResult));

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers.");
                }
            }
        });



        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                num1.setText("");
                num2.setText("");
                result.setText("");

                num1.requestFocus();
            }
        });


        converter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View y) {

                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }


    public boolean inputValidation(String firstInput, String secondInput) {

        if (firstInput.isEmpty() && secondInput.isEmpty()) {
            showAlert(
                    "Validation Error",
                    "Please enter the first and second numbers."
            );
            return false;
        }

        if (firstInput.isEmpty()) {
            showAlert(
                    "Validation Error",
                    "Please enter the first number."
            );
            return false;
        }

        if (secondInput.isEmpty()) {
            showAlert(
                    "Validation Error",
                    "Please enter the second number."
            );
            return false;
        }

        return true;
    }


    public void showAlert(String title, String message) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}