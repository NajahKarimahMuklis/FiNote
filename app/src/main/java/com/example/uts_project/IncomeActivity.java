package com.example.uts_project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_project.income.Income;
import com.example.uts_project.income.IncomeAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class IncomeActivity extends AppCompatActivity {

    private TextInputEditText editIncomeName, editIncomeAmount, editIncomeDescription, editIncomeDate;
    private MaterialButton btnAddIncome;
    private RecyclerView recyclerViewIncomes;
    private IncomeAdapter incomeAdapter;
    private List<Income> incomeList;
    private FirebaseUser currentUser;
    private DatabaseReference incomeRef;
    private ValueEventListener incomeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        editIncomeName = findViewById(R.id.editIncomeName);
        editIncomeAmount = findViewById(R.id.editIncomeAmount);
        editIncomeDescription = findViewById(R.id.editIncomeDescription);
        editIncomeDate = findViewById(R.id.editIncomeDate);
        btnAddIncome = findViewById(R.id.btnAddIncome);
        recyclerViewIncomes = findViewById(R.id.recyclerViewIncomes);

        incomeList = new ArrayList<>();
        incomeAdapter = new IncomeAdapter(this, incomeList);
        recyclerViewIncomes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIncomes.setAdapter(incomeAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            incomeRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("income");
            loadIncomes();
        }

        editIncomeDate.setOnClickListener(v -> showDatePickerDialog());
        btnAddIncome.setOnClickListener(v -> addIncome());
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Kembali ke aktivitas sebelumnya
            super.onBackPressed();  // atau finish();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    editIncomeDate.setText(sdf.format(selectedDate.getTime()));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void addIncome() {
        String name = editIncomeName.getText().toString().trim();
        String amountStr = editIncomeAmount.getText().toString().trim();
        String description = editIncomeDescription.getText().toString().trim();
        String date = editIncomeDate.getText().toString().trim();

        if (name.isEmpty() || amountStr.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Mohon isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            String incomeId = incomeRef.push().getKey();
            if (incomeId != null) {
                Income income = new Income(incomeId, name, amount, description, date);
                income.setId(incomeId);

                incomeRef.child(incomeId).setValue(income)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Pemasukan berhasil disimpan", Toast.LENGTH_SHORT).show();
                            clearInputFields();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format jumlah tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        editIncomeName.setText("");
        editIncomeAmount.setText("");
        editIncomeDescription.setText("");
        editIncomeDate.setText("");
    }

    private void loadIncomes() {
        if (incomeRef != null) {
            incomeListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    incomeList.clear();
                    for (DataSnapshot incomeSnapshot : snapshot.getChildren()) {
                        Income income = incomeSnapshot.getValue(Income.class);
                        if (income != null) {
                            income.setId(incomeSnapshot.getKey());
                            incomeList.add(income);
                        }
                    }
                    incomeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(IncomeActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            incomeRef.addValueEventListener(incomeListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (incomeRef != null && incomeListener != null) {
            incomeRef.removeEventListener(incomeListener);
        }
    }
}