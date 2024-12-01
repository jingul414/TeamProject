package com.donghaeng.withme.command.controlcommand.brightnesscontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class BrightnessUp extends BrightnessControl{
    public BrightnessUp(byte currentBrightness, byte targetBrightness){
        super(ControlCommandList.BRIGHTNESS_UP, currentBrightness, targetBrightness);
    }
}
