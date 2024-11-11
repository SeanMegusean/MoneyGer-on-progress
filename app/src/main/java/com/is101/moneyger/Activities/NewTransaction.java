package com.is101.moneyger.Activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.is101.moneyger.Activities.DatabaseFunctions.WalletDB;
import com.is101.moneyger.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTransaction extends Fragment {

    private Spinner spinnerCategory;
    private TextView editTextText;
    private Button btndate, addexpensebtn;
    private String formattedDate = "";  // Keep formattedDate as a class-level variable

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        btndate = view.findViewById(R.id.buttondate);
        editTextText = view.findViewById(R.id.editTextText);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        addexpensebtn = view.findViewById(R.id.addexpensebtn);

        //Spinner
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Date picker
        btndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                        formattedDate = sdf.format(selectedDate.getTime());

                        btndate.setText(formattedDate);
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        addexpensebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formattedDate.isEmpty()) {
                    Toast.makeText(getActivity(), "Please select a date", Toast.LENGTH_SHORT).show();
                    return;
                }

                String category = spinnerCategory.getSelectedItem().toString().trim();
                int amount = Integer.valueOf(editTextText.getText().toString().trim());

                WalletDB wdb = new WalletDB(getActivity());
                wdb.addExpense(category, formattedDate, amount);
            }
        });

        return view;
    }
}
