package com.donghaeng.withme.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class CallVolumeUp extends VolumeControl {
    public CallVolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VOLUME_UP, currentVolume, targetVolume);
    }

}
