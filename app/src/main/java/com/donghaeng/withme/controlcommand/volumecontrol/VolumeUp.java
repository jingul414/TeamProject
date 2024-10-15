package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeUp extends VolumeControl {
    public VolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.VOLUME_UP, currentVolume, targetVolume);
    }

}
