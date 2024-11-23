package com.is101.moneyger.Activities.model;

public class RecyclerModel {

    private int id;  // Add this field for the ID
    private String title;
    private String date;
    private int amount;
    private String description;

    // Add id to the constructor
    public RecyclerModel(int id, String title, String date, int amount, String description) {
        this.id = id;  // Initialize the ID
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    public RecyclerModel(int id, String name, double amount, String date) {
    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Getter for date
    public String getDate() {
        return date;
    }

    // Getter for amount
    public int getAmount() {
        return amount;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }
}