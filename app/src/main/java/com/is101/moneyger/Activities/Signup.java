package com.is101.moneyger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.is101.moneyger.R;

public class Signup extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        TextView cBHome = findViewById(R.id.cBhome);
        EditText usernameEditText = findViewById(R.id.Username);
        EditText pinEditText = findViewById(R.id.pin);
        EditText verifyPinEditText = findViewById(R.id.VFPin);

        // Set maximum length for PIN fields
        int pinMaxLength = 10;
        pinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(pinMaxLength)});
        verifyPinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(pinMaxLength)});

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Handle "Home" button click
        cBHome.setOnClickListener(v -> navigateToHome());

        // Handle sign-up button click
        findViewById(R.id.SgnupBtn).setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String pin = pinEditText.getText().toString().trim();
            String verifyPin = verifyPinEditText.getText().toString().trim();

            handleSignup(username, pin, verifyPin);
        });
    }

    // Navigate back to the registration screen
    private void navigateToHome() {
        Intent intent = new Intent(Signup.this, Registration.class);
        startActivity(intent);
        finish(); // Ensure this activity is closed
    }

    // Handle sign-up logic
    private void handleSignup(String username, String pin, String verifyPin) {
        if (dbHelper == null) {
            Toast.makeText(this, "Database is unavailable. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input fields
        if (username.isEmpty() || pin.isEmpty() || verifyPin.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pin.length() < 4) {
            Toast.makeText(this, "PIN must be at least 4 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pin.equals(verifyPin)) {
            Toast.makeText(this, "Pins do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists
        if (dbHelper.isUserExists(username, pin)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt to register the user
        boolean isInserted = dbHelper.insertUser(username, pin);
        if (isInserted) {
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
            navigateToHome(); // Navigate to home screen
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Close the database to avoid memory leaks
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
