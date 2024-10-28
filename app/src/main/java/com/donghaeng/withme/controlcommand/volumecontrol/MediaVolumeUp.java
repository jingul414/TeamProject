package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class MediaVolumeUp extends VolumeControl {
    public MediaVolumeUp(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_UP, currentVolume, targetVolume);
    }
}
