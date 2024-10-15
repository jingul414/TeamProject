package com.donghaeng.withme.background.volumecontrolmanager;

import com.donghaeng.withme.controlcommand.ControlCommand;
import com.donghaeng.withme.controlcommand.volumecontrol.VolumeUp;

public abstract class VolumeControlManager {
    protected byte currentVolume;
    protected byte targetVolume;
    // TODO: 볼륨 조절과 관련된 기능 연결
    protected VolumeControlManager(ControlCommand controlCommand){
        currentVolume = ((VolumeUp)controlCommand).getCurrentVolume();
        targetVolume = ((VolumeUp)controlCommand).getTargetVolume();
    }
    protected abstract void changeVolume();
}
