package com.example.uts_project.all;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.DecimalFormat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uts_project.R;
import java.util.List;

public class financeRecordAdapter extends RecyclerView.Adapter<financeRecordAdapter.ViewHolder> {

    private List<financeRecord> financeRecordList;

    public financeRecordAdapter(List<financeRecord> financeRecordList) {
        this.financeRecordList = financeRecordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finance_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        financeRecord record = financeRecordList.get(position);
        holder.tvDescription.setText(record.getDescription());

        // Format jumlah uang dengan pemisah ribuan menggunakan titik
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(record.getAmount());

        // Menampilkan jumlah dengan tanda + untuk pemasukan dan - untuk pengeluaran
        holder.tvAmount.setText((record.getType().equals("pemasukan") ? "+ " : "- ") + "Rp" + formattedAmount);

        // Menampilkan tipe transaksi (pemasukan/pengeluaran)
        holder.tvType.setText(record.getType().equals("pemasukan") ? "Pemasukan" : "Pengeluaran");

        // Mengubah warna berdasarkan tipe transaksi
        if (record.getType().equals("pemasukan")) {
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")); // Hijau untuk pemasukan
        } else {
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")); // Merah untuk pengeluaran
        }
    }

    @Override
    public int getItemCount() {
        return financeRecordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvAmount, tvType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvType = itemView.findViewById(R.id.tvType);
        }
    }
}
