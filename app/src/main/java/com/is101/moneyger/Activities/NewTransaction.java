package com.is101.moneyger.Activities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private String formattedDate = "";
    private TextView descripttxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_transaction, container, false);

        btndate = view.findViewById(R.id.buttondate);
        editTextText = view.findViewById(R.id.editTextText);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        addexpensebtn = view.findViewById(R.id.addexpensebtn);
        descripttxt = view.findViewById(R.id.descripttxt);

        // Spinner
        String[] categories = getResources().getStringArray(R.array.categories);
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        setDate();

        addexpensebtn.setOnClickListener(v -> {

            if (formattedDate.isEmpty() || editTextText.getText().toString().trim().isEmpty() ||
                    spinnerCategory.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(editTextText.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Enter a valid number for the amount", Toast.LENGTH_SHORT).show();
                return;
            }

            String category = spinnerCategory.getSelectedItem().toString().trim();
            String description = descripttxt.getText().toString().trim();

            WalletDB wdb = new WalletDB(getActivity());
            wdb.addExpense(category, formattedDate, amount, description);

            editTextText.setText("");
            descripttxt.setText("");
            spinnerCategory.setSelection(0);

            Toast.makeText(getActivity(), "Expense added successfully", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);
        formattedDate = sdf.format(selectedDate.getTime());

        btndate.setText(formattedDate);
    }
}

//this comment is used for me to push this version. Kindly dismiss this