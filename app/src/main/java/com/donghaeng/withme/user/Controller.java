package com.donghaeng.withme.user;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class Controller extends User{
    public Controller(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.CONTROLLER);
    }

    protected Controller(Parcel in) {
        super(in); // 상위 클래스 필드 복원
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags); // 상위 클래스 필드 기록
    }

    public static final Creator<Controller> CREATOR = new Creator<Controller>() {
        @Override
        public Controller createFromParcel(Parcel in) {
            return new Controller(in);
        }

        @Override
        public Controller[] newArray(int size) {
            return new Controller[size];
        }
    };
}