package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class CallVolumeUp extends VolumeControl {
    public CallVolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VOLUME_UP, currentVolume, targetVolume);
    }

}
