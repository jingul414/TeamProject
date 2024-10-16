package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeUp extends VolumeControl {
    public VolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VOLUME_UP, currentVolume, targetVolume);
    }

}
