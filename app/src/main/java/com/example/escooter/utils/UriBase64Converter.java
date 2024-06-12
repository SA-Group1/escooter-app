package com.example.escooter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UriBase64Converter {

    public static String convertUriToBase64(Context context, Uri uri) throws Exception {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("InputStream is null");
            }

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // 檢查並修正圖片旋轉
            bitmap = rotateBitmapIfNeeded(context, uri, bitmap);

            bitmap = resizeBitmap(bitmap);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream); // 壓縮品質設置為80%

            byte[] imageBytes = outputStream.toByteArray();

            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert URI to Base64", e);
        }
    }

    private static Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > height) {
            width = 500;
            height = Math.round(500 / aspectRatio);
        } else {
            height = 500;
            width = Math.round(500 * aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private static Bitmap rotateBitmapIfNeeded(Context context, Uri uri, Bitmap bitmap) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(inputStream);

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        inputStream.close();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateBitmap(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateBitmap(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateBitmap(bitmap, 270);
            default:
                return bitmap;
        }
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Uri convertBase64ToUri(Context context, String base64String) {

        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

            File file = new File(context.getCacheDir(), "photo");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decodedBytes);
            fos.close();

            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}