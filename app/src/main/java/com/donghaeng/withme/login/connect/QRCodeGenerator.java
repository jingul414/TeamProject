package com.donghaeng.withme.login.connect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class QRCodeGenerator {
    private final Context mContext;
    private AdvertisementHandler mAdvertisementHandler;

    public QRCodeGenerator(Fragment fragment, AdvertisementHandler advertisementHandler) {
        mContext = fragment.requireContext();
        mAdvertisementHandler = advertisementHandler;
    }

    public void generateQRCode(ImageView qrCodeImageView) {
        byte[] array = new byte[20];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8).trim();

        if (mAdvertisementHandler != null) {
            mAdvertisementHandler.setData(generatedString);
            String data = BCrypt.hashpw(generatedString, BCrypt.gensalt());

            try {
                Bitmap bitmap = createQRCode(data);
                qrCodeImageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
                Log.e("QRCodeGenerator", "Error generating QR code", e);
                Toast.makeText(mContext, "Error generating QR code", Toast.LENGTH_SHORT).show();
            }
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
