package com.is101.moneyger.Activities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.is101.moneyger.Activities.Wallet;
import com.is101.moneyger.Activities.model.RecyclerModel;
import com.is101.moneyger.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    Context context;
    List<RecyclerModel> recyclerModels;

    public RecyclerAdapter(Context context, List<RecyclerModel> recyclerModels){
        this.context = context;
        this.recyclerModels = recyclerModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.title.setText(recyclerModels.get(position).getTitle());
        //holder.date.setText(recyclerModels.get(position).getDate());
        //holder.amount.setText(String.valueOf(recyclerModels.get(position).getAmount()));

        RecyclerModel model = recyclerModels.get(position);
        holder.title.setText(model.getTitle());
        holder.date.setText(model.getDate());
        String formattedAmount;
        if (model.getAmount() < 0) {
            formattedAmount = "-" + String.format(context.getString(R.string.amount_format), Math.abs(model.getAmount()));
        } else {
            formattedAmount = "+" + String.format(context.getString(R.string.amount_format), model.getAmount());
        }

        holder.amount.setText(formattedAmount);
    }

    @Override
    public int getItemCount() {
        return recyclerModels.size();//kung ilan yung ididisplay
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,date,amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);

        }
    }
}
