package com.donghaeng.withme.roomdatabase.guide;

public class GuideContent {
    private String type; // "text" 또는 "image"
    private String value; // 텍스트 내용 또는 이미지 경로

    // 생성자
    public GuideContent(String type, String value) {
        this.type = type;
        this.value = value;
    }

    // Getter, Setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /* 형식 예시 */
//    [
//    { "type": "text", "value": "안녕하세요, 이 앱에 대한 설명입니다." },
//    { "type": "image", "value": "file://storage/emulated/0/Pictures/example.png" },
//    { "type": "text", "value": "다음으로 진행할 내용은..." }
//    ]
}