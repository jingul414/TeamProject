package com.donghaeng.withme.data.database.room.user;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.donghaeng.withme.data.user.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {
        UserDatabase db = UserDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(User user){
        executorService.execute(() -> {
            User existingUser = userDao.getUserById(user.getId());
            if (existingUser == null) {
                userDao.insert(user);
            } else {
                Log.e("UserRepository", "중복된 User 삽입 시도: " + user.getId());
            }
        });
    }

    public void insertOrUpdate(User user){
        executorService.execute(() -> userDao.insertOrUpdate(user));
    }

    public void update(User user){
        executorService.execute(() -> userDao.update(user));
    }

    public void delete(User user){
        executorService.execute(() -> userDao.delete(user));
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAllUsers(Callback<List<User>> callback) {
        executorService.execute(() -> {
            List<User> users = userDao.getAllUsers();
            new Handler(Looper.getMainLooper()).post(() -> callback.onResult(users));
        });
    }

    public User getUserById(String userId){
        return userDao.getUserById(userId);
    }

    public void deleteAllUsers(){
        executorService.execute(() -> userDao.deleteAllUsers());
    }

    public void deleteUserById(String userId){
        executorService.execute(() -> userDao.deleteUserById(userId));
    }
}
