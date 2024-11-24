package com.is101.moneyger.Activities;

public class ExpenseModel {
    private int id;
    private String name;
    private double amount;
    private String description;
    private String date;

    // Constructor
    public ExpenseModel(int id, String name, double amount, String description, String date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}

//this comment is used for me to push this version. Kindly dismiss this
