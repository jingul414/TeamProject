package com.donghaeng.withme.screen.start.connect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.donghaeng.withme.data.app.AutomaticLoginChecker;
import com.donghaeng.withme.data.database.firestore.FireStoreManager;
import com.donghaeng.withme.login.connect.LocalConfirmationStatus;
import com.donghaeng.withme.login.connect.controller.ControllerConnect;
import com.donghaeng.withme.login.connect.controller.DiscoveryHandler;
import com.donghaeng.withme.screen.main.ControllerActivity;
import com.donghaeng.withme.data.user.Controller;
import com.donghaeng.withme.data.user.Target;
import com.donghaeng.withme.data.user.User;
import com.donghaeng.withme.data.user.UserType;

@ExperimentalGetImage
public class ControllerQrFragment extends Fragment {
    private View loadingContainer;
    private PreviewView viewFinder;
    private ControllerConnect connect;
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    private static final String ARG_USER = "user";
    private User user;

    public ControllerQrFragment() {
        // Required empty public constructor
    }

    public static ControllerQrFragment newInstance(User user) {
        ControllerQrFragment fragment = new ControllerQrFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ControllerConnect 초기화
        connect = new ControllerConnect(this);
        connect.checkPermissions();
        connect.getReader().setScanner();
        return inflater.inflate(R.layout.fragment_controller_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFinder = view.findViewById(R.id.viewFinder);
        viewFinder.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);
        loadingContainer = view.findViewById(R.id.loadingContainer);

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
                        connect.getReader().startCamera();
                    } else {
                        Toast.makeText(requireContext(), "필요한 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // 로딩 UI 표시/숨김 메서드 추가
    public void showLoading() {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (loadingContainer != null) {
            loadingContainer.setVisibility(View.GONE);
        }
    }

    // 연결 실패 시 호출될 메서드
    public void onConnectionFailed() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                hideLoading();
                Toast.makeText(requireContext(), "연결에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                // QR 스캔 다시 시작
                connect.getReader().setScanner();
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public PreviewView getViewFinder() {
        return viewFinder;
    }

    public User getUser() {
        return user;
    }

    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher() {
        return requestPermissionsLauncher;
    }

    public ControllerConnect getConnect() {
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

            /* Discovery 종료 */
            DiscoveryHandler handler = DiscoveryHandler.getInstance();
            handler.clear();
            AutomaticLoginChecker.setDisable(requireContext());
            ((ControllerActivity) requireActivity()).onConnectionComplete();
        }
    }
}
