package com.donghaeng.withme.featurelist;

public class ControlCommandList extends FeatureList{
    // 제어 명령어 리스트
    public static final byte VOLUME_DOWN = 0;
    public static final byte VOLUME_UP = 1;
    public static final byte MUTE = 2;
    public static final byte UNMUTE = 3;

    public static final byte BRIGHTNESS_DOWN = 4;
    public static final byte BRIGHTNESS_UP = 5;

    // public static final byte FULL_SCREEN_REMOTE = 6;

    // 기능 추가시 아래에 추가
    // -128 ~ 127 사이의 값만 사용 가능
}