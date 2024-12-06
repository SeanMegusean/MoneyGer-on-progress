package com.is101.moneyger.Activities.model;

public class SavingModel {
    private String name;
    private double amount;
    private String startDate;
    private String endDate;
    private String userId;
    private int id;

    public SavingModel(int id, String name, double amount, String startDate, String endDate, String userId) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String comment() {
        return "This is a dummy method for pushing changes to GitHub.";
    }
}