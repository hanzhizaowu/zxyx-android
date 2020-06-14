package cn.zhaoxi.zxyx.module.video.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.zhaoxi.library.BuildConfig;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class VideoCoverUtil {
    //ffmpeg -y -i input.mp4 -strict -2 -vcodec libx264  -preset ultrafast -crf 24 -acodec copy -ar 44100 -ac 2 -b:a 12k -s 640x352 -aspect 16:9 output.mp4
    public static void getVideoCover(Context context, String inputFile, String outputFile, final VideoCoverListener callback) {
        File file = new File(inputFile);
        if(!file.exists()) {
            if (BuildConfig.DEBUG) Log.i("VideoCoverUtil", "input file no exist");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String outputName = "trimmedCover_" + timeStamp + ".jpg";
        final String tmpFile = outputFile + "/" + outputName;

        String cmd = "-i " + inputFile + " -f image2 -ss 00:00:01 -vframes 1 -an " + tmpFile;
        String[] command = cmd.split(" ");

        if (BuildConfig.DEBUG) Log.i("VideoCoverUtil", "get cover in file is:"+inputFile+" , out file is:" + tmpFile);
        try {
            FFmpeg.getInstance(context).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String msg) {
                    if (callback != null) {
                        callback.onFailCover();
                    }
                }

                @Override
                public void onSuccess(String msg) {
                    if (callback != null) {
                        callback.onFinishCover(inputFile, tmpFile);
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
