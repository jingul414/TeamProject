package com.donghaeng.withme.data.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class MediaVolumeUp extends VolumeControl {
    public MediaVolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_UP, currentVolume, targetVolume);
    }
}
