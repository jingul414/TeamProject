package com.donghaeng.withme.login;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.security.EncrpytPhoneNumber;
import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// 로그인
public class Login {
    private FirebaseFirestore db;       //firestore
    private String phoneNum;            //사용자의 전화번호
    private User user;
    private Fragment fragment;

    public Login() {
        // 로그인 생성, 사용x
    }

    public Login(Fragment fragment, String phoneNum) {
        // 로그인 생성, 확인할 전화번호
        this.fragment = fragment;
        this.phoneNum = phoneNum;
    }

    public interface Callback {
        void onResult(boolean result);
    }

    public void verifyUser(String passwd, Callback callback) {
        String hashedPhoneNum = EncrpytPhoneNumber.hashPhoneNumber(this.phoneNum);
        db = FirebaseFirestore.getInstance();
        Log.e("Login", "phoneNum: " + this.phoneNum);

        DocumentReference ref = db.collection("user").document(hashedPhoneNum);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // 문서가 존재할 경우
                String hashedPW = task.getResult().getString("hashedPW");
                if (hashedPW != null && BCrypt.checkpw(passwd, hashedPW)) {
                    //비밀번호가 문서에 존재하고, 일치할 경우
                    Log.e("Login", "hashedPW: " + hashedPW);
                    // 유저 정보 다운로드
                    Map<String, Object> userData;
                    if ((userData = task.getResult().getData()) != null) {
                        String name = Objects.requireNonNull(userData.get("name")).toString();
                        String phone = Objects.requireNonNull(userData.get("phoneNum")).toString();
                        String uid = Objects.requireNonNull(userData.get("uid")).toString();
                        String hashedPassword = Objects.requireNonNull(userData.get("hashedPW")).toString();
                        byte userType = Objects.requireNonNull(userData.get("userType")).toString().getBytes()[0];

                        switch (userType) {
                            case UserType.CONTROLLER:
                                user = new Controller(name, phone, uid, hashedPassword);
                                for (Map<String, Object> targetData : (List<Map<String, Object>>) Objects.requireNonNull(userData.get("targets"))) {
                                    String targetName = Objects.requireNonNull(targetData.get("name")).toString();
                                    String targetPhone = Objects.requireNonNull(targetData.get("phoneNum")).toString();
                                    String targetUid = Objects.requireNonNull(targetData.get("uid")).toString();
                                    Target target = new Target(targetName, targetPhone, targetUid, "");
                                    ((Controller) user).addTarget(target);
                                }
                                break;
                            case UserType.TARGET:
                                user = new Target(name, phone, uid, hashedPassword);
                                Map<String, Object> controllerData = (Map<String, Object>) userData.get("controller");
                                String controllerName = Objects.requireNonNull(Objects.requireNonNull(controllerData).get("name")).toString();
                                String controllerPhone = Objects.requireNonNull(controllerData.get("phoneNum")).toString();
                                String controllerUid = Objects.requireNonNull(controllerData.get("uid")).toString();
                                Controller controller = new Controller(controllerName, controllerPhone, controllerUid, "");
                                ((Target) user).addController(controller);
                                break;
                            default:
                                user = null;
                                break;
                        }
                        if (user != null) {
                            UserRepository repository = new UserRepository(fragment.requireContext());
                            if (user.getUserType() == UserType.CONTROLLER) {
                                for (Target target : ((Controller) user).getTargets()) {
                                    repository.insert(target);
                                }
                                ((StartActivity)fragment.requireActivity()).changeFragment("controller");

                            } else {
                                repository.insert(((Target) user).getController());
                                ((StartActivity)fragment.requireActivity()).changeFragment("target");
                            }
                        }
                    }
                    callback.onResult(true);
                } else {
                    //비밀번호가 존재하지 않거나, 일치하지 않을 경우
                    Log.e("Login", "hashedPW is null or password mismatch");
                    callback.onResult(false);
                }
            } else {
                // 문서가 없거나 Firestore 작업 실패
                Log.e("Login", "hashedPhoneNum에 해당하는 문서가 존재하지 않음");
                callback.onResult(false);
            }
        });
    }
}