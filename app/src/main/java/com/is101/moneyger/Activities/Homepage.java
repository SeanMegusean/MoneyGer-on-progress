package com.is101.moneyger.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.is101.moneyger.R;
import com.is101.moneyger.databinding.ActivityHomepageBinding;
import androidx.core.view.GravityCompat;

public class Homepage extends AppCompatActivity {

    private ActivityHomepageBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the DrawerLayout and Button
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageView openSidebarButton = findViewById(R.id.open_sidebar_button);

        // Set up the button to open the sidebar
        openSidebarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sidebar (NavigationView) from the right
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // Set the initial fragment
        if (savedInstanceState == null) {
            replaceFragment(new Wallet());
        }

        // Bottom Navigation listener
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.wallet) {
                selectedFragment = new Wallet();
            } else if (item.getItemId() == R.id.expense) {
                selectedFragment = new Expenses();
            } else if (item.getItemId() == R.id.savings) {
                selectedFragment = new Savings();
            }

            return replaceFragment(selectedFragment);
        });

        // Set up the NavigationView listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.main) {
                // Handle navigation to the Home section
            } else if (item.getItemId() == R.id.dashboard) {
                // Handle navigation to the Settings section
            }
            // Add other conditions as needed

            // Close the drawer after navigation
            drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        });
    }

    private boolean replaceFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }
}
