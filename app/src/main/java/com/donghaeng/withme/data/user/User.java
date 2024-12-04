package com.donghaeng.withme.data.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class User implements Serializable, Parcelable {
    protected String name;
    protected String phone;
    @PrimaryKey
    @NonNull
    protected String id = "";
    protected transient String hashedPassword;
    protected byte userType = UserType.UNDEFINED;
    protected String token;
    public User() {
    }

    public User(String name, String phone, String id, String hashedPassword, byte userType) {
        setName(name);
        setPhone(phone);
        setId(id);
        setHashedPassword(hashedPassword);
        setUserType(userType);
    }

    //TODO : TEST용 생성자
    public User(String name, String phone, String id, String hashedPassword, byte userType, String token) {
        setName(name);
        setPhone(phone);
        setId(id);
        setHashedPassword(hashedPassword);
        setUserType(userType);
        setToken(token);
    }

    protected User(Parcel in) {
        name = in.readString();
        phone = in.readString();
        id = Objects.requireNonNull(in.readString());
        hashedPassword = in.readString();
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
        dest.writeString(hashedPassword);
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

    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setId(@NonNull String id) {
        this.id = id;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public void setUserType(byte userType) {
        this.userType = userType;
    }
    public void setToken(String token) { this.token = token; }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    @NonNull
    public String getId() {
        return id;
    }
    public String getHashedPassword() { return hashedPassword; }
    public byte getUserType() {
        return userType;
    }
    public String getToken() {
        return token;
    }
}