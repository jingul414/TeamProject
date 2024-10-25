package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class CallVolumeDown extends VolumeControl {
    public CallVolumeDown(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VOLUME_DOWN, currentVolume, targetVolume);
    }
}