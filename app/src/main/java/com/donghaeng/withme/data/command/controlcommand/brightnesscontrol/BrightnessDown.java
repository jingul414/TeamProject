package com.donghaeng.withme.data.command.controlcommand.brightnesscontrol;

import com.donghaeng.withme.data.featurelist.ControlCommandList;

public class BrightnessDown extends BrightnessControl{
    public BrightnessDown(byte currentBrightness, byte targetBrightness){
        super(ControlCommandList.BRIGHTNESS_DOWN, currentBrightness, targetBrightness);
    }
}
