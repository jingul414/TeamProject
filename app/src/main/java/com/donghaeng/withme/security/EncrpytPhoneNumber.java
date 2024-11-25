package com.donghaeng.withme.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncrpytPhoneNumber {
    //전화번호를 받아서 해시화하는 메소드
    public static String hashPhoneNumber(String phoneNumber) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(phoneNumber.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }}
