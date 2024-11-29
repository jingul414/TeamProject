package com.donghaeng.withme.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable, Parcelable {
    protected String name;
    protected String phone;
    protected String id;
    protected transient String hashedPassword;
    protected byte userType = UserType.UNDEFINED;

    protected User(String name, String phone, String id, String hashedPassword, byte userType) {
        setName(name);
        setPhone(phone);
        setId(id);
        setHashedPassword(hashedPassword);
        setUserType(userType);
    }

    protected User(Parcel in) {
        name = in.readString();
        phone = in.readString();
        id = in.readString();
        userType = in.readByte();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(id);
        dest.writeByte(userType);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // set은 반드시 클래스 내부에서만 수정하게 할 것
    private void setName(String name) {
        this.name = name;
    }
    private void setPhone(String phone) {
        this.phone = phone;
    }
    private void setId(String id) {
        this.id = id;
    }
    private void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    private void setUserType(byte userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getId() {
        return id;
    }
    public byte getUserType() {
        return userType;
    }
}