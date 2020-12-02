package com.destro.pfood.classes;

public class Food {
    private String name;
    private Integer price;
    private String description;
    private Integer id;
    private String imageUrl;
    private String products;
    private Boolean sale;

    public Food() {

    }

    public Food(String name, Integer price, String description, Integer id, String imageUrl) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public Food(String name, Integer price, String description, String products, String imageUrl) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.products = products;
        this.imageUrl = imageUrl;
    }

    public Food(String name, Integer price, String description, String products, String imageUrl, Boolean sale) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.products = products;
        this.imageUrl = imageUrl;
        this.sale = sale;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getId() {
        return this.id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public Boolean getSale() {
        return sale;
    }

    public void setSale(Boolean sale) {
        this.sale = sale;
    }
}
