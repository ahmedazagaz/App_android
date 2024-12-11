package com.example.myapplication.category;

public class Category {
    private String id; // ID unique
    private String name;
    private int iconResId; // Peut être une URL pour des icônes personnalisées dans Firebase
    private boolean isMoreButton; // Indique si cette catégorie est un bouton spécial

    // Constructeur par défaut requis pour Firebase
    public Category() {
    }

    public Category( String name, int iconResId, boolean isMoreButton) {

        this.name = name;
        this.iconResId = iconResId;
        this.isMoreButton = isMoreButton;
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

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isMoreButton() {
        return isMoreButton;
    }

    public void setMoreButton(boolean moreButton) {
        isMoreButton = moreButton;
    }
}
