package com.donghaeng.withme.data.database.firestore;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.Callback;

import android.util.Log;

import androidx.annotation.NonNull;

import com.donghaeng.withme.data.message.firebasemessage.FirebaseCloudMessage;
import com.google.gson.Gson;

import java.io.IOException;

public class SendDataMessage {
    public void sendDataMessage(String token, String commandType, String commandValue) {
        OkHttpClient client = new OkHttpClient();

        // 데이터 메시지 요청 본문
        String json = "{"
                + "\"token\": \"" + token + "\","
                + "\"data\": {"
                + "    \"commandType\": \"" + commandType + "\","
                + "    \"commandValue\": \"" + commandValue + "\""
                + "}"
                + "}";

        // 요청 만들기a
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url("https://senddatamessage-2qnis2bswa-uc.a.run.app")  // 실제 URL로 변경
                .post(requestBody)
                .build();

        // HTTP 요청 보내기
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("FCM", "Failed to send data message", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Data message sent successfully!");
                    // TODO: 로그 기록 구현
                } else {
                    Log.e("FCM", "Error: " + response.code() + ", Body: " + response.body().string());
                }
            }
        });
    }

    public void sendDataMessage(String token, String type, Object command) {
        OkHttpClient client = new OkHttpClient();

        FirebaseCloudMessage message = new FirebaseCloudMessage(token, type, command);
        String json = new Gson().toJson(message);

        // 요청 만들기
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url("https://senddatamessage-2qnis2bswa-uc.a.run.app")  // 실제 URL로 변경
                .post(requestBody)
                .build();

        // HTTP 요청 보내기
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("FCM", "Failed to send data message", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("FCM", "Data message sent successfully!");
                    // TODO: 로그 기록 구현
                } else {
                    Log.e("FCM", "Error: " + response.code() + ", Body: " + response.body().string());
                }
            }
        });
    }

}