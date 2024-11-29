package com.donghaeng.withme.user;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class Target extends User{
    public Target(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.TARGET);
    }

    protected Target(Parcel in) {
        super(in); // 상위 클래스 필드 복원
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags); // 상위 클래스 필드 기록
    }

    public static final Creator<Target> CREATOR = new Creator<Target>() {
        @Override
        public Target createFromParcel(Parcel in) {
            return new Target(in);
        }

        @Override
        public Target[] newArray(int size) {
            return new Target[size];
        }
    };
}