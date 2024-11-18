package com.donghaeng.withme.roomdatabase.guide;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GuideBook.class}, version = 1, exportSchema = false)
public abstract class GuideBookDatabase extends RoomDatabase {

    private static GuideBookDatabase instance;

    public abstract GuideBookDao guideBookDao();

    // 싱글톤 패턴을 사용하여 데이터베이스 인스턴스 생성
    public static synchronized GuideBookDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            GuideBookDatabase.class, "guide_book_database")
                    .fallbackToDestructiveMigration() // 데이터베이스 버전 변경 시 기존 데이터 삭제
                    .build();
        }
        return instance;
    }
}