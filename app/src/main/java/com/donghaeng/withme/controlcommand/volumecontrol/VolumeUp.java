package com.donghaeng.withme.controlcommand;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeUp extends VoulumeControl {
    public VolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.VOLUME_UP, currentVolume, targetVolume);
    }

}
