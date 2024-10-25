package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class CallVibrate extends VolumeControl{
    public CallVibrate(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_VIBRATE, currentVolume, targetVolume);
    }
}
