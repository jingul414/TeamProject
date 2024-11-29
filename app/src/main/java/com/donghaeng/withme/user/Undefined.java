package com.donghaeng.withme.user;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class Undefined extends User{
    public Undefined(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.UNDEFINED);
    }

    protected Undefined(Parcel in) {
        super(in); // 상위 클래스 필드 복원
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags); // 상위 클래스 필드 기록
    }

    public static final Creator<Undefined> CREATOR = new Creator<Undefined>() {
        @Override
        public Undefined createFromParcel(Parcel in) {
            return new Undefined(in);
        }

        @Override
        public Undefined[] newArray(int size) {
            return new Undefined[size];
        }
    };
}
