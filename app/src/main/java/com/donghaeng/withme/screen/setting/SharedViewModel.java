package com.donghaeng.withme.screen.setting;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.donghaeng.withme.data.app.ControlAllowanceListChecker;

import java.util.HashMap;
import java.util.Map;

public class SharedViewModel extends AndroidViewModel {
    private final Context context;
    private final Map<String, MutableLiveData<Boolean>> toggles = new HashMap<>();

    // 초기화 블록에서 각 토글의 기본 상태를 ControlAllowanceListChecker에서 가져옵니다.
    public SharedViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext(); // Application Context 사용

        String[] keys = {
                ControlAllowanceListChecker.KEY_STORING_NOTICE,
                ControlAllowanceListChecker.KEY_VOLUME_MODE,
                ControlAllowanceListChecker.KEY_VOLUME_CONTROL,
                ControlAllowanceListChecker.KEY_BRIGHTNESS_CONTROL,
                ControlAllowanceListChecker.KEY_SETTING_ALARM
        };

        for (String key : keys) {
            boolean storedValue = ControlAllowanceListChecker.getValue(context, key);
            toggles.put(key, new MutableLiveData<>(storedValue));
        }
    }

    // 특정 키에 해당하는 LiveData를 반환하는 메서드
    public LiveData<Boolean> getToggle(String key) {
        return toggles.get(key);
    }

    // 특정 키에 해당하는 값을 설정하는 메서드
    public void setToggle(String key, boolean value) {
        MutableLiveData<Boolean> liveData = toggles.get(key);
        if (liveData != null) {
            liveData.setValue(value);
        }
        // 설정 값을 저장합니다.
        ControlAllowanceListChecker.setValue(context, key, value);
        // TODO: 피제어자이면, 설정값 변경을 요청하는 코드 작성
    }
}