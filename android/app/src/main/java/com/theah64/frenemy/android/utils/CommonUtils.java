package com.theah64.frenemy.android.utils;

import android.os.Build;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by theapache64 on 11/9/16.
 */
public class CommonUtils {

    public static boolean isSupport(final int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    public static int parseInt(String integer) {
        try {
            if (integer != null) {
                return Integer.parseInt(integer);
            }
            return -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String getContentTypeFromFile(String filePath) {
        String contentType = null;
        final String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            contentType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return contentType;
    }

    public static long parseLong(String value, long defaultValue) {
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public static String getProper(int count, String singular, String plural) {
        return count > 1 ? singular : plural;
    }

    public static String getIMGSRC(String imageUrl) {
        return "<img src=\"" + imageUrl + "\"/>";
    }

    public static String getIMGSRC(String imageUrl, final int width, final int height) {
        return String.format("<img width=\"%s\" height=\"%s\"  src=\"%s\" />", width, height, imageUrl);
    }

    public static String getSizeInKB(File cmpFile) {
        return ((double) (cmpFile.length() / 1024)) + "KB";
    }

    public static String getDownloadHref(String downloadLink) {
        return String.format("<a href=\"%s\" download>Download</a>", downloadLink);
    }
}
