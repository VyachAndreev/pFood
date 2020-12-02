package com.destro.pfood.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Order {
    private String name;
    private String address;
    private String phone;
    private HashMap<String, String> time;
    private ArrayList<String> foodCart;
    private String comment;
    private Integer price;
    private String paymentType;
    private String key;
    private String status;

    public Order(String name, String address, String phone, HashMap<String, String> time, ArrayList<String> foodCart, String comment, Integer price, String paymentType, String status) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.time = time;
        this.foodCart = foodCart;
        this.comment = comment;
        this.price = price;
        this.paymentType = paymentType;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(name, order.name) &&
                Objects.equals(address, order.address) &&
                Objects.equals(phone, order.phone) &&
                Objects.equals(time, order.time) &&
                Objects.equals(foodCart, order.foodCart) &&
                Objects.equals(comment, order.comment) &&
                Objects.equals(price, order.price) &&
                Objects.equals(paymentType, order.paymentType) &&
                Objects.equals(key, order.key) &&
                Objects.equals(status, order.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, phone, time, foodCart, comment, price, paymentType, key, status);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, String> getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTime(HashMap time) {
        this.time = time;
    }

    public ArrayList<String> getFoodCart() {
        return foodCart;
    }

    public void setFoodCart(ArrayList<String> foodCart) {
        this.foodCart = foodCart;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String payment_type) {
        this.paymentType = paymentType;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
