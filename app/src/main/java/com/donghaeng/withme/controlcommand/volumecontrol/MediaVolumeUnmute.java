package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class MediaVolumeUnmute extends VolumeControl {
    public MediaVolumeUnmute(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_UNMUTE, currentVolume, targetVolume);
    }
}
