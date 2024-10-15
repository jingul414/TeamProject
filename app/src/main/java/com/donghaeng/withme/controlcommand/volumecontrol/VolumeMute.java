package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeMute extends VolumeControl {
    public VolumeMute(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MUTE, currentVolume, targetVolume);
    }
}
