package com.donghaeng.withme.user;

public class Controller extends User{
    public Controller(String name, String phone, String id, String password) {
        super(name, phone, id, password, UserType.CONTROLLER);
    }
}