package com.donghaeng.withme.data.message.firebasemessage;

public class FirebaseCloudMessage {
    private final String token; // 보낼 유저 토큰
    private final int priority; // FCM 메시지 우선순위
    private final Data data; // 보낼 데이터

    public FirebaseCloudMessage(String token, String type, int priority, Object payload) {
        this.token = token;
        this.data = new Data(type, payload);
        this.priority = priority;
    }

    public static class Data {
        private final String type;  // 어떤 메세지인지 설명
        private final Object payload;   // 실제 내용 ( 만들어진 객체 )

        public Data(String type, Object payload) {
            this.type = type;
            this.payload = payload;
        }

        public String getType() {
            return type;
        }

        public Object getPayload() {
            return payload;
        }
    }

    public String getToken() {
        return token;
    }

    public int getPriority() {
        return priority;
    }

    public Data getData() {
        return data;
    }
}