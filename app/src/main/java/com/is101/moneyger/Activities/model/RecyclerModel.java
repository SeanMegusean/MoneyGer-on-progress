package com.is101.moneyger.Activities.model;

public class RecyclerModel {

    private int id;
    private String title;
    private String date;
    private int amount;
    private String description;

    public RecyclerModel(int id, String title, String date, int amount, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    public RecyclerModel(String title, double amount) {
        this.title = title;
        this.amount = (int) amount; // Cast to int if needed
        this.date = ""; // Default date
        this.description = ""; // Default description
        this.id = 0; // Default ID
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}