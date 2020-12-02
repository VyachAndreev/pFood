package com.destro.pfood.classes;

public class FoodCollectable extends Food {
    private Integer foodCount;
    private Integer fullPrice;

    public FoodCollectable() {
    }

    public FoodCollectable(Food food, Integer foodCount) {
        this.setName(food.getName());
        this.setPrice(food.getPrice());
        this.setDescription(food.getDescription());
        this.setDescription(food.getDescription());
        this.setImageUrl(food.getImageUrl());
        this.foodCount = foodCount;
        this.fullPrice = food.getPrice();
    }

    public void setFoodCount(Integer foodCount) {
        this.foodCount = foodCount;
        this.fullPrice = this.foodCount * this.getPrice();
    }

    public Integer getFoodCount() {
        return this.foodCount;
    }

    public Integer getFullPrice() {
        return this.fullPrice;
    }

    public void incCount() {
        this.foodCount++;
        this.fullPrice = this.foodCount * this.getPrice();
        AppSettings.getInstance().fullNumPrice += this.getPrice();
    }

    public void decCount() {
        this.foodCount--;
        this.fullPrice = this.foodCount * this.getPrice();
        AppSettings.getInstance().fullNumPrice -= this.getPrice();

        if (this.getFoodCount() == 0) {
            AppSettings.getInstance().deleteCollectable(this);
        }
    }

    public void setFullPrice(Integer fullPrice) {
        this.fullPrice = fullPrice;
    }
}
