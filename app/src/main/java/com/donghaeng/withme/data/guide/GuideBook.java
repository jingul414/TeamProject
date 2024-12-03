package com.donghaeng.withme.data.guide;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GuideBook {
    @PrimaryKey(autoGenerate = true)
    private int index;

    private String title;
    private String date;

    // 본문 내용을 JSON 형태로 저장
    private String contentJson; // 텍스트와 이미지 리스트를 JSON 형식으로 저장

    private String type;

    // 기본 생성자 (필수)
    public GuideBook() {}

    // 모든 필드를 초기화하는 생성자
    public GuideBook(String title, String date, String contentJson, String type) {
        this.title = title;
        this.date = date;
        this.contentJson = contentJson;
        this.type = type;
    }

    // Getter, Setter
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}