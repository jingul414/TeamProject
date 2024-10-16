package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeDown extends VolumeControl {
    public VolumeDown(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VOLUME_DOWN, currentVolume, targetVolume);
    }
}