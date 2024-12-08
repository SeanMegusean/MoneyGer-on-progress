package com.is101.moneyger.Activities.model;

public class SavingModel {
    private int id;           // Unique identifier for the saving entry
    private String name;      // Name of the saving
    private double amount;    // Amount saved
    private String startDate; // Start date of the saving period
    private String endDate;   // End date of the saving period
    private int userId;       // ID of the user associated with this saving

    // Constructor to initialize a SavingModel object
    public SavingModel(int id, String name, double amount, String startDate, String endDate, int userId) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId; // User ID as an integer
    }

    // Getters for the private fields
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getUserId() {
        return userId; // Return user ID as an integer
    }

    // Sample method for demonstration purposes
    public String comment() {
        return "This is a dummy method for pushing changes to GitHub.";
    }
}