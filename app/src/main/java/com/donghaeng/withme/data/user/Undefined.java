package com.donghaeng.withme.data.user;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class Undefined extends User{
    public Undefined(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.UNDEFINED);
    }

    //TODO : TEST 용 생성자
    public Undefined(String name, String phone, String id, String hashedPassword, String token) {
        super(name, phone, id, hashedPassword, UserType.UNDEFINED, token);
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
