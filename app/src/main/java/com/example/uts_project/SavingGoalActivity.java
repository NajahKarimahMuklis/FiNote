package com.example.uts_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uts_project.transaction.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavingGoalActivity extends AppCompatActivity {
    private ImageButton backButton;
    private ImageButton addTransactionButton;
    private ImageButton showTransactionsButton;
    private CardView addTransactionCard;
    private RecyclerView transactionsRecyclerView;
    private EditText savingGoalNameEdit;
    private ProgressBar progressBar;
    private TextView savedAmountText;
    private TextView progressPercentageText;
    private TextView targetAmountText;
    private TextView savedLabel;
    private TextView targetLabel;

    private double savedAmount = 0;
    private double targetAmount = 0;
    private boolean isTargetSet = false;
    private List<Transaction> transactions = new ArrayList<>();
    private transactionAdapter transactionAdapter;
    private boolean isTransactionsVisible = false;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SavingGoalPrefs";
    private static final String KEY_TARGET_AMOUNT = "targetAmount";
    private static final String KEY_SAVED_AMOUNT = "savedAmount";
    private static final String KEY_IS_TARGET_SET = "isTargetSet";
    private static final String KEY_GOAL_NAME = "goalName";
    private static final String KEY_TRANSACTION_COUNT = "transactionCount";
    private static final String KEY_TRANSACTION_AMOUNT_PREFIX = "transactionAmount_";
    private static final String KEY_TRANSACTION_NOTE_PREFIX = "transactionNote_";
    private static final String KEY_TRANSACTION_TIME_PREFIX = "transactionTime_";
    private static final String KEY_TRANSACTIONS_VISIBLE = "transactionsVisible";

    // Tambahkan USER_ID untuk memisahkan data berdasarkan user
    private static final String KEY_USER_ID = "currentUserId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_goal);

        backButton = findViewById(R.id.backButton);
        addTransactionButton = findViewById(R.id.addTransactionButton);
        showTransactionsButton = findViewById(R.id.showTransactionsButton);
        addTransactionCard = findViewById(R.id.addTransactionCard);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        savingGoalNameEdit = findViewById(R.id.savingGoalNameEdit);
        progressBar = findViewById(R.id.firstProgressBar);
        savedAmountText = findViewById(R.id.savedAmount);
        progressPercentageText = findViewById(R.id.progressPercentage);
        targetAmountText = findViewById(R.id.targetAmount);
        savedLabel = findViewById(R.id.savedLabel);
        targetLabel = findViewById(R.id.targetLabel);

        initializeSharedPreferences();

        transactions = new ArrayList<>();
        transactionAdapter = new transactionAdapter(this, transactions);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(transactionAdapter);

        loadData();

        setupUI();

        setupClickListeners();
    }

    private void initializeSharedPreferences() {
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = userPrefs.getString(KEY_USER_ID, "default_user");

        sharedPreferences = getSharedPreferences(PREFS_NAME + "_" + userId, Context.MODE_PRIVATE);
    }

    private void setupUI() {
        String goalName = sharedPreferences.getString(KEY_GOAL_NAME, "");
        if (!goalName.isEmpty()) {
            savingGoalNameEdit.setText(goalName);
        }

        if (isTargetSet) {
            progressBar.setVisibility(View.VISIBLE);
            savedAmountText.setVisibility(View.VISIBLE);
            targetAmountText.setVisibility(View.VISIBLE);
            savedLabel.setVisibility(View.VISIBLE);
            targetLabel.setVisibility(View.VISIBLE);
            progressPercentageText.setVisibility(View.VISIBLE);

            EditText targetAmountEditText = findViewById(R.id.targetAmountEditText);
            if (targetAmountEditText != null) {
                targetAmountEditText.setText(String.valueOf(targetAmount));
                targetAmountEditText.setEnabled(false);
            }

            updateProgressUI();
        } else {
            progressBar.setVisibility(View.GONE);
        }

        isTransactionsVisible = sharedPreferences.getBoolean(KEY_TRANSACTIONS_VISIBLE, false);
        transactionsRecyclerView.setVisibility(isTransactionsVisible ? View.VISIBLE : View.GONE);
    }

    private void loadData() {
        savedAmount = Double.longBitsToDouble(sharedPreferences.getLong(KEY_SAVED_AMOUNT, Double.doubleToLongBits(0)));
        targetAmount = Double.longBitsToDouble(sharedPreferences.getLong(KEY_TARGET_AMOUNT, Double.doubleToLongBits(0)));
        isTargetSet = sharedPreferences.getBoolean(KEY_IS_TARGET_SET, false);

        int transactionCount = sharedPreferences.getInt(KEY_TRANSACTION_COUNT, 0);
        transactions.clear();

        for (int i = 0; i < transactionCount; i++) {
            double amount = Double.longBitsToDouble(sharedPreferences.getLong(KEY_TRANSACTION_AMOUNT_PREFIX + i, 0));
            String note = sharedPreferences.getString(KEY_TRANSACTION_NOTE_PREFIX + i, "");
            long timestamp = sharedPreferences.getLong(KEY_TRANSACTION_TIME_PREFIX + i, System.currentTimeMillis());

            Transaction transaction = new Transaction(amount, note, timestamp);
            transactions.add(transaction);
        }

        if (transactionAdapter != null) {
            transactionAdapter.notifyDataSetChanged();
        }
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String goalName = savingGoalNameEdit.getText().toString();
        editor.putString(KEY_GOAL_NAME, goalName);

        editor.putLong(KEY_SAVED_AMOUNT, Double.doubleToLongBits(savedAmount));
        editor.putLong(KEY_TARGET_AMOUNT, Double.doubleToLongBits(targetAmount));
        editor.putBoolean(KEY_IS_TARGET_SET, isTargetSet);
        editor.putBoolean(KEY_TRANSACTIONS_VISIBLE, isTransactionsVisible);
        editor.putInt(KEY_TRANSACTION_COUNT, transactions.size());

        SharedPreferences.Editor clearEditor = sharedPreferences.edit();
        int oldCount = sharedPreferences.getInt(KEY_TRANSACTION_COUNT, 0);
        for (int i = 0; i < oldCount; i++) {
            clearEditor.remove(KEY_TRANSACTION_AMOUNT_PREFIX + i);
            clearEditor.remove(KEY_TRANSACTION_NOTE_PREFIX + i);
            clearEditor.remove(KEY_TRANSACTION_TIME_PREFIX + i);
        }
        clearEditor.apply();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            editor.putLong(KEY_TRANSACTION_AMOUNT_PREFIX + i, Double.doubleToLongBits(transaction.getAmount()));
            editor.putString(KEY_TRANSACTION_NOTE_PREFIX + i, transaction.getNote());
            editor.putLong(KEY_TRANSACTION_TIME_PREFIX + i, transaction.getTimestamp());
        }

        editor.apply();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            saveData();
            navigateToHome();
        });

        addTransactionButton.setOnClickListener(v -> {
            if (addTransactionCard.getVisibility() == View.VISIBLE) {
                addTransactionCard.setVisibility(View.GONE);
            } else {
                addTransactionCard.setVisibility(View.VISIBLE);
            }
        });

        showTransactionsButton.setOnClickListener(v -> {
            isTransactionsVisible = !isTransactionsVisible;
            transactionsRecyclerView.setVisibility(isTransactionsVisible ? View.VISIBLE : View.GONE);
            sharedPreferences.edit().putBoolean(KEY_TRANSACTIONS_VISIBLE, isTransactionsVisible).apply();
        });

        Button saveTransactionButton = findViewById(R.id.saveTransactionButton);
        saveTransactionButton.setOnClickListener(v -> saveTransaction());

        Button cancelTransactionButton = findViewById(R.id.cancelTransactionButton);
        cancelTransactionButton.setOnClickListener(v -> {
            addTransactionCard.setVisibility(View.GONE);
        });

        savingGoalNameEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                saveData();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void saveTransaction() {
        EditText amountEditText = findViewById(R.id.amountEditText);
        EditText noteEditText = findViewById(R.id.noteEditText);
        EditText targetAmountEditText = findViewById(R.id.targetAmountEditText);
        RadioButton depositRadioButton = findViewById(R.id.depositRadioButton);

        String amountStr = amountEditText.getText().toString();
        String note = noteEditText.getText().toString();
        String targetAmountStr = targetAmountEditText.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Masukkan jumlah", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            boolean isDeposit = depositRadioButton.isChecked();

            if (!targetAmountStr.isEmpty() && !isTargetSet) {
                targetAmount = Double.parseDouble(targetAmountStr);
                if (targetAmount <= 0) {
                    Toast.makeText(this, "Target tabungan harus lebih dari 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                isTargetSet = true;
                targetAmountEditText.setEnabled(false);
            } else if (targetAmountStr.isEmpty() && !isTargetSet) {
                Toast.makeText(this, "Masukkan target tabungan", Toast.LENGTH_SHORT).show();
                return;
            }

            Transaction transaction = new Transaction(
                    isDeposit ? amount : -amount,
                    note,
                    System.currentTimeMillis()
            );

            transactions.add(0, transaction);
            transactionAdapter.notifyItemInserted(0);

            if (isDeposit) {
                savedAmount += amount;
            } else {
                savedAmount -= amount;
                if (savedAmount < 0) savedAmount = 0;
            }

            progressBar.setVisibility(View.VISIBLE);
            savedAmountText.setVisibility(View.VISIBLE);
            targetAmountText.setVisibility(View.VISIBLE);
            savedLabel.setVisibility(View.VISIBLE);
            targetLabel.setVisibility(View.VISIBLE);
            progressPercentageText.setVisibility(View.VISIBLE);

            updateProgressUI();

            saveData();

            amountEditText.setText("");
            noteEditText.setText("");
            addTransactionCard.setVisibility(View.GONE);

            if (!isTransactionsVisible) {
                transactionsRecyclerView.setVisibility(View.VISIBLE);
                isTransactionsVisible = true;
                sharedPreferences.edit().putBoolean(KEY_TRANSACTIONS_VISIBLE, true).apply();
            }

            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format jumlah tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgressUI() {
        double percentage = (savedAmount / targetAmount) * 100;
        int progressValue = (int) percentage;

        if (progressValue > 100) {
            progressValue = 100;
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String savedFormatted = currencyFormat.format(savedAmount).replace("Rp", "Rp");
        String targetFormatted = currencyFormat.format(targetAmount).replace("Rp", "Rp");

        savedAmountText.setText(savedFormatted);
        targetAmountText.setText(targetFormatted);
        progressPercentageText.setText(String.format("%.1f%%", percentage));
        progressBar.setProgress(progressValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        setupUI();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
        navigateToHome();
    }
}