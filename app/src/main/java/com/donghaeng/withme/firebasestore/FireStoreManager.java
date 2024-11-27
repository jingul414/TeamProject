package com.donghaeng.withme.firebasestore;

import android.util.Log;

import com.donghaeng.withme.security.EncrpytPhoneNumber;
import com.donghaeng.withme.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireStoreManager {
    private String hashedPW, uid, name, phone;
    private static FireStoreManager instance;
    private final FirebaseFirestore db;
    CollectionReference docRef;

    //싱글톤으로 구현, 생성자
    private FireStoreManager(){
        db = FirebaseFirestore.getInstance();
    }

    //인스턴스 반환 메소드
    public static synchronized FireStoreManager getInstance() {
        if (instance == null) {
            instance = new FireStoreManager();
        }
        return instance;
    }

    //성공 실패 콜백
    public interface firestoreCallback {
        void onSuccess(Object result);
        void onFailure(Exception e);
    }

    //firestore 에서 유저 데이터를 받아오는 메소드
    public void getUserData() {
        db.collection("controller").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                name = snapshot.getString("name");
                phone = snapshot.getString("phoneNumber");
                hashedPW = snapshot.getString("hashedPw");
                uid = snapshot.getString("uid");
                Log.e("firestoreManager", "task successful");
            }else{
                Log.e("firestoreManager", "task failed");
            }
        });

    }

    //firestore 에 유저의 정보를 넣는 역할을 하는 메소드, 파라미터 User 객체의 정보를 firestore 에 입력, 각 유저 문서의 이름은 해시화한 전화번호로 함
    public void setUserData(User usr, firestoreCallback callback){
        Map<String, Object> user = new HashMap<>();
        user.put("name", usr.getName());
        user.put("phoneNumber", usr.getPhone());
        user.put("hashedPw", usr.getHashedPassword());
        user.put("uid", usr.getId());

        String hashedPhoneNumber = EncrpytPhoneNumber.hashPhoneNumber(usr.getPhone());   //전화번호 해시화해서 저장 -> 로그인을 위해 각 유저의 firestore 문서의 이름으로 사용

        db.collection("controller")
                .document(hashedPhoneNumber)
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User data saved successfully"))
                .addOnFailureListener(callback::onFailure);
    }

    public String getHashedPW() {
        return hashedPW;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUid() {
        return uid;
    }

}