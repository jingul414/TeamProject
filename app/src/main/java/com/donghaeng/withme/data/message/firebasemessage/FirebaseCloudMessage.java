package com.donghaeng.withme.data.message.firebasemessage;

public class FirebaseCloudMessage {
    private final String token; // 보낼 유저 토큰
    private final String type;  // 어떤 메세지인지 설명
    private final Object payload;   // 실제 내용 ( 만들어진 객체 )
    private String priority = "high"; // FCM 메시지 우선순위

    public FirebaseCloudMessage(String token, String type, Object payload) {
        this.token = token;
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public Object getPayload() {
        return payload;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}