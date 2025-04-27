package com.example.uts_project.income;

public class Income {
    private String name;
    private double amount;
    private String description;
    private String date;
    private String key;
    private String id;

    // Constructor
    public Income(String id, String name, double amount, String description, String date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
    public Income () {

    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;

    }
    public void setId(String id) {
        this.id = id;
    }
}
