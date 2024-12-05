package com.donghaeng.withme.data.command.controlcommand.volumecontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class MediaVolumeUnmute extends VolumeControl {
    public MediaVolumeUnmute(byte currentVolume, byte targetVolume) {
        super(ControlCommandList.MEDIA_VOLUME_UNMUTE, currentVolume, targetVolume);
    }
}
