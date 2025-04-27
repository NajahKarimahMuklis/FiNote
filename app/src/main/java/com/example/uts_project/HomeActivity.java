package com.example.uts_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_project.all.financeRecord;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.uts_project.all.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private CardView cardIncome, cardExpense, cardSavingGoal;
    private BottomNavigationView bottomNavigationView;
    private TextView tvViewAll;
    private RecyclerView rvTransactions;
    private List<financeRecord> financeRecordList;
    private financeRecordAdapter financeRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardIncome = findViewById(R.id.cardIncome);
        cardExpense = findViewById(R.id.cardExpense);
        cardSavingGoal = findViewById(R.id.cardSavingGoal);
        tvViewAll = findViewById(R.id.tvViewAll);
        rvTransactions = findViewById(R.id.rvTransactions);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        financeRecordList = new ArrayList<>();
        financeRecordAdapter = new financeRecordAdapter(financeRecordList);
        rvTransactions.setAdapter(financeRecordAdapter);

        cardIncome.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, IncomeActivity.class);
            startActivity(intent);
        });
        cardExpense.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ExpenseActivity.class);
            startActivity(intent);
        });
        cardSavingGoal.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SavingGoalActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                return true;
            }
            return false;
        });

        // Panggil untuk ambil data transaksi
        fetchFinanceRecords();
    }

    private void fetchFinanceRecords() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Ambil user id
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        // Ambil data dari 'pemasukan' dan 'pengeluaran'
        database.child("income").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Ambil data pemasukan
                for (DataSnapshot data : snapshot.getChildren()) {
                    financeRecord record = data.getValue(financeRecord.class);
                    if (record != null) {
                        record.setType("pemasukan"); // Set type sebagai pemasukan
                        financeRecordList.add(record);
                    }
                }

                // Ambil data pengeluaran
                database.child("expense").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Ambil data pengeluaran
                        for (DataSnapshot data : snapshot.getChildren()) {
                            financeRecord record = data.getValue(financeRecord.class);
                            if (record != null) {
                                record.setType("pengeluaran"); // Set type sebagai pengeluaran
                                financeRecordList.add(record);
                            }
                        }

                        // Urutkan berdasarkan timestamp terbaru
                        Collections.sort(financeRecordList, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                        financeRecordAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error jika pengambilan data gagal
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error jika pengambilan data gagal
            }
        });
    }
}
