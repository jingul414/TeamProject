package com.donghaeng.withme.controlcommand;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class VolumeDown extends VolumeControl {
    public VolumeDown(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.VOLUME_DOWN, currentVolume, targetVolume);
    }
}