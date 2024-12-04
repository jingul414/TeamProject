package com.donghaeng.withme.data.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;

import java.util.Objects;

public class AutomaticLoginChecker {
    private static final String PREFS_NAME = "AutomaticLoginPrefs";
    /* KEYs */
    private static final String KEY_AUTOMATIC_LOGIN = "isAutomaticLoginEnabled";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PHONE_NUMBER = "userPhoneNumber";
    private static final String KEY_USER_UID = "userUid";
    private static final String KEY_USER_TYPE = "userType";

    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    // 자동 로그인 초기화
    private static void initPrefs(Context context) {
        if (prefs == null || editor == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = prefs.edit();
        }
    }

    // 자동 로그인 상태 확인
    public static boolean isAutomaticLoginEnabled(Context context) {
        initPrefs(context);
        return prefs.getBoolean(KEY_AUTOMATIC_LOGIN, false);
    }

    // 자동 로그인 활성화
    public static void setEnable(Context context, User user) {
        initPrefs(context);
        editor.putBoolean(KEY_AUTOMATIC_LOGIN, true);
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_PHONE_NUMBER, user.getPhone());
        editor.putString(KEY_USER_UID, user.getId());
        editor.putString(KEY_USER_TYPE, String.valueOf(user.getUserType()));
        editor.apply();
        Log.e("AutomaticLoginChecker", "자동 로그인 설정 저장됨: " + prefs.getAll());
    }


    // 자동 로그인 비활성화
    public static void setDisable(Context context) {
        initPrefs(context);
        editor.putBoolean(KEY_AUTOMATIC_LOGIN, false);
        editor.putString(KEY_USER_NAME, null);
        editor.putString(KEY_USER_PHONE_NUMBER, null);
        editor.putString(KEY_USER_UID, null);
        editor.putString(KEY_USER_TYPE, null);
        editor.apply();
        Log.e("AutomaticLoginChecker", "자동 로그인 설정 저장됨: " + prefs.getAll());
    }

    // 자동 로그인을 위한 메서드 예시
    public static void performLoginIfEnabled(Context context, Runnable onLoginSuccess, Runnable onLoginFail) {
        initPrefs(context);
        if (isAutomaticLoginEnabled(context)) {
            if (!isNullUser()) {
                onLoginSuccess.run();
            } else {
                Log.e("AutomaticLoginChecker", "자동 로그인 활성화 상태지만 User 객체가 null입니다.");
                onLoginFail.run();
            }
        } else {
            Log.e("AutomaticLoginChecker", "자동 로그인 비활성화 상태입니다.");
            onLoginFail.run();
        }
    }

    private static boolean isNullUser(){
        return prefs.getString(KEY_USER_NAME, null) == null;
    }
    public static User getUser() {
        if(isNullUser()) return null;
        String name = prefs.getString(KEY_USER_NAME, null);
        String phoneNumber = prefs.getString(KEY_USER_PHONE_NUMBER, null);
        String uid = prefs.getString(KEY_USER_UID, null);
        byte userType = Byte.parseByte(Objects.requireNonNull(prefs.getString(KEY_USER_TYPE, null)));
        if(userType == UserType.CONTROLLER){
            return new Controller(name, phoneNumber, uid, "");
        }else if(userType == UserType.TARGET) {
            return new Target(name, phoneNumber, uid, "");
        }else{
            return null;
        }
    }
}
