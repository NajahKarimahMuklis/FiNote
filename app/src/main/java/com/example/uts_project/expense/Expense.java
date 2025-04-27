package com.example.uts_project.expense;

public class Expense {
    private String id; // ID dari pengeluaran
    private String name;
    private double amount;
    private String description;
    private String date;

    // Constructor dengan ID
    public Expense(String id, String name, double amount, String description, String date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Constructor tanpa ID (untuk memudahkan saat menambah data)
    public Expense(){

    }


    // Getter and Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
