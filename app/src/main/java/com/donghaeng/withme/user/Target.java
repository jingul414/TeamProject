package com.donghaeng.withme.user;

public class Target extends User{
    public Target(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.TARGET);
    }
}