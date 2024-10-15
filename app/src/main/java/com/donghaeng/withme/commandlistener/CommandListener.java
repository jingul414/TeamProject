package com.donghaeng.withme.commandlistener;

import com.donghaeng.withme.controlcommand.ControlCommand;
import com.donghaeng.withme.featurelist.ControlCommandList;

public class CommandListener {
    // TODO: to be executed in background!!
    // so, target user must construct this class
    public CommandListener(){}

    public void onListen(ControlCommand controlCommand){
        switch (controlCommand.getControlCommandType()){
            case ControlCommandList.VOLUME_DOWN:
                // 음량 낮추기
                break;
            case ControlCommandList.VOLUME_UP:
                // 음량 높이기
                break;
            case ControlCommandList.MUTE:
                // 음소거
                break;
            case ControlCommandList.UNMUTE:
                // 음소거 해제
                break;
            case ControlCommandList.BRIGHTNESS_DOWN:
                // 밝기 낮추기
                break;
            case ControlCommandList.BRIGHTNESS_UP:
                // 밝기 높이기
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + controlCommand);
        }
    }
}
