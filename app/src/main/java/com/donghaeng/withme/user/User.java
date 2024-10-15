package com.donghaeng.withme.user;

public abstract class User {
    protected String name;
    protected String phone;
    protected String id;
    protected String password;
    protected byte userType = UserType.UNDEFINED;

    protected User(String name, String phone, String id, String password, byte userType) {
        setName(name);
        setPhone(phone);
        setId(id);
        setPassword(password);
        setUserType(userType);
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

    // set은 반드시 클래스 내부에서만 수정하게 할 것
    private void setName(String name) {
        this.name = name;
    }
    private void setPhone(String phone) {
        this.phone = phone;
    }
    private void setId(String id) {
        this.id = id;
    }
    private void setPassword(String password) {
        this.password = password;
    }
    private void setUserType(byte userType) {
        this.userType = userType;
    }
}