package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.login.connect.LocalConfirmationStatus;
import com.donghaeng.withme.login.connect.target.AdvertisementHandler;
import com.donghaeng.withme.login.connect.target.TargetConnect;
import com.donghaeng.withme.screen.main.TargetActivity;
import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;

/*  피제어자 => QR 생성  */
public class TargetQrFragment extends Fragment {
    private ImageView qrCodeImageView;
    private TargetConnect connect;
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    /**
     * Fragment 생성자 데이터
     */
    private static final String ARG_USER = "user";
    private User user;

    public TargetQrFragment() {
        // Required empty public constructor
    }

    public static TargetQrFragment newInstance(User user) {
        TargetQrFragment fragment = new TargetQrFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_qr, container, false);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);

        // ControllerConnect 초기화
        connect = new TargetConnect(this);
        connect.getGenerator().generateQRCode();
        connect.checkPermissions();
        connect.getHandler().setAdvertiser();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Fragment가 처음 생성될 때 호출
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
        requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (String permission : result.keySet()) {
                        if (Boolean.FALSE.equals(result.get(permission))) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        Toast.makeText(requireContext(), "모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "필요한 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Fragment가 사용자에게 보이게 되었을 때 호출
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public ImageView getQrCodeImageView() {
        return qrCodeImageView;
    }

    public User getUser() {
        return user;
    }

    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher() {
        return requestPermissionsLauncher;
    }

    public TargetConnect getConnect() {
        return connect;
    }

    public void checkAndProceed(User opponent) {
        boolean bothConfirmed = LocalConfirmationStatus.isConfirmed(user.getId()) &&
                LocalConfirmationStatus.isConfirmed(opponent.getId());

        if (bothConfirmed) {
            // User 정보 firestore에 저장
            User undefinedUser = getUser();
            if (opponent.getUserType() == UserType.CONTROLLER) {
                user = new Target(undefinedUser.getName(), undefinedUser.getPhone(), undefinedUser.getId(), undefinedUser.getHashedPassword());
                ((Target) user).addController((Controller) opponent);
            } else if (opponent.getUserType() == UserType.TARGET) {
                user = new Controller(undefinedUser.getName(), undefinedUser.getPhone(), undefinedUser.getId(), undefinedUser.getHashedPassword());
                ((Controller) user).addTarget((Target) opponent);
            }
            FireStoreManager fireStoreManager = FireStoreManager.getInstance();
            fireStoreManager.updateUserData(user);

            /* Advertiser 종료 */
            AdvertisementHandler handler = AdvertisementHandler.getInstance();
            handler.clear();

            ((TargetActivity) requireActivity()).onConnectionComplete();
        }
    }
}