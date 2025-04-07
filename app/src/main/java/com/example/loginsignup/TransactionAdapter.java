package com.example.loginsignup;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<TransactionModel> transactionList;

    public TransactionAdapter(List<TransactionModel> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionModel transaction = transactionList.get(position);
        holder.transactionTitle.setText(transaction.getTitle());
        holder.transactionDate.setText(transaction.getDate());
        holder.transactionAmount.setText(transaction.getAmount());
        holder.transactionIcon.setImageResource(transaction.getIcon());

        // Click listener to open TransactionDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TransactionDetailsActivity.class);
            intent.putExtra("title", transaction.getTitle());
            intent.putExtra("date", transaction.getDate());
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("icon", transaction.getIcon());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView transactionTitle, transactionDate, transactionAmount;
        ImageView transactionIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionTitle = itemView.findViewById(R.id.transactionTitle);
            transactionDate = itemView.findViewById(R.id.transactionDate);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            transactionIcon = itemView.findViewById(R.id.transactionIcon);
        }
    }
}
