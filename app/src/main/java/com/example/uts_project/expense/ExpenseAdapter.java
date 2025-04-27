package com.example.uts_project.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList;
    private Context context;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
        holder.options.setOnClickListener(v -> showPopupMenu(v, position));
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.morevert_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                deleteExpense(position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void deleteExpense(int position) {
        Expense expense = expenseList.get(position);
        showDeleteConfirmationDialog(expense, position);
    }

    private void showDeleteConfirmationDialog(Expense expense, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus \"" + expense.getName() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && expense.getId() != null) {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(user.getUid())
                                .child("expense")
                                .child(expense.getId())
                                .removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Pengeluaran berhasil dihapus", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView textExpenseName, textExpenseAmount, textExpenseDescription, textExpenseDate;
        private ImageView options;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textExpenseName = itemView.findViewById(R.id.textExpenseName);
            textExpenseAmount = itemView.findViewById(R.id.textExpenseAmount);
            textExpenseDescription = itemView.findViewById(R.id.textExpenseDescription);
            textExpenseDate = itemView.findViewById(R.id.textExpenseDate);
            options = itemView.findViewById(R.id.options);
        }

        public void bind(Expense expense) {
            textExpenseName.setText(expense.getName());
            textExpenseAmount.setText(formatCurrency(expense.getAmount()));
            textExpenseDescription.setText(expense.getDescription());
            textExpenseDate.setText(expense.getDate());
        }

        private String formatCurrency(double amount) {
            Locale localeID = new Locale("in", "ID");
            NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
            return format.format(amount);
        }
    }
}