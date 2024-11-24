package com.is101.moneyger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.is101.moneyger.R;

public class Registration extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        dbHelper = new DatabaseHelper(this);

        TextView signUpTextView = findViewById(R.id.textView5);
        signUpTextView.setOnClickListener(view -> {
            Intent intent = new Intent(Registration.this, Signup.class);
            startActivity(intent);
        });

        findViewById(R.id.Login).setOnClickListener(view -> {
            String username = ((EditText) findViewById(R.id.Username)).getText().toString().trim();
            String pin = ((EditText) findViewById(R.id.Pin)).getText().toString().trim();

            if (username.isEmpty() || pin.isEmpty()) {
                Toast.makeText(Registration.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Registration", "Attempting login with username: " + username + " and pin: " + pin);

            // Check if user exists
            if (dbHelper.checkUser(username, pin)) {
                Toast.makeText(Registration.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Registration.this, Homepage.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Registration.this, "Invalid username or pin", Toast.LENGTH_SHORT).show();
            }
        });
    }
}