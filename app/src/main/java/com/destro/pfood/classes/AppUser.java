package com.destro.pfood.classes;

public class AppUser{
    private static AppUser instance;
    private AppUser() {
        userID = null;
        userName = null;
        userAddress = null;
        userPhone = null;
    }
    private String userID;
    private String userName;
    private String userAddress;
    private String userPhone;
    private int userLevelAccess;


    public int getUserLevelAccess() {
        return userLevelAccess;
    }

    public void setUserLevelAccess(int userLevelAccess) {
        this.userLevelAccess = userLevelAccess;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserName() {
        return userName;
    }

    public static synchronized AppUser getInstance() {
        if (instance == null) {
            instance = new AppUser();
        }

        return instance;
    }

}
