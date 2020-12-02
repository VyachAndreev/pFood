package com.destro.pfood.classes;

import java.util.ArrayList;

public class FoodCategory {

    private String name;
    private Integer id;
    private ArrayList<Food> foodList;
    private String imageUrl;

    public FoodCategory() {
    }

    public FoodCategory(String name, Integer id, ArrayList<Food> foodList, String imageUrl) {
        this.name = name;
        this.id = id;

        this.foodList = new ArrayList<>();
        this.foodList = foodList;

        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFoodList(ArrayList<Food> foodList) {
        this.foodList = foodList;
    }



    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public ArrayList<Food> getFoodList() {
        return this.foodList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
