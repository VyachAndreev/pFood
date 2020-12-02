package com.destro.pfood.model;

public class FoodItem {
    private double price;
    private String name;
    private String description;
    private String products;
    private String imageUrl;
    private Boolean sale;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getSale() {
        return sale;
    }

    public void setSale(Boolean sale) {
        this.sale = sale;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "price=" + price +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", products='" + products + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", sale=" + sale +
                '}';
    }
}
