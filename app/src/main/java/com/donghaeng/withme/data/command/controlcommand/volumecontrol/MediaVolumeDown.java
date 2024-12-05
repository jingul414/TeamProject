package com.donghaeng.withme.data.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class MediaVolumeDown extends VolumeControl {
    public MediaVolumeDown(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_DOWN, currentVolume, targetVolume);
    }
}
