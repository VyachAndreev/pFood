package com.destro.pfood.model;

public class UserItem {
    private String name;
    private String address;

    public UserItem() {
    }

    public UserItem(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }
}
