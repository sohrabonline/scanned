package com.shrapp.scanned;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


/**
 * ScanPlugin
 */

public class ScanPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private Activity activity;
    private FlutterPluginBinding flutterPluginBinding;
    private Result _result;
    private QrCodeAsyncTask task;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        this.flutterPluginBinding = flutterPluginBinding;
    }

    private void configChannel(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "scanned");
        channel.setMethodCallHandler(this);
        flutterPluginBinding.getPlatformViewRegistry()
                .registerViewFactory("scan_view", new ScanViewFactory(
                        flutterPluginBinding.getBinaryMessenger(),
                        flutterPluginBinding.getApplicationContext(),
                        activity,
                        binding
                ));
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        configChannel(binding);
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        configChannel(binding);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.flutterPluginBinding = null;
    }

    @Override
    public void onDetachedFromActivity() {
        activity = null;
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        _result = result;
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("parse")) {
            String path = (String) call.arguments;
            task = new QrCodeAsyncTask(this, path);
            task.execute(path);
        } else {
            result.notImplemented();
        }
    }

    /**
     * AsyncTask 静态内部类，防止内存泄漏
     */
    static class QrCodeAsyncTask extends AsyncTask<String, Integer, String> {
        private final WeakReference<ScanPlugin> mWeakReference;
        private final String path;

        public QrCodeAsyncTask(ScanPlugin plugin, String path) {
            mWeakReference = new WeakReference<>(plugin);
            this.path = path;
        }

        @Override
        protected String doInBackground(String... strings) {
            // 解析二维码/条码
            return QRCodeDecoder.decodeQRCode(mWeakReference.get().flutterPluginBinding.getApplicationContext(), path);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //识别出图片二维码/条码，内容为s
            ScanPlugin plugin = (ScanPlugin) mWeakReference.get();
            plugin._result.success(s);
            plugin.task.cancel(true);
            plugin.task = null;
            if (s != null) {
                Vibrator myVib = (Vibrator) plugin.flutterPluginBinding.getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                if (myVib != null) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        myVib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        myVib.vibrate(50);
                    }
                }
            }
        }
    }
}
