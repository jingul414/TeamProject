package com.donghaeng.withme.screen.start.connect;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.graphics.Color;

public class TargetQrFragment extends Fragment {
    private ImageView qrCodeImageView;

    public TargetQrFragment() {
        // Required empty public constructor
    }

    public static TargetQrFragment newInstance() {
        return new TargetQrFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target_qr, container, false);

        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);

        generateQRCode();
        return view;
    }

    private void generateQRCode() {
        String data = "test";

        try {
            Bitmap bitmap = createQRCode(data);
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap createQRCode(String data) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    500, 500, null
            );
        } catch (IllegalArgumentException e) {
            return null;
        }

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }
}