package com.example.myapplication.saving;

public class Saving {

    private String name;
    private int iconResId;

    // Empty constructor required for Firestore
    public Saving() {
    }

    public Saving(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
