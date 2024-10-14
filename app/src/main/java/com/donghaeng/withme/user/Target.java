package com.donghaeng.withme.user;

public class Target extends User{
    public Target(String name, String phone, String id, String password) {
        super(name, phone, id, password, UserType.TARGET);
    }
}