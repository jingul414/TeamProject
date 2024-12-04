package com.donghaeng.withme.data.database.firestore;

public class TokenManager {
    private static TokenManager instance;
    private String token;   //나의 토큰 저장

    // 싱글톤 인스턴스 가져오기
    public static TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    // 토큰 저장
    public void setToken(String token) {
        this.token = token;
    }

    // 토큰 가져오기
    public String getToken() {
        return token;
    }
}
