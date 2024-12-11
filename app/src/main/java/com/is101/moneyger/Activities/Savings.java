package com.is101.moneyger.Activities;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.is101.moneyger.Activities.adapter.SGAdapter;
import com.is101.moneyger.Activities.model.SavingModel;
import com.is101.moneyger.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Savings extends Fragment {

    private DatabaseHelper dbHelper;
    private RecyclerView savingsRecyclerView;
    private List<SavingModel> savingsList;
    private SGAdapter adapter;
    private TextView totalSavingsTextView;
    private TextView currentAmountTextView;

    public Savings() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);
        initializeViews(view);
        setupRecyclerView();
        loadSavingsData();
        setupClickListeners(view);
        return view;
    }

    private void initializeViews(View view) {
        dbHelper = DatabaseHelper.getInstance(requireActivity());
        savingsList = new ArrayList<>();
        totalSavingsTextView = view.findViewById(R.id.textView_TotalAmount);
        currentAmountTextView = view.findViewById(R.id.txtViewMonthlyGoal);
        savingsRecyclerView = view.findViewById(R.id.rv_savings);
    }

    private void setupRecyclerView() {
        adapter = new SGAdapter(savingsList, new SGAdapter.OnItemClickListener() {
            @Override
            public void onEdit(SavingModel saving) {
                // Call the editSaving method when an item is clicked
                editSaving(saving);
            }

            @Override
            public void onDelete(SavingModel saving) {
                // Call the deleteSaving method when an item is long-clicked
                deleteSaving(saving);
            }
        });
        savingsRecyclerView.setAdapter(adapter);
        savingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void loadSavingsData() {
        int currentUserId = getCurrentUserId();
        double totalSavings = dbHelper.getTotalSavings(currentUserId);
        double currentAmount = dbHelper.getCurrentAmount(currentUserId);
        totalSavingsTextView.setText(formatAmount(totalSavings));
        currentAmountTextView.setText(formatAmount(currentAmount));
        updateSavingsList(currentUserId);
    }

    private void updateSavingsList(int currentUserId) {
        savingsList.clear();
        savingsList.addAll(dbHelper.getSavingsByUserId(currentUserId));
        adapter.notifyDataSetChanged();
    }

    private void setupClickListeners(View view) {
        view.findViewById(R.id.plus_sign).setOnClickListener(v -> showAddSavingsPopup());
        view.findViewById(R.id.txtViewMonthly).setOnClickListener(v -> showMonthlyGoalPopup());

        TextView currentAmountTextView = view.findViewById(R.id.txtViewMonthlyGoal);
        TextView lblCurrentAmountTextView = view.findViewById(R.id.textView_lblCurrentAmount);

        currentAmountTextView.setOnClickListener(v -> showMonthlyGoalPopup());
        lblCurrentAmountTextView.setOnClickListener(v -> showMonthlyGoalPopup());
    }

    private void showAddSavingsPopup() {
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.savings_add_popup_window, null);
        PopupWindow popupWindow = createPopupWindow(popupView);
        setupAddSavingsPopup(popupView, popupWindow);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    private void setupAddSavingsPopup(View popupView, PopupWindow popupWindow) {
        popupView.findViewById(R.id.x_button).setOnClickListener(v -> {
            popupWindow.dismiss();
            updateSavingsList(getCurrentUserId());
        });
        popupView.findViewById(R.id.btn_Save).setOnClickListener(v -> {
            if (addNewSaving(popupView)) {
                popupWindow.dismiss();
                updateSavingsList(getCurrentUserId());
            }
        });
    }

    private void showMonthlyGoalPopup() {
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.savings_month_goal_window, null);
        PopupWindow popupWindow = createPopupWindow(popupView);
        EditText goalAmountEditText = popupView.findViewById(R.id.editText_MonthlyGoal);
        TextView currentGoalTextView = popupView.findViewById(R.id.textView_MonthlyGoal);

        // Load and display the current goal
        double currentGoal = dbHelper.getMonthlyGoal(getCurrentUserId());
        currentGoalTextView.setText(currentGoal > 0 ?
                "Current Goal: ₱" + formatAmount(currentGoal) : "No Goal Set");

        popupView.findViewById(R.id.set_button).setOnClickListener(v -> {
            String goalAmountStr = goalAmountEditText.getText().toString().trim();
            if (goalAmountStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter a goal amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double newGoalAmount;
            try {
                newGoalAmount = Double.parseDouble(goalAmountStr);
                dbHelper.updateMonthlyGoal(getCurrentUserId(), newGoalAmount);
                Toast.makeText(getActivity(), "Monthly goal updated: ₱" + formatAmount(newGoalAmount), Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                updateMonthlyGoalDisplay();
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        popupView.findViewById(R.id.x_button_sign).setOnClickListener(v -> popupWindow.dismiss());
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    private void updateMonthlyGoalDisplay() {
        double currentGoal = dbHelper.getMonthlyGoal(getCurrentUserId());
        if (currentGoal > 0) {
            currentAmountTextView.setText("Current Goal: ₱" + formatAmount(currentGoal));
        } else {
            currentAmountTextView.setText("No Goal Set");
        }
    }

    private void setupMonthlyGoalPopup(View popupView, PopupWindow popupWindow) {
        int userId = getCurrentUserId(); // Get the current user's ID

        popupView.findViewById(R.id.set_button).setOnClickListener(v -> {
            if (addMonthlyGoal(popupView, userId)) { // Pass userId as an argument
                popupWindow.dismiss();
                updateSavingsList(userId);
            }
        });

        View.OnClickListener dismissListener = v -> {
            popupWindow.dismiss();
            updateSavingsList(userId);
        };

        popupView.findViewById(R.id.x_button).setOnClickListener(dismissListener);
        popupView.findViewById(R.id.x_button_sign).setOnClickListener(dismissListener);
    }

    private PopupWindow createPopupWindow(View popupView) {
        return new PopupWindow(popupView, FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, true);
    }

    private boolean addNewSaving(View popupView) {
        EditText nameEditText = popupView.findViewById(R.id.editText_MonthlyGoal);
        EditText amountEditText = popupView.findViewById(R.id.editText_GoalAmount);
        EditText startDateEditText = popupView.findViewById(R.id.editText_StartDate);
        EditText endDateEditText = popupView.findViewById(R.id.editText_EndDate);

        String goalName = nameEditText.getText().toString().trim();
        String goalAmountStr = amountEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        if (!validateInputs(goalName, goalAmountStr, startDate, endDate)) {
            return false;
        }

        double goalAmount = Double.parseDouble(goalAmountStr);
        SavingModel saving = new SavingModel(0, goalName, goalAmount, startDate, endDate, getCurrentUserId());

        dbHelper.insertSaving(saving);
        updateSavingsList(getCurrentUserId());

        savingsRecyclerView.scrollToPosition(savingsList.size() - 1);

        Toast.makeText(getActivity(), "Saving added successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean validateInputs(String goalName, String goalAmountStr, String startDate, String endDate) {
        if (goalName.isEmpty() || goalAmountStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Double.parseDouble(goalAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean addMonthlyGoal(View popupView, int userId) {
        EditText goalAmountEditText = popupView.findViewById(R.id.editText_MonthlyGoal);
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

        // Call the insertMonthlyGoal method with the goalAmount and userId
        boolean isInserted = dbHelper.insertMonthlyGoal(goalAmount, userId);
        if (!isInserted) {
            Toast.makeText(getActivity(), "Failed to set monthly goal", Toast.LENGTH_SHORT).show();
            return false;
        }

        Toast.makeText(getActivity(), "Monthly goal set: " + formatAmount(goalAmount), Toast.LENGTH_SHORT).show();
        return true;
    }

    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("currentUserId", -1);
    }

    private String formatAmount(double amount) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        return "₱" + numberFormat.format(amount);
    }

    private void updateMonthlyGoal(int goalAmount) {
        // Implementation to update the monthly goal in the database
    }

    private void deleteSaving(SavingModel saving) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Saving")
                .setMessage("Are you sure you want to delete this saving?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
                    if (dbHelper.deleteSaving(saving.getId())) {
                        savingsList.remove(saving); // Remove from the list
                        adapter.notifyDataSetChanged(); // Notify adapter to refresh the view
                        Toast.makeText(getActivity(), "Saving deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete saving", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void editSaving(SavingModel saving) {
        // Inflate the popup view
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.savings_edit_popup_window, null);
        PopupWindow popupWindow = createPopupWindow(popupView);

        // Initialize EditText fields
        EditText nameEditText = popupView.findViewById(R.id.etSavingName);
        EditText amountEditText = popupView.findViewById(R.id.etSavingAmount);
        EditText startDateEditText = popupView.findViewById(R.id.etSavingStartDate);
        EditText endDateEditText = popupView.findViewById(R.id.etSavingEndDate);

        // Set current values in the EditText fields
        nameEditText.setText(saving.getName());
        amountEditText.setText(String.valueOf(saving.getAmount()));
        startDateEditText.setText(saving.getStartDate());
        endDateEditText.setText(saving.getEndDate());

        // Save button click listener
        popupView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            String newAmountStr = amountEditText.getText().toString().trim();
            String newStartDate = startDateEditText.getText().toString().trim();
            String newEndDate = endDateEditText.getText().toString().trim();

            // Validate input
            if (newName.isEmpty() || newAmountStr.isEmpty() || newStartDate.isEmpty() || newEndDate.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double newAmount = Double.parseDouble(newAmountStr);

                // Update the saving object
                saving.setName(newName);
                saving.setAmount(newAmount);
                saving.setStartDate(newStartDate);
                saving.setEndDate(newEndDate);

                // Update in the database
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(getActivity());
                if (dbHelper.updateSaving(saving)) {
                    Toast.makeText(getActivity(), "Saving updated successfully", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss(); // Close the popup
                } else {
                    Toast.makeText(getActivity(), "Failed to update saving", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        // Close button listener
        popupView.findViewById(R.id.x_button).setOnClickListener(v -> popupWindow.dismiss());

        // Show the popup window
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
    }

    private boolean updateSaving(int savingId, EditText nameEditText, EditText amountEditText, EditText startDateEditText, EditText endDateEditText) {
        String goalName = nameEditText.getText().toString().trim();
        String goalAmountStr = amountEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        if (!validateInputs(goalName, goalAmountStr, startDate, endDate)) {
            return false;
        }

        double goalAmount = Double.parseDouble(goalAmountStr);
        SavingModel saving = new SavingModel(savingId, goalName, goalAmount, startDate, endDate, getCurrentUserId());

        dbHelper.updateSaving(saving); // Implement this method in DatabaseHelper
        updateSavingsList(getCurrentUserId());
        Toast.makeText(getActivity(), "Saving updated successfully!", Toast.LENGTH_SHORT).show();
        return true;
    }
}