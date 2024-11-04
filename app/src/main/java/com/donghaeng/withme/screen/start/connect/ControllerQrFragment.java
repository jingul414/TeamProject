package com.donghaeng.withme.screen.start.connect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
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
public class ControllerQrFragment extends Fragment {
    private static final String TAG = "ControllerQrFragment";

    private ControllerConnectFragment connectFragment;

    private PreviewView viewFinder;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private AtomicBoolean isDialogShowing = new AtomicBoolean(false);
    private AtomicBoolean isScanning = new AtomicBoolean(false);

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_controller_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        connectFragment = (ControllerConnectFragment) getParentFragment();

        viewFinder = view.findViewById(R.id.viewFinder);
        viewFinder.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);

        // QR 코드만을 위한 스캐너 설정
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        scanner = BarcodeScanning.getClient(options);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void showQrDialog(String qrContent) {
        if (!isDialogShowing.get() && isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            Log.d(TAG, "Showing dialog with QR content: " + qrContent);
            isDialogShowing.set(true);

            getActivity().runOnUiThread(() -> {
                try {
                    new AlertDialog.Builder(requireContext())
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

            scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            Log.d(TAG, "QR codes detected: " + barcodes.size());
                            for (Barcode barcode : barcodes) {
                                String qrContent = barcode.getRawValue();
                                int format = barcode.getFormat();

                                Log.d(TAG, "Detected format: " + format + ", Content: " + qrContent);

                                if (qrContent != null && !isDialogShowing.get()) {
                                    isScanning.set(false);
                                    showQrDialog(qrContent);
                                    break;
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

    @ExperimentalGetImage
    private void startCamera() {
        Log.d(TAG, "Starting camera");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Log.d(TAG, "Camera provider obtained");

                // 프리뷰 설정
                Preview preview = new Preview.Builder()
                        .build();
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
                            this,
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
                Toast.makeText(requireContext(), "카메라 시작 실패", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        cleanupCamera();
    }

    // 카메라 및 스캐너 정리를 위한 메서드 추가
    private void cleanupCamera() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }

        if (scanner != null) {
            scanner.close();
            scanner = null;
        }

        // ProcessCameraProvider 해제
        try {
            ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(requireContext()).get();
            cameraProvider.unbindAll();
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error cleaning up camera", e);
        }

        isScanning.set(false);
    }
}