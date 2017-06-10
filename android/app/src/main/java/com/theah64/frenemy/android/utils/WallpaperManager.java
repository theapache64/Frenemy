package com.theah64.frenemy.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.theah64.frenemy.android.commandcenter.commands.BaseCommand;
import com.theah64.frenemy.android.commandcenter.commands.LoremPixelCommand;

import java.io.IOException;

/**
 * Created by theapache64 on 8/10/16.
 */

public class WallpaperManager {

    //no cache config.
    //TODO: DEBUG -DISABLED.
    /*private static final DisplayImageOptions options = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();*/

    public static void setWallpaper(final Context context, final String imageUrl, final BaseCommand.Callback callback, final boolean isInLoop, final boolean isFinished) {

        ImageLoader.getInstance().loadImage(imageUrl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                callback.onError("ERROR: " + failReason.getCause().getMessage());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    final android.app.WallpaperManager wm = android.app.WallpaperManager.getInstance(context);
                    wm.setBitmap(loadedImage);


                    final String statusMsg;
                    if (imageUrl.contains(LoremPixelCommand.DOMAIN)) {
                        statusMsg = "Wallpaper set : " + imageUrl;
                    } else {
                        statusMsg = "Wallpaper set :" + CommonUtils.getIMGSRC(imageUrl);
                    }

                    if (isInLoop) {
                        callback.onInfo(statusMsg);
                    } else {
                        callback.onSuccess(statusMsg);
                    }

                    if (isFinished) {
                        callback.onFinish("Wallpaper changed.");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError("ERROR: " + e.getMessage());
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                callback.onError("ERROR: Loading cancelled");
            }
        });

    }
}
