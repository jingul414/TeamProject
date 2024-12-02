package com.donghaeng.withme.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class CallVibrate extends VolumeControl{
    public CallVibrate(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VIBRATE, currentVolume, targetVolume);
    }
}
