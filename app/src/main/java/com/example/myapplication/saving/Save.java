package com.example.myapplication.saving;

public class Save {

    private String name;
    private String date;
    private double amount;
    private String message; // Champ message
    private String id_saving; // Référence à l'ID de la classe Saving

    // Constructeur vide requis pour Firestore
    public Save() {
    }

    // Constructeur avec paramètres
    public Save(String name, String date, double amount, String message, String id_saving) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.message = message;
        this.id_saving = id_saving;
    }

    // Getters et Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId_saving() {
        return id_saving;
    }

    public void setId_saving(String id_saving) {
        this.id_saving = id_saving;
    }

    // Méthode fictive pour obtenir une icône (si nécessaire)
    public int getIconResId() {
        return 0;  // Remplacez par un ID d'icône réel
    }
}
