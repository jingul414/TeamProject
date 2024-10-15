package com.donghaeng.withme.controlcommand.brightnesscontrol;

import com.donghaeng.withme.controlcommand.ControlCommand;

abstract class BrightnessControl extends ControlCommand{
    private byte currentBrightness;
    private byte targetBrightness;
    //public byte memorizedBrightness;
    protected BrightnessControl(byte controlCommandType, byte currentBrightness, byte targetBrightness){
        super(controlCommandType);
        setCurrentBrightness(currentBrightness);
        setTargetBrightness(targetBrightness);
    }

    @Override
    protected final void cancelControl(){
        // TODO: 취소 시 원래 밝기로 돌아 가는 기능 구현
    }

    public final byte getCurrentBrightness() {
        return currentBrightness;
    }
    public final byte getTargetBrightness() {
        return targetBrightness;
    }

    private void setCurrentBrightness(byte currentBrightness){
        this.currentBrightness = currentBrightness;
    }
    private void setTargetBrightness(byte targetBrightness) {
        this.targetBrightness = targetBrightness;
    }
}
