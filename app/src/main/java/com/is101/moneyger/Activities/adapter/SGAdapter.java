package com.is101.moneyger.Activities.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.is101.moneyger.Activities.model.SavingModel;
import com.is101.moneyger.R;
import java.util.List;

public class SGAdapter extends RecyclerView.Adapter<SGAdapter.ViewHolder> {
    private List<SavingModel> savingsList;
    private OnItemClickListener onItemClickListener;

    // Interface for click listener
    public interface OnItemClickListener {
        void onEdit(SavingModel saving);
        void onDelete(SavingModel saving);
    }

    public SGAdapter(List<SavingModel> savingsList, OnItemClickListener onItemClickListener) {
        this.savingsList = savingsList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saving, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingModel saving = savingsList.get(position);
        holder.bind(saving);

        // Set the click listener for editing the item
        holder.itemView.setOnClickListener(v -> onItemClickListener.onEdit(saving));

        // Set the long click listener for deletion
        holder.itemView.setOnLongClickListener(v -> {
            onItemClickListener.onDelete(saving);
            return true; // Return true to indicate the event was handled
        });
    }

    @Override
    public int getItemCount() {
        return savingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView goalNameTextView;
        private TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            goalNameTextView = itemView.findViewById(R.id.textView_GoalName);
            amountTextView = itemView.findViewById(R.id.textView_Amount);
        }

        public void bind(SavingModel saving) {
            goalNameTextView.setText(saving.getName()); // Use getName() to get the name
            amountTextView.setText(String.format("₱%.2f", saving.getAmount())); // Format amount
        }
    }
} //comment