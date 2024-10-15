package com.donghaeng.withme.controlcommand;

public abstract class VolumeControl extends ControlCommand {
    public byte currentVolume;
    public byte targetVolume;
    public byte memorizedVolume;

    public VolumeControl(byte controlCommandType, byte currentVolume, byte targetVolume) {
        super(controlCommandType);
        setCurrentVolume(currentVolume);
        setTargetVolume(targetVolume);
    }

    public final void cancelControl(){
        // TODO: 취소 시 원래 볼륨으로 돌아가는 기능 구현
    }

    public final byte getCurrentVolume() {
        return currentVolume;
    }
    public final void setCurrentVolume(byte currentVolume) {
        this.currentVolume = currentVolume;
    }
    public final byte getTargetVolume() {
        return targetVolume;
    }
    public final void setTargetVolume(byte targetVolume) {
        this.targetVolume = targetVolume;
    }
}
