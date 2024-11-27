package com.donghaeng.withme.screen.setting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> toggle1 = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> toggle2 = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> toggle3 = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> toggle4 = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> toggle5 = new MutableLiveData<>(false);

    public LiveData<Boolean> getToggle1() {
        return toggle1;
    }

    public void setToggle1(boolean value) {
        toggle1.setValue(value);
    }

    public LiveData<Boolean> getToggle2() {
        return toggle2;
    }

    public void setToggle2(boolean value) {
        toggle2.setValue(value);
    }

    public LiveData<Boolean> getToggle3() {
        return toggle3;
    }

    public void setToggle3(boolean value) {
        toggle3.setValue(value);
    }

    public LiveData<Boolean> getToggle4() {
        return toggle4;
    }

    public void setToggle4(boolean value) {
        toggle4.setValue(value);
    }

    public LiveData<Boolean> getToggle5() {
        return toggle5;
    }

    public void setToggle5(boolean value) {
        toggle5.setValue(value);
    }
}