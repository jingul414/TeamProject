package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeUnmute extends VolumeControl {
    public VolumeUnmute(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_NORMAL, currentVolume, targetVolume);
    }
}
