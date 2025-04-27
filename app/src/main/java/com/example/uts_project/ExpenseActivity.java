package com.example.uts_project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uts_project.expense.Expense;
import com.example.uts_project.expense.ExpenseAdapter;
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

public class ExpenseActivity extends AppCompatActivity {

    private TextInputEditText editExpenseName, editExpenseAmount, editExpenseDescription, editExpenseDate;
    private MaterialButton btnAddExpense;
    private RecyclerView recyclerViewExpenses;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private DatabaseReference expenseRef;
    private FirebaseUser currentUser;
    private ValueEventListener expenseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        // Initialize UI elements
        editExpenseName = findViewById(R.id.editExpenseName);
        editExpenseAmount = findViewById(R.id.editExpenseAmount);
        editExpenseDescription = findViewById(R.id.editExpenseDescription);
        editExpenseDate = findViewById(R.id.editExpenseDate);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);

        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExpenses.setAdapter(expenseAdapter);

        // Initialize Firebase user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            expenseRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("expense");
            loadExpenses();
        }

        editExpenseDate.setOnClickListener(v -> showDatePickerDialog());

        btnAddExpense.setOnClickListener(v -> addExpense());
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Kembali ke aktivitas sebelumnya
            super.onBackPressed();  // atau finish();
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            editExpenseDate.setText(sdf.format(selectedDate.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addExpense() {
        String name = editExpenseName.getText().toString().trim();
        String amountStr = editExpenseAmount.getText().toString().trim();
        String description = editExpenseDescription.getText().toString().trim();
        String date = editExpenseDate.getText().toString().trim();

        if (name.isEmpty() || amountStr.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // Create a new Expense object
            String expenseId = expenseRef.push().getKey();
            if (expenseId != null) {
                Expense expense = new Expense(expenseId, name, amount, description, date);

                // Save to Firebase
                expenseRef.child(expenseId).setValue(expense)
                        .addOnSuccessListener(aVoid -> {
                            clearInputFields();
                            Toast.makeText(this, "Pengeluaran berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Jumlah format tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields() {
        editExpenseName.setText("");
        editExpenseAmount.setText("");
        editExpenseDescription.setText("");
        editExpenseDate.setText("");
    }

    private void loadExpenses() {
        if (expenseRef != null) {
            expenseListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    expenseList.clear();
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        Expense expense = expenseSnapshot.getValue(Expense.class);
                        if (expense != null) {
                            expense.setId(expenseSnapshot.getKey());
                            expenseList.add(expense);
                        }
                    }
                    expenseAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ExpenseActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            expenseRef.addValueEventListener(expenseListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hapus listener untuk mencegah memory leak
        if (expenseRef != null && expenseListener != null) {
            expenseRef.removeEventListener(expenseListener);
        }
    }
}