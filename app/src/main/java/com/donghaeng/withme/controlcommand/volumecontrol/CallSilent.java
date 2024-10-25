package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class CallSilent extends VolumeControl {
    public CallSilent(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_SILENT, currentVolume, targetVolume);
    }
}
