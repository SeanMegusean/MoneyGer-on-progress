package com.is101.moneyger.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.is101.moneyger.R;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        // Apply insets for UI layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up logout button click listener
        findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform logout actions
                Intent intent = new Intent(Homepage.this, Registration.class);
                startActivity(intent);
                finish(); // Close Homepage activity
            }
        });
    }
}
