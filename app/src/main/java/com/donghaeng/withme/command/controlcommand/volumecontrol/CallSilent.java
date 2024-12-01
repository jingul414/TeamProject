package com.donghaeng.withme.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class CallSilent extends VolumeControl {
    public CallSilent(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.CALL_SILENT, currentVolume, targetVolume);
    }
}
