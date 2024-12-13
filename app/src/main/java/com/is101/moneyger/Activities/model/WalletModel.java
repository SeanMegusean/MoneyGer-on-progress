package com.is101.moneyger.Activities.model;

public class WalletModel {
    private int id;
    private double balance;
    private String createdAt;

    // Constructor
    public WalletModel(int id, double balance, String createdAt) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance must be non-negative");
        }
        this.id = id;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Optional: Setter for balance
    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance must be non-negative");
        }
        this.balance = balance;
    }
}