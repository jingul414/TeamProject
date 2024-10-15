package com.donghaeng.withme.controlcommand.brightnesscontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class BrightnessUp extends BrightnessControl{
    public BrightnessUp(byte currentBrightness, byte targetBrightness){
        super(ControlCommandList.BRIGHTNESS_UP, currentBrightness, targetBrightness);
    }
}
