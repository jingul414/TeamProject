package com.donghaeng.withme.screen.setting;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

public class SharedViewModelManager {
    private static SharedViewModelManager instance;
    private final MutableLiveData<Map<String, Boolean>> liveDataMap = new MutableLiveData<>();

    private SharedViewModelManager() {}

    public static synchronized SharedViewModelManager getInstance() {
        if (instance == null) {
            instance = new SharedViewModelManager();
        }
        return instance;
    }

    public MutableLiveData<Map<String, Boolean>> getLiveDataMap() {
        return liveDataMap;
    }

    public void updateValue(String key, boolean value) {
        Map<String, Boolean> currentData = liveDataMap.getValue();
        if (currentData == null) currentData = new HashMap<>();
        currentData.put(key, value);
        liveDataMap.postValue(currentData);
    }
}
