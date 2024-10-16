package com.donghaeng.withme.featurelist;

public class ControlCommandList extends FeatureList{
    // 제어 명령어 리스트
    public static final byte CALL_VOLUME_DOWN = 0;      // 벨소리 음량 낮추기
    public static final byte CALL_VOLUME_UP = 1;        // 벨소리 음량 높이기
    public static final byte CALL_SILENT = 2;           // 벨소리 음소거
    public static final byte CALL_NORMAL = 3;           // 벨소리 음소거 해제
    public static final byte CALL_VIBRATE = 4;          // 벨소리 진동
    public static final byte MEDIA_VOLUME_DOWN = 5;     // 미디어 음량 낮추기
    public static final byte MEDIA_VOLUME_UP = 6;       // 미디어 음량 높이기
    public static final byte MEDIA_VOLUME_MUTE = 7;     // 미디어 음소거
    public static final byte MEDIA_VOLUME_UNMUTE = 8;   // 미디어 음소거 해제

    public static final byte BRIGHTNESS_DOWN = 9;       // 화면 밝기 낮추기
    public static final byte BRIGHTNESS_UP = 10;        // 화면 밝기 높이기

    // public static final byte FULL_SCREEN_REMOTE = 6;

    // 기능 추가시 아래에 추가
    // -128 ~ 127 사이의 값만 사용 가능
}