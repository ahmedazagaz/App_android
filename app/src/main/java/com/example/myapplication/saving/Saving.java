package com.example.myapplication.saving;

public class Saving {

    private String id_saving; // Identifiant unique
    private String name;      // Nom de l'épargne
    private int iconResId;    // ID de la ressource de l'icône
    private double goal;      // Montant de l'objectif
    private double amountSaved; // Montant total économisé (calculé)

    // Constructeur vide pour Firestore
    public Saving() {
    }

    // Constructeur avec tous les paramètres
    public Saving(String id_saving, String name, int iconResId, double goal, double amountSaved) {
        this.id_saving = id_saving;
        this.name = name;
        this.iconResId = iconResId;
        this.goal = goal;
        this.amountSaved = amountSaved;
    }

    // Constructeur sans id_saving
    public Saving(String name, int iconResId, double goal) {
        this.name = name;
        this.iconResId = iconResId;
        this.goal = goal;
        this.amountSaved = 0; // Valeur par défaut
    }

    // Getters et Setters
    public String getId_saving() {
        return id_saving;
    }

    public void setId_saving(String id_saving) {
        this.id_saving = id_saving;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public double getAmountSaved() {
        return amountSaved;
    }

    public void setAmountSaved(double amountSaved) {
        this.amountSaved = amountSaved;
    }
}
