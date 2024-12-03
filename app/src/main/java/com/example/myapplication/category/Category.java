package com.example.myapplication.category;


    public class Category {
        private String name;
        private int iconResId;
        private boolean isMoreButton;

        public Category(String name, int iconResId, boolean isMoreButton) {
            this.name = name;
            this.iconResId = iconResId;
            this.isMoreButton = isMoreButton;
        }

        public String getName() {
            return name;
        }

        public int getIconResId() {
            return iconResId;
        }

        public boolean isMoreButton() {
            return isMoreButton;
        }
    }


