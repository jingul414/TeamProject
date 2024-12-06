package com.donghaeng.withme.data.database.firestore;

import android.util.Log;

import com.donghaeng.withme.security.EncrpytPhoneNumber;
import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireStoreManager {
    private String hashedPW, uid, name, phone;
    private Byte userType;
    private static FireStoreManager instance;
    private final FirebaseFirestore db;

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

    //firestore 에서 유저 데이터를 받아오는 메소드, 전화번호를 받아서 정보를 받아옴, 이 메소드 실행 후 변수에 이 클래스의 getter 호출
    public void getUserData(String phoneNumber) {
        String hashedPhoneNumber = EncrpytPhoneNumber.hashPhoneNumber(phoneNumber);   //전화번호 해시화해서 저장
        db.collection("user")
                .document(hashedPhoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot docSnapshot = task.getResult();
                        if(docSnapshot.exists()){
                            Map<String, Object> data = docSnapshot.getData();
                            this.hashedPW = data.get("hashedPW").toString();
                            this.name = data.get("name").toString();
                            this.phone = data.get("phoneNum").toString();
                            this.uid = data.get("uid").toString();
                            this.userType = data.get("userType").toString().getBytes()[0];
                            Log.e("Firestore db", "데이터 받아오기 성공");
                        }
                    }else{
                        Log.e("Firestore db", "해당 이름의 문서 없음");
                    }
                });
    }

    //firestore 에 유저의 정보를 넣는 역할을 하는 메소드, 파라미터 User 객체의 정보를 firestore 에 입력, 각 유저 문서의 이름은 해시화한 전화번호로 함
    public void setUserData(User usr, firestoreCallback callback){
        Map<String, Object> user = processUserInfo(usr);

        String hashedPhoneNumber = EncrpytPhoneNumber.hashPhoneNumber(usr.getPhone());   //전화번호 해시화해서 저장 -> 로그인을 위해 각 유저의 firestore 문서의 이름으로 사용

        db.collection("user")
                .document(hashedPhoneNumber)
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User data saved successfully"))
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUserData(User user){
        Map<String, Object> userData = processUserInfo(user);

        db.collection("user").document(EncrpytPhoneNumber.hashPhoneNumber(user.getPhone()))
//                .set(userData, SetOptions.merge());
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.e("Firestore db", "데이터 업데이트 성공"))
                .addOnFailureListener(e -> Log.e("Firestore db", "데이터 업데이트 실패"));
    }

    private Map<String, Object> processUserInfo(User user){
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("phoneNum", user.getPhone());
        userData.put("hashedPW", user.getHashedPassword());
        userData.put("uid", user.getId());
        long userType = user.getUserType();
        userData.put("userType", userType);
        userData.put("token", user.getToken());

        if(userType == UserType.CONTROLLER){
            List<Map<String, Object>> targetsData = new ArrayList<>();
            for(Target target : ((Controller)user).getTargets()){
                Map<String, Object> targetData = new HashMap<>();
                targetData.put("name", target.getName());
                targetData.put("phoneNum", target.getPhone());
                targetData.put("uid", target.getId());
                targetData.put("token", target.getToken());
                targetsData.add(targetData);
            }
            userData.put("targets", targetsData);
        }else if(userType == UserType.TARGET){
            Map<String, Object> controllerData = new HashMap<>();
            Controller controller = ((Target)user).getController();
            controllerData.put("name", controller.getName());
            controllerData.put("phoneNum", controller.getPhone());
            controllerData.put("uid", controller.getId());
            controllerData.put("token", controller.getToken());
            userData.put("controller", controllerData);
        }

        return userData;
    }

    //TODO : 아래 3개 메소드 테스트 미실시상태.
    //이름, 비번 공통 변경 메소드? , 전화번호, 바꿀것, 비밀번호냐 이름이냐 결정, 콜백
    public void changeInformation(String phoneNum, String changeMode, String changed, firestoreCallback callback){
        String hashedPhoneNumber = EncrpytPhoneNumber.hashPhoneNumber(phoneNum);    //전화번호 해시
        Map<String, Object> data = new HashMap<>();
        switch (changeMode) {
            case "password":
                data.put("hashedPW", changed);
                break;
            case "name":
                data.put("name", changed);
                break;
            case "token":
                data.put("token", changed);
                break;
        }
        db.collection("user")
                .document(hashedPhoneNumber)
                .update(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User " + changeMode + " changed successfully"))
                .addOnFailureListener(callback::onFailure);
    }

    //전화번호, 평문 비밀번호, 콜백을 받아서 전화번호에 해당하는 문서의 비밀번호 변경
    public void changePW(String phoneNumber, String passwd, firestoreCallback callback){
        String hashedPhoneNumber = EncrpytPhoneNumber.hashPhoneNumber(phoneNumber);
        String hashedPassword = BCrypt.hashpw(passwd, BCrypt.gensalt());            //비밀번호 해시
        db.collection("user")
                .document(hashedPhoneNumber)
                .update("hashedPW", hashedPassword)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User password changed successfully"))
                .addOnFailureListener(callback::onFailure);
    }

    //전화번호, 이름, 콜백을 받아서 전화번호에 해당하는 문서의 이름 변경
    public void changeName(String phoneNumber, String name, firestoreCallback callback) {
        String hashedPhoneNumber = EncrpytPhoneNumber.hashPhoneNumber(phoneNumber);
        db.collection("user")
                .document(hashedPhoneNumber)
                .update("name", name)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User name changed successfully"))
                .addOnFailureListener(callback::onFailure);
    }

    // 전화번호 변경 메소드
    public void changePhoneNumber(String oldPhoneNum, String newPhoneNum, User user, firestoreCallback callback) {
        String oldHashedPhoneNum = EncrpytPhoneNumber.hashPhoneNumber(oldPhoneNum);
        String newHashedPhoneNum = EncrpytPhoneNumber.hashPhoneNumber(newPhoneNum);

        // 현재 데이터를 가져와서 새 문서에 저장
        db.collection("user")
                .document(oldHashedPhoneNum)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // 기존 데이터 모두 가져오기
                        Map<String, Object> existingData = task.getResult().getData();
                        // 새 전화번호만 업데이트
                        existingData.put("phoneNum", newPhoneNum);

                        // 새 문서에 데이터 저장
                        db.collection("user")
                                .document(newHashedPhoneNum)
                                .set(existingData)
                                .addOnSuccessListener(aVoid -> {
                                    // 성공적으로 새 문서를 만들었으면 이전 문서 삭제
                                    db.collection("user")
                                            .document(oldHashedPhoneNum)
                                            .delete()
                                            .addOnSuccessListener(aVoid2 -> callback.onSuccess("Phone number changed successfully"))
                                            .addOnFailureListener(callback::onFailure);
                                })
                                .addOnFailureListener(callback::onFailure);
                    } else {
                        callback.onFailure(new Exception("Original document not found"));
                    }
                });
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

    public Byte getUserType(){
        return userType;
    }
}