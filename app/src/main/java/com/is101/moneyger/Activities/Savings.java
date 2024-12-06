package com.is101.moneyger.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.is101.moneyger.Activities.model.SavingModel;
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
    private TextView totalSavingsTextView;

    public Savings() {
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

        int userId = getCurrentUserId();
        loadSavingsData(userId);
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.rv_savings);
        savingsAddButton = view.findViewById(R.id.btnAddTransaction);
        totalSavingsTextView = view.findViewById(R.id.textView_TotalAmount);
    }

    private void setupRecyclerView() {
        recyclerAdapter = new RecyclerAdapter(getContext(), recyclerModels);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadSavingsData(int userId) {
        recyclerModels.clear();
        double totalSavings = dbHelper.getTotalSavings(userId);
        recyclerModels.add(new RecyclerModel("Total Savings", totalSavings));
        recyclerAdapter.notifyDataSetChanged();
        updateTotalSavings(userId);
    }

    private void updateTotalSavings(int userId) {
        double totalSavings = dbHelper.getTotalSavings(userId);
        totalSavingsTextView.setText(String.valueOf(totalSavings));
    }

    private void setupListeners() {
        savingsAddButton.setOnClickListener(v -> addSavings());
    }

    private void addSavings() {
        double amountToAdd = 10.0;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        int userId = getCurrentUserId();

        SavingModel saving = new SavingModel(0, "Default Savings", amountToAdd, currentDate, null, userId);
        boolean success = dbHelper.insertSaving(saving);

        if (success) {
            loadSavingsData(userId);
            Toast.makeText(getContext(), "Savings added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error adding savings", Toast.LENGTH_SHORT).show();
        }
    }

    private int getCurrentUserId() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("currentUserId", -1);
    }
}