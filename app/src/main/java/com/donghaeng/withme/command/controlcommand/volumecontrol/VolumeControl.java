package com.donghaeng.withme.command.controlcommand.volumecontrol;

import com.donghaeng.withme.command.controlcommand.ControlCommand;

public abstract class VolumeControl extends ControlCommand {
    private byte currentVolume;
    private byte targetVolume;
    //public byte memorizedVolume;

    protected VolumeControl(byte controlCommandType, byte currentVolume, byte targetVolume) {
        super(controlCommandType);
        setCurrentVolume(currentVolume);
        setTargetVolume(targetVolume);
    }
    @Override
    protected final void cancelControl(){
        // TODO: 취소 시 원래 음량으로 돌아 가는 기능 구현
    }

    public final byte getCurrentVolume() {
        return currentVolume;
    }
    public final byte getTargetVolume() {
        return targetVolume;
    }

    private void setCurrentVolume(byte currentVolume) {
        this.currentVolume = currentVolume;
    }
    private void setTargetVolume(byte targetVolume) {
        this.targetVolume = targetVolume;
    }
}
