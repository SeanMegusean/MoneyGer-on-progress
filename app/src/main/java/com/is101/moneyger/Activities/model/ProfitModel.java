package com.is101.moneyger.Activities.model;

public class ProfitModel {
    private int id;
    private String name;
    private double amount;
    private String createdAt;

    public ProfitModel(int id, String name, double amount, String createdAt) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}