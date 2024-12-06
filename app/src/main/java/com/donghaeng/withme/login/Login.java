package com.donghaeng.withme.login;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.donghaeng.withme.data.app.AutomaticLoginChecker;
import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.data.database.firestore.TokenManager;
import com.donghaeng.withme.data.database.room.user.UserRepository;
import com.donghaeng.withme.data.user.Undefined;
import com.donghaeng.withme.screen.start.StartActivity;
import com.donghaeng.withme.screen.start.login.LoginFragment;
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
    private String phoneNum;            //사용자의 전화번호
    private User user;
    private Fragment fragment;

    public Login() {
        // 로그인 생성, 사용x
    }

    public Login(Fragment fragment, String phoneNum) {
        // 로그인 생성, 확인할 전화번호
        this.fragment = fragment;
        this.phoneNum = phoneNum.replace("-","");
    }

    public interface Callback {
        void onResult(boolean result);
    }

    public void verifyUser(String passwd, Callback callback) {
        String hashedPhoneNum = EncrpytPhoneNumber.hashPhoneNumber(this.phoneNum);
        //firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.e("Login", "phoneNum: " + this.phoneNum);

        DocumentReference ref = db.collection("user").document(hashedPhoneNum);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // 문서가 존재할 경우
                String hashedPW = task.getResult().getString("hashedPW");
                if (hashedPW != null && BCrypt.checkpw(passwd, hashedPW)) {
                    //비밀번호가 문서에 존재하고, 일치할 경우
                    Log.e("Login", "hashedPW: " + hashedPW);

                    FireStoreManager fireStoreManager = FireStoreManager.getInstance();
                    fireStoreManager.changeInformation(user.getPhone(), "token", TokenManager.getInstance().getToken(), new FireStoreManager.firestoreCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            Log.e("Login", "Token successfully send to firestore");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("Login", "Token send failed");
                        }
                    });

                    // 유저 정보 다운로드
                    Map<String, Object> userData;
                    if ((userData = task.getResult().getData()) != null) {
                        String name = Objects.requireNonNull(userData.get("name")).toString();
                        String phone = Objects.requireNonNull(userData.get("phoneNum")).toString();
                        String uid = Objects.requireNonNull(userData.get("uid")).toString();
                        String hashedPassword = Objects.requireNonNull(userData.get("hashedPW")).toString();
                        byte userType = Byte.parseByte(Objects.requireNonNull(userData.get("userType")).toString());

                        switch (userType) {
                            case UserType.CONTROLLER:
                                user = new Controller(name, phone, uid, hashedPassword);
                                for (Map<String, Object> targetData : (List<Map<String, Object>>) Objects.requireNonNull(userData.get("targets"))) {
                                    String targetName = Objects.requireNonNull(targetData.get("name")).toString();
                                    String targetPhone = Objects.requireNonNull(targetData.get("phoneNum")).toString();
                                    String targetUid = Objects.requireNonNull(targetData.get("uid")).toString();
                                    String targetToken = Objects.requireNonNull(targetData.get("token")).toString();
                                    Target target = new Target(targetName, targetPhone, targetUid, "");
                                    target.addToken(targetToken);
                                    ((Controller) user).addTarget(target);
                                }
                                break;
                            case UserType.TARGET:
                                user = new Target(name, phone, uid, hashedPassword);
                                Map<String, Object> controllerData = (Map<String, Object>) userData.get("controller");
                                String controllerName = Objects.requireNonNull(Objects.requireNonNull(controllerData).get("name")).toString();
                                String controllerPhone = Objects.requireNonNull(controllerData.get("phoneNum")).toString();
                                String controllerUid = Objects.requireNonNull(controllerData.get("uid")).toString();
                                String controllerToken = Objects.requireNonNull(controllerData.get("token")).toString();
                                Controller controller = new Controller(controllerName, controllerPhone, controllerUid, "");
                                controller.addToken(controllerToken);
                                ((Target) user).addController(controller);
                                break;
                            case UserType.UNDEFINED:
                                user = new Undefined(name, phone, uid, hashedPassword);
                                break;
                            default:
                                user = null;
                                break;
                        }
                        if (user != null) {
                            UserRepository repository = new UserRepository(fragment.requireContext());
                            repository.deleteAllUsers();  // 기존 데이터 모두 삭제
                            if (user.getUserType() == UserType.CONTROLLER) {
                                for (Target target : ((Controller) user).getTargets()) {
                                    repository.insertOrUpdate(target);  // REPLACE 전략 사용
                                }
                                ((StartActivity)fragment.requireActivity()).setUser(user);
                                ((StartActivity)fragment.requireActivity()).changeFragment("controller");
                            }  else if (user.getUserType() == UserType.TARGET) {
                                repository.insertOrUpdate(((Target) user).getController());  // REPLACE 전략 사용
                                ((StartActivity)fragment.requireActivity()).setUser(user);
                                ((StartActivity)fragment.requireActivity()).changeFragment("target");
                            } else if (user.getUserType() == UserType.UNDEFINED) {
                                ((StartActivity)fragment.requireActivity()).setUser(user);
                                ((StartActivity)fragment.requireActivity()).changeFragment("SelectFragment");
                            }
                            if (((LoginFragment) fragment).getCheckBox().isChecked()) {
                                Log.e("Login", "체크 박스 눌림 확인됨");
                                AutomaticLoginChecker.setEnable(fragment.requireContext(), user); // User 객체와 함께 호출
                                fireStoreManager.changeInformation(user.getPhone(), "token", TokenManager.getInstance().getToken(), new FireStoreManager.firestoreCallback() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        Log.e("Login", "Token successfully send to firestore");
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("Login", "Token send failed");
                                    }
                                });
                            } else {
                                Log.e("Login", "체크 박스 눌리지 않음");
                                AutomaticLoginChecker.setDisable(fragment.requireContext());
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