package com.is101.moneyger.Activities;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.is101.moneyger.R;

public class SavingsClickHandler implements View.OnClickListener {
    private Context context;

    public SavingsClickHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        // Use if-else instead of switch for View IDs
        if (v.getId() == R.id.set_button) {
            // Handle the set button click
            EditText edittxtAmount = ((MainActivity) context).findViewById(R.id.editText_MonthlyGoal);
            String amount = edittxtAmount.getText().toString();
            if (!amount.isEmpty()) {
                // Process the amount (e.g., save it, show a message, etc.)
                Toast.makeText(context, "Amount set: " + amount, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please enter an amount", Toast.LENGTH_SHORT).show();
            }
        }
    }
} //push comment