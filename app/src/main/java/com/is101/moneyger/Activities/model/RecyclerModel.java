package com.is101.moneyger.Activities.model;

public class RecyclerModel {

    private String title;
    private String date;
    private int amount;

    public RecyclerModel(String title, String date, int amount) {
        this.title = title;
        this.date = date;
        this.amount = amount;
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
}

