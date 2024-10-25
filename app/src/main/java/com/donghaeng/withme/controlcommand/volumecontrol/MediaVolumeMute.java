package com.donghaeng.withme.controlcommand.volumecontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class MediaVolumeMute extends VolumeControl {
    public MediaVolumeMute(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_MUTE, currentVolume, targetVolume);
    }
}
