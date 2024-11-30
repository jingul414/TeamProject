package com.donghaeng.withme.login.connect.target;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

import com.donghaeng.withme.screen.start.connect.TargetQrFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class QRCodeGenerator {
    private final Fragment mFragment;
    private final AdvertisementHandler mHandler;

    public QRCodeGenerator(Fragment fragment, TargetConnect parent) {
        mFragment = fragment;
        mHandler = parent.getHandler();
    }

    public void generateQRCode() {
        byte[] array = new byte[20];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8).trim();

        if (mHandler != null) {
            mHandler.setData(generatedString);
            String data = BCrypt.hashpw(generatedString, BCrypt.gensalt());

            try {
                Bitmap bitmap = createQRCode(data);
                ((TargetQrFragment)mFragment).getQrCodeImageView().setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
                Log.e("QRCodeGenerator", "Error generating QR code", e);
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
