package com.example.uts_project.transaction;

public class Transaction {
    private double amount;
    private String note;
    private long timestamp;

    public Transaction(double amount, String note, long timestamp) {
        this.amount = amount;
        this.note = note;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public long getTimestamp() {
        return timestamp;
    }
}