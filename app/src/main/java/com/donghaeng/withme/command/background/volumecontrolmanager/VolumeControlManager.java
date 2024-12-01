package com.donghaeng.withme.command.background.volumecontrolmanager;

import com.donghaeng.withme.command.controlcommand.ControlCommand;
import com.donghaeng.withme.command.controlcommand.volumecontrol.CallVolumeUp;

public abstract class VolumeControlManager {
    protected byte currentVolume;
    protected byte targetVolume;
    // TODO: 볼륨 조절과 관련된 기능 연결
    protected VolumeControlManager(ControlCommand controlCommand){
        currentVolume = ((CallVolumeUp)controlCommand).getCurrentVolume();
        targetVolume = ((CallVolumeUp)controlCommand).getTargetVolume();
    }
    protected abstract void changeVolume();
}
