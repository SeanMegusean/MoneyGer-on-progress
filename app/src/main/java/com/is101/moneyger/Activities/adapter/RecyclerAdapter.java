package com.is101.moneyger.Activities.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.is101.moneyger.Activities.DatabaseFunctions.WalletDB;
import com.is101.moneyger.Activities.model.RecyclerModel;
import com.is101.moneyger.R;

import java.text.NumberFormat;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private List<RecyclerModel> recyclerModels;

    public RecyclerAdapter(Context context, List<RecyclerModel> recyclerModels) {
        this.context = context;
        this.recyclerModels = recyclerModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerModel model = recyclerModels.get(position);
        String formattedDate = model.getDate();
        String formattedAmount = formatAmount(model.getAmount());

        holder.titleTextView.setText(model.getTitle());
        holder.dateTextView.setText(formattedDate);
        holder.amountTextView.setText(formattedAmount);

        holder.itemView.setOnClickListener(v -> {
            showItemDialog(model);
        });
    }

    private void showItemDialog(RecyclerModel model) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.item_popup, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView descriptionTextView = dialogView.findViewById(R.id.descripPOP);
        Button backButton = dialogView.findViewById(R.id.buttonback);
        Button deleteButton = dialogView.findViewById(R.id.buttondelete);

        // Set the title and description from the model
        titleTextView.setText(model.getTitle());
        descriptionTextView.setText("Date: " + model.getDate() + "\nAmount: " + formatAmount(model.getAmount()) + "\nNote: " + model.getDescription());
        descriptionTextView.setGravity(Gravity.START | Gravity.TOP);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        backButton.setOnClickListener(v -> dialog.dismiss());

        deleteButton.setOnClickListener(v -> {

            deleteItem(model);
            dialog.dismiss();
        });
    }

    private void deleteItem(RecyclerModel model) {
        //deleting items using ID \\ metod in walletdb
        int itemId = model.getId();
        WalletDB wdb = new WalletDB(context);
        wdb.deleteTransaction(itemId);

        recyclerModels.remove(model);
        notifyDataSetChanged();

        Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return recyclerModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, dateTextView, amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            dateTextView = itemView.findViewById(R.id.date);
            amountTextView = itemView.findViewById(R.id.amount);
        }
    }

    private String formatAmount(int amount) {
        // Format amount with commas and currency symbol
        NumberFormat numberFormat = NumberFormat.getInstance();
        String formattedAmount = numberFormat.format(amount);

        return "₱" + formattedAmount;
    }
}
