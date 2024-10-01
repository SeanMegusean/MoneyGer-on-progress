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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.landing_page);

        // Set up padding for the layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the StartBtn and set a click listener
        findViewById(R.id.StartBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Registration activity
                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);
            }
        });
    }
}
