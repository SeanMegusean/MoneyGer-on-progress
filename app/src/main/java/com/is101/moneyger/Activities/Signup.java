package com.is101.moneyger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.is101.moneyger.R;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Apply insets for UI layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Set up sign-up button click listener
        findViewById(R.id.SgnupBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String username = ((EditText) findViewById(R.id.Username)).getText().toString().trim();
                String pin = ((EditText) findViewById(R.id.pin)).getText().toString().trim();
                String verifyPin = ((EditText) findViewById(R.id.VFPin)).getText().toString().trim();

                // Validate inputs
                if (username.isEmpty() || pin.isEmpty() || verifyPin.isEmpty()) {
                    Toast.makeText(Signup.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if pins match
                if (!pin.equals(verifyPin)) {
                    Toast.makeText(Signup.this, "Pins do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Insert user into the database
                boolean isInserted = dbHelper.insertUser(username, pin);
                if (isInserted) {
                    Toast.makeText(Signup.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    // Navigate to Registration activity
                    Intent intent = new Intent(Signup.this, Registration.class);
                    startActivity(intent);
                    finish(); // Optional: close Signup activity
                } else {
                    Toast.makeText(Signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
