package com.donghaeng.withme.login;

import com.google.firebase.firestore.FirebaseFirestore;

// 로그인
public class Login {
    private FirebaseFirestore db;
    public Login(){
        // 로그인 생성
    }
    public Login(FirebaseFirestore db){
        this.db = db;
    }
}