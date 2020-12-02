package com.destro.pfood.model;

import androidx.annotation.NonNull;

public class FoodCategoryItem {
    private String categoryName;
    private String imageUrl;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return categoryName;
    }
}
