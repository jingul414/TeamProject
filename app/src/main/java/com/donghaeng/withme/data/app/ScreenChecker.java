package com.donghaeng.withme.data.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.donghaeng.withme.screen.ScreenList;
import com.donghaeng.withme.screen.main.ControllerActivity;
import com.donghaeng.withme.screen.main.TargetActivity;
import com.donghaeng.withme.screen.start.StartActivity;

public class ScreenChecker {
    private static final String PREFS_NAME = "ControlAllowanceListPrefs";
    private static final String KEY_INITIAL_SCREEN = "initialScreen";

    private static SharedPreferences prefs;

    // 초기화
    private static void initPrefs(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setInitialScreen(Context context, String className) {
        initPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_INITIAL_SCREEN, className);
        editor.apply();
    }

    public static void getInitialScreen(Context context) {
        initPrefs(context);
        String screenName = prefs.getString(KEY_INITIAL_SCREEN, ScreenList.ACTIVITY.START);

        if (isClassName(screenName)) {
            executeNextActivity(context, screenName);
        } else {
            if (screenName.equals(ScreenList.FRAGMENT.LOGIN)) {
                if (AutomaticLoginChecker.isAutomaticLoginEnabled(context)) {
                    new Handler().postDelayed(() -> AutomaticLoginChecker.performLoginIfEnabled(
                            context,
                            () -> {
                                User user = AutomaticLoginChecker.getUser();
                                if (user != null) { // User 객체 null 체크 추가
                                    if (user.getUserType() == UserType.CONTROLLER) {
                                        Intent intent = new Intent(context, ControllerActivity.class);
                                        intent.putExtra("user", (Parcelable) user);
                                        context.startActivity(intent);
                                    } else if (user.getUserType() == UserType.TARGET) {
                                        Intent intent = new Intent(context, TargetActivity.class);
                                        intent.putExtra("user", (Parcelable) user);
                                        context.startActivity(intent);
                                    }
                                    ((Activity) context).finish();
                                }
                            },
                            () -> Log.e("ScreenChecker", "자동로그인 불가능")), 2500);
                } else {
                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(context, StartActivity.class);
                        intent.putExtra("fragmentName", ScreenList.FRAGMENT.LOGIN);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }, 2500);
                }
            } else if (screenName.equals(ScreenList.FRAGMENT.SIGNUP_NAME)) {
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(context, StartActivity.class);
                    intent.putExtra("fragmentName", ScreenList.FRAGMENT.SIGNUP_NAME);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }, 2500);
            } else {
                Log.e("ScreenChecker", "Invalid screen name: " + screenName);
            }
        }
    }

    private static boolean isClassName(String className) {
        return className.contains("com.donghaeng.withme.screen.");
    }

    private static void executeNextActivity(Context context, String className) {
        new Handler().postDelayed(() -> {
            Intent intent;
            try {
                // 클래스 이름을 통해 클래스 객체를 동적으로 로드합니다.
                ClassLoader classLoader = context.getClassLoader();
                Class<?> loadedClass = classLoader.loadClass(className);
                Class<? extends AppCompatActivity> targetClass = loadedClass.asSubclass(AppCompatActivity.class);
                intent = new Intent(context, targetClass);
            } catch (ClassNotFoundException | ClassCastException e) {
                // 기본값으로 SettingActivity로 이동
                intent = new Intent(context, com.donghaeng.withme.screen.start.StartActivity.class);
            }
            context.startActivity(intent);
            ((Activity) context).finish();
        }, 2500);
    }
}
