package com.donghaeng.withme.data.database.room.user;

import android.content.Context;

import com.donghaeng.withme.data.user.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private UserDao userDao;
    private ExecutorService executorService;

    public UserRepository(Context context) {
        UserDatabase db = UserDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(User user){
        executorService.execute(() -> userDao.insert(user));
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

    public List<User> getAllUsers(){
        return userDao.getAllUsers();
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
