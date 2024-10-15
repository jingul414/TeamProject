package com.donghaeng.withme.background.volumecontrolmanager;

import com.donghaeng.withme.controlcommand.ControlCommand;
import com.donghaeng.withme.featurelist.ControlCommandList;

public abstract class VolumeControlManger {
    protected ControlCommand controlCommand;
    // TODO: 볼륨 조절과 관련된 기능 연결
    protected VolumeControlManger(ControlCommand controlCommand){
        this.controlCommand = controlCommand;
    }
    protected abstract void changeVolume();
}
