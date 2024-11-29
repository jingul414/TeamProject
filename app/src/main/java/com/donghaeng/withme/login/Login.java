package com.donghaeng.withme.login;

import android.util.Log;
import android.widget.Toast;

import com.donghaeng.withme.firebasestore.FireStoreManager;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.security.EncrpytPhoneNumber;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.mindrot.jbcrypt.BCrypt;

// 로그인
public class Login {
    private FirebaseFirestore db;       //firestore
    private String phoneNum;            //사용자의 전화번호

    public Login(){
        // 로그인 생성, 사용x
    }

    public Login(String phoneNum){
        // 로그인 생성, 확인할 전화번호
        this.phoneNum = phoneNum;
    }

    public interface Callback{
        void onResult(boolean result);
    }

    public void verifyUser(String passwd, Callback callback){
        String hashedPhoneNum = EncrpytPhoneNumber.hashPhoneNumber(this.phoneNum);
        db = FirebaseFirestore.getInstance();
        Log.e("Login", "phoneNum: "+this.phoneNum);
        db.collection("controller").document(hashedPhoneNum).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // 문서가 존재할 경우
                String hashedPW = task.getResult().getString("hashedPw");
                if (hashedPW != null && BCrypt.checkpw(passwd, hashedPW)) {
                    //비밀번호가 문서에 존재하고, 일치할 경우
                    Log.e("Login", "hashedPW: " + hashedPW);
                    callback.onResult(true);
                }else{
                    //비밀번호가 존재하지 않거나, 일치하지 않을 경우
                    Log.e("Login", "hashedPW is null or password mismatch");
                    callback.onResult(false);
                }
            }else {
                // 문서가 없거나 Firestore 작업 실패
                Log.e("Login", "hashedPhoneNum에 해당하는 문서가 존재하지 않음");
                callback.onResult(false);
            }
        });
    }
}