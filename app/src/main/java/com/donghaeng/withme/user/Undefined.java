package com.donghaeng.withme.user;

public class Undefined extends User{
    public Undefined(String name, String phone, String id, String password) {
        super(name, phone, id, password, UserType.UNDEFINED);
    }
}
