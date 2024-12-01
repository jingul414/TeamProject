package com.donghaeng.withme.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class CallVolumeDown extends VolumeControl {
    public CallVolumeDown(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VOLUME_DOWN, currentVolume, targetVolume);
    }
}