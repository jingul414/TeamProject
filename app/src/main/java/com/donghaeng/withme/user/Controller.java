package com.donghaeng.withme.user;

public class Controller extends User{
    public Controller(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.CONTROLLER);
    }
}