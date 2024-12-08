//package com.donghaeng.withme.command.background.volumecontrolmanager;
//
//import android.util.Log;
//import com.donghaeng.withme.command.controlcommand.ControlCommand;
//import java.util.Objects;
//
//public class VolumeUpManager extends VolumeControlManager {
//    //private AudioManager audioManager;
//    public VolumeUpManager(ControlCommand controlCommand){
//        super(controlCommand);
//        //audioManager = (AudioManager) getSystemSerive(Context.AUDIO_SERVICE);
//    }
//
//    @Override
//    protected void changeVolume(){
//        try{
//            if(currentVolume >= targetVolume){
//                // 제어자가 명령할 때에 체크된 내용이므로 발생할 경우 데이터가 손실된 것.
//                throw new Exception("예기치 못한 오류");
//            }
//            // TODO: 실제 볼륨조절을 한다.
//            //audioManager.adjustVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
//        }catch (Exception e){
//            Log.println(Log.ERROR,"Unexpected Error", Objects.requireNonNull(e.getMessage()));
//        }
//    }
//}
