package com.syed.unrar_file;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.exception.UnsupportedRarV5Exception;

import java.io.IOException;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

/** UnrarFilePlugin */
public class UnrarFilePlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Executor executor = Executors.newCachedThreadPool();
  Handler handler = new Handler(Looper.getMainLooper());

  public static boolean isNullOrEmpty(String str) {
    if(str != null && !str.isEmpty())
      return false;
    return true;
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
//      result.success("extraction done");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isNullOrEmpty(password)) {
                        Junrar.extract(file_path, destination_path);
                    } else {
                        Junrar.extract(file_path, destination_path, password);
                    }
                    sendSuccess(result);
                } catch (UnsupportedRarV5Exception e) {
                    sendError(result, "extractionRAR5Error :: "+ e.toString());
                } catch (IOException | RarException e) {
                    sendError(result, "extractionError :: "+ e.toString());
                }
            }
        });
    } else {
      result.notImplemented();
    }
  }

    void sendSuccess(final Result result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                result.success("Extraction Success");
            }
        });
    }

    void sendError(final Result result, final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                result.error(msg, "", null);
            }
        });
    }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
