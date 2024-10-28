package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class MediaVolumeDown extends VolumeControl {
    public MediaVolumeDown(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_DOWN, currentVolume, targetVolume);
    }
}
