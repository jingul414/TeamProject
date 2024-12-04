package com.donghaeng.withme.data.user;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class Target extends User{
    private Controller controller;

    public Target(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.TARGET);
    }

    protected Target(Parcel in) {
        super(in); // 상위 클래스 필드 복원
        controller = in.readParcelable(Controller.class.getClassLoader());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags); // 상위 클래스 필드 기록
        dest.writeParcelable(controller, flags);
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

    public void addController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }
}