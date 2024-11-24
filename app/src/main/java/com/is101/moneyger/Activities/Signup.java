package com.is101.moneyger.Activities;

import com.is101.moneyger.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Signup extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        TextView cBHome = findViewById(R.id.cBhome);
        cBHome.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Registration.class);
            startActivity(intent);
        });

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set up sign-up button click listener
        findViewById(R.id.SgnupBtn).setOnClickListener(v -> {
            EditText usernameEditText = findViewById(R.id.Username);
            EditText pinEditText = findViewById(R.id.pin);
            EditText verifyPinEditText = findViewById(R.id.VFPin);

            pinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            verifyPinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            String username = usernameEditText.getText().toString().trim();
            String pin = pinEditText.getText().toString().trim();
            String verifyPin = verifyPinEditText.getText().toString().trim();

            // Validate inputs
            if (isEmpty(username) || isEmpty(pin) || isEmpty(verifyPin)) {
                Toast.makeText(Signup.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pin.equals(verifyPin)) {
                Toast.makeText(Signup.this, "Pins do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.isUserExists(username)) {
                Toast.makeText(Signup.this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user into the database
            boolean isInserted = dbHelper.insertUser(username, pin);
            if (isInserted) {
                Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Signup.this, Registration.class);
                startActivity(intent);
                finish(); // Optional: close Signup activity
            } else {
                Toast.makeText(Signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to check if a string is empty
    private boolean isEmpty(String str) {
        return str.length() == 0;
    }
}