package com.donghaeng.withme.user;

public class Undefined extends User{
    public Undefined(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.UNDEFINED);
    }
}
