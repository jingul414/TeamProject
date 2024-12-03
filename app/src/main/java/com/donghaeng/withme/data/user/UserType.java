package com.donghaeng.withme.data.user;

public class UserType{
    // 유저 유형: 관리자, 제어자, 피제어자
    public static final byte UNDEFINED = 0;
    public static final byte ADMIN = 1;
    public static final byte CONTROLLER = 2;
    public static final byte TARGET = 3;
}