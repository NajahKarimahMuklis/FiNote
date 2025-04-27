package com.example.uts_project.transaction;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_project.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class transactionAdapter extends RecyclerView.Adapter<transactionAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private Context context;

    public transactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        // Format amount with currency
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String amountFormatted = currencyFormat.format(Math.abs(transaction.getAmount())).replace("Rp", "Rp");

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        String dateFormatted = dateFormat.format(new Date(transaction.getTimestamp()));

        // Set values to views
        holder.amountText.setText(amountFormatted);
        holder.amountText.setTextColor(transaction.getAmount() >= 0 ?
                ContextCompat.getColor(context, R.color.green) :
                ContextCompat.getColor(context, R.color.red));

        holder.typeText.setText(transaction.getAmount() >= 0 ? "Tabung" : "Tarik");
        holder.dateText.setText(dateFormatted);

        if (!TextUtils.isEmpty(transaction.getNote())) {
            holder.noteText.setText(transaction.getNote());
            holder.noteText.setVisibility(View.VISIBLE);
        } else {
            holder.noteText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(0, transaction); // Add to the beginning of the list
        notifyItemInserted(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText;
        TextView typeText;
        TextView dateText;
        TextView noteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.transactionAmount);
            typeText = itemView.findViewById(R.id.transactionType);
            dateText = itemView.findViewById(R.id.transactionDate);
            noteText = itemView.findViewById(R.id.transactionNote);
        }
    }
}