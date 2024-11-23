package com.is101.moneyger.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.is101.moneyger.R;
import com.is101.moneyger.databinding.ActivityHomepageBinding;

public class Homepage extends AppCompatActivity {

    private ActivityHomepageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the initial fragment
        if (savedInstanceState == null) {
            replaceFragment(new Wallet());
        }

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
