package com.is101.moneyger.Activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
        //taga lagay ng comma
        NumberFormat numberFormat = NumberFormat.getInstance();
        String formattedAmount = numberFormat.format(amount);

        return "₱" + formattedAmount;
    }

}
