package com.example.escooter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

            bitmap = resizeBitmap(bitmap, 500);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream); // 壓縮品質設置為80%

            byte[] imageBytes = outputStream.toByteArray();

            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert URI to Base64", e);
        }
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, int maxSideLength) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > height) {
            width = maxSideLength;
            height = Math.round(maxSideLength / aspectRatio);
        } else {
            height = maxSideLength;
            width = Math.round(maxSideLength * aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static Uri convertBase64ToUri(Context context, String base64String) {

        if(base64String == null || base64String.isEmpty()){
            return null;
        }

        System.out.println("hello" + base64String);

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