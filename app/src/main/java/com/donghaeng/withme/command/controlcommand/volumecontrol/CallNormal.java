package com.donghaeng.withme.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class CallNormal extends VolumeControl {
    public CallNormal(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_NORMAL, currentVolume, targetVolume);
    }
}
