package com.donghaeng.withme.login.connect.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.screen.start.connect.ControllerConnectFragment;
import com.donghaeng.withme.screen.start.connect.ControllerQrFragment;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@ExperimentalGetImage
public class QRCodeReader {
    private final static String TAG = "QRCodeReader";
    protected static final String[] REQUIRED_PERMISSIONS;

    static {
        REQUIRED_PERMISSIONS = new String[]{
                Manifest.permission.CAMERA
        };
    }

    private final Fragment mFragment;
    private final ControllerConnectFragment connectFragment;

    // 카메라
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;

    private final AtomicBoolean isDialogShowing = new AtomicBoolean(false);
    private final AtomicBoolean isScanning = new AtomicBoolean(false);
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    private final ControllerConnect mParent;
    private final DiscoveryHandler mHandler;

    public QRCodeReader(Fragment fragment, ControllerConnect parent) {
        this.mFragment = fragment;
        this.connectFragment = (ControllerConnectFragment) fragment.getParentFragment();
        requestPermissionLauncher =
                fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(fragment.getActivity(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        this.mParent = parent;
        this.mHandler = parent.getHandler();
    }

    public void setScanner() {
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(mFragment.requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showQrDialog(String qrContent) {
        if (!isDialogShowing.get() && mFragment.isAdded() && mFragment.getActivity() != null && !mFragment.getActivity().isFinishing()) {
            Log.d(TAG, "Showing dialog with QR content: " + qrContent);
            isDialogShowing.set(true);

            mFragment.getActivity().runOnUiThread(() -> {
                try {
                    new AlertDialog.Builder(mFragment.getActivity())
                            .setTitle("QR 코드 스캔 완료")
                            .setMessage("내용: " + qrContent)
                            .setPositiveButton("확인", (dialog, which) -> {
                                Log.d(TAG, "Dialog confirmed");
                                isDialogShowing.set(false);
                                // QR 스캔 관련 종료 로직
                                cleanupCamera();
                                connectFragment.changeFragment("info");
                            })
                            .setNegativeButton("취소", (dialog, which) -> {
                                Log.d(TAG, "Dialog cancelled");
                                isDialogShowing.set(false);
                                isScanning.set(true);
                            })
                            .setOnDismissListener(dialog -> {
                                Log.d(TAG, "Dialog dismissed");
                                isDialogShowing.set(false);
                                isScanning.set(true);
                            })
                            .create()
                            .show();
                } catch (Exception e) {
                    Log.e(TAG, "Error showing dialog: ", e);
                    isDialogShowing.set(false);
                    isScanning.set(true);
                }
            });
        }
    }

    @ExperimentalGetImage
    public void startCamera() {
        Log.d(TAG, "Starting camera");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(mFragment.requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Log.d(TAG, "Camera provider obtained");

                // 프리뷰 설정
                Preview preview = new Preview.Builder()
                        .build();
                PreviewView viewFinder = ((ControllerQrFragment) mFragment).getViewFinder();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                // 이미지 분석 설정
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                isScanning.set(true);
                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageProxy);

                // 후면 카메라 선택
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                try {
                    cameraProvider.unbindAll();
                    Log.d(TAG, "Previous use cases unbound");

                    cameraProvider.bindToLifecycle(
                            mFragment,
                            cameraSelector,
                            preview,
                            imageAnalysis
                    );
                    Log.d(TAG, "Use cases bound successfully");

                } catch (Exception e) {
                    Log.e(TAG, "Use case binding failed", e);
                }

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera provider failed", e);
                Toast.makeText(mFragment.getActivity(), "카메라 시작 실패", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(mFragment.requireContext()));
    }

    @ExperimentalGetImage
    private void processImageProxy(ImageProxy image) {
        if (!isScanning.get()) {
            image.close();
            return;
        }

        try {
            if (image.getImage() == null) {
                Log.w(TAG, "Skipping null image");
                image.close();
                return;
            }

            InputImage inputImage = InputImage.fromMediaImage(
                    image.getImage(),
                    image.getImageInfo().getRotationDegrees()
            );

            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        // QR 코드 읽기 성공 시
                        if (!barcodes.isEmpty()) {
                            Log.d(TAG, "QR codes detected: " + barcodes.size());
                            for (Barcode barcode : barcodes) {
                                String qrContent = barcode.getRawValue();
                                int format = barcode.getFormat();

                                Log.d(TAG, "Detected format: " + format + ", Content: " + qrContent);

                                if (qrContent != null && !isDialogShowing.get()) {
                                    isScanning.set(false);
                                    try {
                                        // 광고 중인 기기 검색 시작
                                        mHandler.startDiscovering(qrContent);
                                        if (!mHandler.isDiscovering) {
                                            // QR 스캔 관련 종료 로직
                                            cleanupCamera();
                                            connectFragment.changeFragment("info");
                                        }
                                        break;
                                    } catch (Exception e) {
                                        isScanning.set(true);
                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "QR scanning failed: ", e))
                    .addOnCompleteListener(task -> image.close());
        } catch (Exception e) {
            Log.e(TAG, "Error processing image: ", e);
            image.close();
        }
    }

    // 카메라 및 스캐너 정리를 위한 메서드 추가
    public void cleanupCamera() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }

        if (barcodeScanner != null) {
            barcodeScanner.close();
            barcodeScanner = null;
        }

        // ProcessCameraProvider 해제
        try {
            ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(mFragment.requireContext()).get();
            cameraProvider.unbindAll();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error cleaning up camera", e);
        }

        // DiscoveryHandler 검색 중지
        if (mHandler != null) {
            mHandler.stopDiscovering();
        }

        isScanning.set(false);
    }

    protected static String[] getRequiredPermissions() {
        return REQUIRED_PERMISSIONS;
    }
}
