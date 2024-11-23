package com.is101.moneyger.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.is101.moneyger.R;
import com.is101.moneyger.Activities.adapter.RecyclerAdapter;
import com.is101.moneyger.Activities.model.RecyclerModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Savings extends Fragment {

    private RecyclerView recyclerView;
    private List<RecyclerModel> recyclerModels = new ArrayList<>();
    private RecyclerAdapter recyclerAdapter;
    private DatabaseHelper dbHelper;

    private ImageButton savingsAddButton;
    private ProgressBar savingsProgressBar;
    private TextView totalSavingsTextView; // To display total savings
    private double totalSavings; // To hold the total savings value

    public Savings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);
        initializeViews(view);
        setupRecyclerView();
        loadSavingsData();
        setupListeners();
        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rv_savings);
        savingsAddButton = view.findViewById(R.id.savings_addbtn);
        savingsProgressBar = view.findViewById(R.id.savings_PB);
        totalSavingsTextView = view.findViewById(R.id.textView15); // Assuming this displays the total savings
    }

    private void setupRecyclerView() {
        recyclerAdapter = new RecyclerAdapter(getContext(), recyclerModels);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadSavingsData() {
        recyclerModels.clear();
        recyclerModels.addAll(dbHelper.getAllSavings());
        recyclerAdapter.notifyDataSetChanged();
        updateTotalSavings();
    }

    private void updateTotalSavings() {
        totalSavings = dbHelper.getTotalSavings(); // Get total savings from the database
        totalSavingsTextView.setText(String.valueOf(totalSavings));
        savingsProgressBar.setProgress((int) totalSavings); // Update progress bar based on total savings
    }

    private void setupListeners() {
        savingsAddButton.setOnClickListener(v -> addSavings());
    }

    private void addSavings() {
        double amountToAdd = 10.0; // Example default amount
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()); // Current date
        boolean success = dbHelper.insertSavings("Default Savings", amountToAdd, currentDate); // Example name
        if (success) {
            loadSavingsData();
            Toast.makeText(getContext(), "Savings added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error adding savings", Toast.LENGTH_SHORT).show();
        }
    }
}