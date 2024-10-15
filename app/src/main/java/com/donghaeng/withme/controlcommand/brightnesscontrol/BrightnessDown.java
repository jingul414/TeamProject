package com.donghaeng.withme.controlcommand.brightnesscontrol;

import com.donghaeng.withme.featurelist.ControlCommandList;

public class BrightnessDown extends BrightnessControl{
    public BrightnessDown(byte currentBrightness, byte targetBrightness){
        super(ControlCommandList.BRIGHTNESS_DOWN, currentBrightness, targetBrightness);
    }
}
