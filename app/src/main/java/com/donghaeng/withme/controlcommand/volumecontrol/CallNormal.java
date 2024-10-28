package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class CallNormal extends VolumeControl {
    public CallNormal(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_NORMAL, currentVolume, targetVolume);
    }
}
