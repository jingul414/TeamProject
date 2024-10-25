package com.donghaeng.withme.login;

import com.google.firebase.firestore.FirebaseFirestore;

// 로그인
public class Login {
    private FirebaseFirestore db;
    public Login(FirebaseFirestore db){
        this.db = db;
    }
}