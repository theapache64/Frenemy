package com.theah64.frenemy.android.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by theapache64 on 10/6/17.
 */

public class BitmapUtils {
    public static void saveBitmap(final Bitmap bitmap, final String filePath) throws IOException {
        File f = new File(filePath);

        if (f.createNewFile()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } else {
            throw new IOException("Failed to create new file");
        }
    }
}
