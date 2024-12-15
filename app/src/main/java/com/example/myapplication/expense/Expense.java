package com.example.myapplication.expense;

import com.example.myapplication.R;

public class Expense {
    private String id;
    private String name;
    private double amount;
    private String category;
    private String date;  // Date de la dépense
    private String message;  // Message
    private int categoryIcon;  // L'icône associée à la catégorie

    public Expense() {
        // Constructeur par défaut pour Firebase
    }

    public Expense(String id, String name, double amount, String category, String date, String message, int categoryIcon) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.message = message;
        this.categoryIcon = categoryIcon;  // L'icône associée à la catégorie
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCategoryIcon() {
        return categoryIcon;  // Getter pour l'icône de la catégorie
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }




}
