package com.donghaeng.withme.data.database.room.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.donghaeng.withme.data.user.User;

import java.util.List;

@Dao
public interface UserDao {

    // 새로운 User 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    // 기존 User 업데이트
    @Update
    void update(User user);

    // 새로운 User 삽입 하거나 업데이트
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(User user);

    // 특정 User 삭제
    @Delete
    void delete(User user);

    // 모든 User 가져오기
    @Query("SELECT * FROM User")
    List<User> getAllUsers();

    // 특정 ID로 User 가져오기
    @Query("SELECT * FROM User WHERE id = :userId")
    User getUserById(String userId);

    // 모든 User 삭제
    @Query("DELETE FROM User")
    void deleteAllUsers();

    // 특정 ID로 User 삭제
    @Query("DELETE FROM User WHERE id = :userId")
    void deleteUserById(String userId);
}
