package com.example.uts_project.all;

public class financeRecord {
    private String id;
    private String type; // "pemasukan" atau "pengeluaran"
    private String description;
    private int amount;
    private long timestamp;

    // Wajib ada constructor kosong untuk Firebase
    public financeRecord() {
    }

    public financeRecord(String id, String type, String description, int amount, long timestamp) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

