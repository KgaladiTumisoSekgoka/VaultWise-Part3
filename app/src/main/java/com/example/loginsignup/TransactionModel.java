package com.example.loginsignup;
public class TransactionModel {
    private String title;
    private String date;
    private String amount;
    private int icon;

    public TransactionModel(String title, String date, String amount, int icon) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.icon = icon;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public int getIcon() { return icon; }
}
