package com.donghaeng.withme.data.database.room.guide;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.donghaeng.withme.data.guide.GuideBook;

import java.util.List;

@Dao
public interface GuideBookDao {

    // 새로운 GuideBook 삽입
    @Insert
    void insert(GuideBook guideBook);

    // 기존 GuideBook 업데이트
    @Update
    void update(GuideBook guideBook);

    // 특정 GuideBook 삭제
    @Delete
    void delete(GuideBook guideBook);

    // 모든 GuideBook 가져오기
    @Query("SELECT * FROM GuideBook ORDER BY date DESC")
    List<GuideBook> getAllGuides();

    // 특정 ID로 GuideBook 가져오기
    @Query("SELECT * FROM GuideBook WHERE `index` = :guideIndex")
    GuideBook getGuideById(int guideIndex);

    // 특정 제목으로 GuideBook 검색
    @Query("SELECT * FROM GuideBook WHERE title LIKE '%' || :title || '%'")
    List<GuideBook> searchGuidesByTitle(String title);

    @Query("SELECT * FROM GuideBook WHERE type = :type")
    List<GuideBook> getGuidesByType(String type);

    // 모든 GuideBook 삭제
    @Query("DELETE FROM GuideBook")
    void deleteAllGuides();
}
