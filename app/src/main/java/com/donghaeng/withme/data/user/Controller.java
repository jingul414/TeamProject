package com.donghaeng.withme.data.user;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Controller extends User{
    private List<Target> targets;

    public Controller(String name, String phone, String id, String hashedPassword) {
        super(name, phone, id, hashedPassword, UserType.CONTROLLER);
    }

    protected Controller(Parcel in) {
        super(in); // 상위 클래스 필드 복원
        targets = in.createTypedArrayList(Target.CREATOR);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags); // 상위 클래스 필드 기록
        dest.writeTypedList(targets);
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

    public void addTarget(Target target) {
        if( targets == null ) targets = new ArrayList<>();
        targets.add(target);
    }

    public void updateTarget(Target target){
        if( targets == null ){
            targets = new ArrayList<>();
            targets.add(target);
            return;
        }
        for(Target t : targets){
            if(t.getId().equals(target.getId())){
                targets.remove(t);
                targets.add(target);
                return;
            }
        }
    }

    public List<Target> getTargets() {
        return targets;
    }
}