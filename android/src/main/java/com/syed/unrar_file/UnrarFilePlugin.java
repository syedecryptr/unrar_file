package com.syed.unrar_file;

import android.os.Handler;
import android.os.Looper;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.exception.UnsupportedRarV5Exception;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * UnrarFilePlugin
 */
public class UnrarFilePlugin implements FlutterPlugin, MethodCallHandler {
    private final Executor executor = Executors.newCachedThreadPool();
    Handler handler = new Handler(Looper.getMainLooper());
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "unrar_file");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {
        if (call.method.equals("extractRAR")) {
            final String file_path = call.argument("file_path");
            final String destination_path = call.argument("destination_path");
            final String password = call.argument("password");
            executor.execute(() -> {
                try {
                    extract(password, file_path, destination_path);
                    sendSuccess(result);
                } catch (UnsupportedRarV5Exception e) {
                    sendError(result, "extractionRAR5Error :: " + e);
                } catch (IOException | RarException e) {
                    sendError(result, "extractionError :: " + e);
                }
            });
        } else {
            result.notImplemented();
        }
    }

    private void extract(String password, String file_path, String destination_path)
            throws RarException, IOException {
        if (isNullOrEmpty(password)) {
            Junrar.extract(file_path, destination_path);
        } else {
            Junrar.extract(file_path, destination_path, password);
        }
    }

    void sendSuccess(final Result result) {
        handler.post(() -> result.success("Extraction Success"));
    }

    void sendError(final Result result, final String msg) {
        handler.post(() -> result.error(msg, "", null));
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
