package com.is101.moneyger.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.is101.moneyger.Activities.adapter.SGAdapter;
import com.is101.moneyger.R;
import com.is101.moneyger.Activities.model.SavingModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class SavingFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView savingsRecyclerView;
    private List<SavingModel> savingsList;
    private SGAdapter adapter;
    private TextView totalSavingsTextView;
    private TextView currentAmountTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container);

        dbHelper = DatabaseHelper.getInstance(requireActivity());
        savingsList = new ArrayList<>();

        totalSavingsTextView = view.findViewById(R.id.textView_TotalAmount);
        currentAmountTextView = view.findViewById(R.id.textView_CurrentAmount);
        TextView plusSignTextView = view.findViewById(R.id.plus_sign);
        TextView txtViewMonthly = view.findViewById(R.id.txtViewMonthly);
        savingsRecyclerView = view.findViewById(R.id.rv_savings);

        adapter = new SGAdapter(savingsList);
        savingsRecyclerView.setAdapter(adapter);
        savingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        int currentUserId = getCurrentUserId();
        updateSavingsData(currentUserId);

        plusSignTextView.setOnClickListener(v -> showAddSavingsPopup(currentUserId));
        txtViewMonthly.setOnClickListener(v -> showMonthlyGoalPopup());

        return view;
    }

    private void showAddSavingsPopup(int userId) {
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.savings_add_popup_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, true);

        // Set up the close button
        popupView.findViewById(R.id.x_button).setOnClickListener(v -> popupWindow.dismiss());

        // Set up the save button
        Button saveButton = popupView.findViewById(R.id.btn_Save);
        saveButton.setOnClickListener(v -> {
            if (addNewSaving(popupView, userId)) {
                popupWindow.dismiss();
            }
        });

        // Show the popup
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    private void showMonthlyGoalPopup() {
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.savings_month_goal_window, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        Button confirmButton = popupView.findViewById(R.id.set_button);
        confirmButton.setOnClickListener(v -> {
            if (addMonthlyGoal(popupView)) {
                popupWindow.dismiss();
            }
        });

        popupView.findViewById(R.id.x_button).setOnClickListener(v -> popupWindow.dismiss());
        popupView.findViewById(R.id.x_button_sign).setOnClickListener(v -> popupWindow.dismiss());
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    private boolean addNewSaving(View popupView, int userId) {
        EditText nameEditText = popupView.findViewById(R.id.editText_NewGoal);
        EditText amountEditText = popupView.findViewById(R.id.editText_GoalAmount);
        EditText startDateEditText = popupView.findViewById(R.id.editText_StartDate);
        EditText endDateEditText = popupView.findViewById(R.id.editText_EndDate);

        String goalName = nameEditText.getText().toString().trim();
        String goalAmountStr = amountEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        if (goalName.isEmpty() || goalAmountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        double goalAmount;
        try {
            goalAmount = Double.parseDouble(goalAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Create a new SavingModel object
        SavingModel saving = new SavingModel(0, goalName, goalAmount, startDate, endDate, userId);

        // Insert into database
        dbHelper.insertSaving(saving);

        // Refresh the list
        updateSavingsData(userId);

        Toast.makeText(getActivity(), "Saving added successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean addMonthlyGoal(View popupView) {
        EditText goalAmountEditText = popupView.findViewById(R.id.editText_NewGoal);
        String goalAmountStr = goalAmountEditText.getText().toString().trim();

        if (goalAmountStr.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a goal amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        double goalAmount;
        try {
            goalAmount = Double.parseDouble(goalAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Save the monthly goal to the database
        dbHelper.setMonthlyGoal(goalAmount); // No need to pass user ID

        Toast.makeText(getActivity(), "Monthly goal set: " + formatAmount(goalAmount), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void updateSavingsData(int userId) {
        totalSavingsTextView.setText(formatAmount(dbHelper.getTotalSavings(userId)));
        currentAmountTextView.setText(formatAmount(dbHelper.getCurrentAmount(userId)));
        savingsList.clear();
        savingsList.addAll(dbHelper.getSavingsByUserId(userId));
        adapter.notifyDataSetChanged();
    }

    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("currentUserId", -1);
    }

    private String formatAmount(double amount) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        return "₱" + numberFormat.format(amount);
    }
}