package com.donghaeng.withme.user;

public abstract class User {
    protected String name;
    protected String phone;
    protected String id;
    protected String password;
    protected byte userType;

    public User(String name, String phone, String id, String password, byte userType) {
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.password = password;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getId() {
        return id;
    }
    public String getPassword() {
        return password;
    }
    public byte getUserType() {
        return userType;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserType(byte userType) {
        this.userType = userType;
    }
}