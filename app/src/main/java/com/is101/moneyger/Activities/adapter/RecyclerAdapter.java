package com.is101.moneyger.Activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
        holder.titleTextView.setText(model.getTitle());
        holder.dateTextView.setText(model.getDate());
        holder.amountTextView.setText(formatAmount(model.getAmount()));

        holder.itemView.setOnClickListener(v -> showItemDialog(model));
    }

    private void showItemDialog(RecyclerModel model) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.item_popup, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Dialog views
        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView descriptionTextView = dialogView.findViewById(R.id.descripPOP);
        TextView dateTextView = dialogView.findViewById(R.id.date_text);
        TextView amountTextView = dialogView.findViewById(R.id.amount_text);

        Button backButton = dialogView.findViewById(R.id.buttonback);
        Button deleteButton = dialogView.findViewById(R.id.buttondelete);

        titleTextView.setText(model.getTitle());
        descriptionTextView.setText(getDescriptionText(model.getDescription()));
        dateTextView.setText(model.getDate());
        amountTextView.setText(formatAmount(model.getAmount()));

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        backButton.setOnClickListener(v -> dialog.dismiss());

        deleteButton.setOnClickListener(v -> {
            deleteItem(model);
            dialog.dismiss();
        });
    }

    private String getDescriptionText(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "Note: No description provided.";
        } else {
            return "Note: " + description;
        }
    }

    private void deleteItem(RecyclerModel model) {
        WalletDB wdb = new WalletDB(context);
        wdb.deleteTransaction(model.getId());

        int position = recyclerModels.indexOf(model);
        recyclerModels.remove(position);
        notifyItemRemoved(position);

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
            titleTextView = itemView.findViewById(R.id.textView_MonthlyGoal);
            dateTextView = itemView.findViewById(R.id.date);
            amountTextView = itemView.findViewById(R.id.amount);
        }
    }

    private String formatAmount(int amount) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        return "₱" + numberFormat.format(amount);
    }
} //push comment
