package com.donghaeng.withme.controlcommand;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeMute extends VolumeControl {
    public VolumeMute(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.VOLUME_MUTE, currentVolume, 0);
    }
}
