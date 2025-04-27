package com.example.uts_project.income;

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

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<Income> incomeList;
    private Context context;

    public IncomeAdapter(Context context, List<Income> incomeList) {
        this.context = context;
        this.incomeList = incomeList;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.bind(income);

        holder.options.setOnClickListener(v -> showPopupMenu(v, position));
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.morevert_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                deleteIncome(position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void deleteIncome(int position) {
        Income income = incomeList.get(position);

        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus \"" + income.getName() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && income.getId() != null) {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(user.getUid())
                                .child("income")
                                .child(income.getId())
                                .removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Berhasil dihapus", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Gagal hapus: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .setNegativeButton("Batal", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        private TextView textIncomeName, textIncomeAmount, textIncomeDescription, textIncomeDate;
        private ImageView options;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            textIncomeName = itemView.findViewById(R.id.textIncomeName);
            textIncomeAmount = itemView.findViewById(R.id.textIncomeAmount);
            textIncomeDescription = itemView.findViewById(R.id.textIncomeDescription);
            textIncomeDate = itemView.findViewById(R.id.textIncomeDate);
            options = itemView.findViewById(R.id.options);
        }

        public void bind(Income income) {
            textIncomeName.setText(income.getName());
            textIncomeAmount.setText(formatCurrency(income.getAmount()));
            textIncomeDescription.setText(income.getDescription());
            textIncomeDate.setText(income.getDate());
        }

        private String formatCurrency(double amount) {
            Locale localeID = new Locale("in", "ID");
            NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
            return format.format(amount);
        }
    }
}