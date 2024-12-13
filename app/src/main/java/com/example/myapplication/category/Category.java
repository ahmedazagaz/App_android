package com.example.myapplication.category;

public class Category {
    private String id; // Unique ID
    private String name;
    private int iconResId; // Can be a URL for Firebase icons
    private boolean isMoreButton;
    private String userId; // ID of the user who owns this category

    public Category() {
        // Default constructor required for Firebase
    }

    public Category(String name, int iconResId, boolean isMoreButton, String userId) {
        this.name = name;
        this.iconResId = iconResId;
        this.isMoreButton = isMoreButton;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
