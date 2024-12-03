package com.donghaeng.withme.command.commandlistener;

import com.donghaeng.withme.command.controlcommand.ControlCommand;
import com.donghaeng.withme.command.controlcommand.brightnesscontrol.BrightnessDown;
import com.donghaeng.withme.command.controlcommand.brightnesscontrol.BrightnessUp;
import com.donghaeng.withme.command.controlcommand.volumecontrol.CallNormal;
import com.donghaeng.withme.command.controlcommand.volumecontrol.CallSilent;
import com.donghaeng.withme.command.controlcommand.volumecontrol.CallVibrate;
import com.donghaeng.withme.command.controlcommand.volumecontrol.CallVolumeDown;
import com.donghaeng.withme.command.controlcommand.volumecontrol.CallVolumeUp;
import com.donghaeng.withme.command.controlcommand.volumecontrol.MediaVolumeDown;
import com.donghaeng.withme.command.controlcommand.volumecontrol.MediaVolumeMute;
import com.donghaeng.withme.command.controlcommand.volumecontrol.MediaVolumeUnmute;
import com.donghaeng.withme.command.controlcommand.volumecontrol.MediaVolumeUp;

public class CommandListener {
    // TODO: to be executed in background!!
    // so, target user must construct this class
    public CommandListener(){}

    public void onListen(ControlCommand controlCommand){
        //전화 볼륨 컨트롤 부분
        if(controlCommand instanceof CallVolumeDown){
            //전화 볼륨 다운 실행
        }
        if(controlCommand instanceof CallVolumeUp){
            //전화 볼륨 업 실행
        }
        if(controlCommand instanceof CallNormal){
            //전화 벨소리 모드로 변경
        }
        if(controlCommand instanceof CallSilent){
            //전화 벨소리 무음 모드로 변경
        }
        if(controlCommand instanceof CallVibrate){
            //전화 벨소리 진동 모드로 변경
        }
        
        //미디어 볼륨 컨트롤 부분
        if(controlCommand instanceof MediaVolumeDown){
            //미디어 음량 다운 실행
        }
        if(controlCommand instanceof MediaVolumeUp){
            //미디어 음량 업 실행
        }
        if(controlCommand instanceof MediaVolumeMute){
            //미디어 음량 음소거 실행
        }
        if(controlCommand instanceof MediaVolumeUnmute){
            //미디어 음량 음소거 해제
        }

        //밝기 컨트롤 부분
        if(controlCommand instanceof BrightnessUp){
            //밝기 증가
        }
        if(controlCommand instanceof BrightnessDown){
            //밝기 감소
        }
    }
}