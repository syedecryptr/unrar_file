package com.syed.unrar_file;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;

import java.io.IOException;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** UnrarFilePlugin */
public class UnrarFilePlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "unrar_file");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("extractRAR")) {
      final String file_path = call.argument("file_path");
      final String destination_path = call.argument("destination_path");
      Log.i("extractRAR", "arguments recieved : " + file_path + " " + destination_path);
//      result.success("extraction done");
      try {
        Junrar.extract(file_path, destination_path);
        result.success("Android " + android.os.Build.VERSION.RELEASE);

      }
      catch (IOException | RarException e){
        result.error("extractionError", e.getMessage(), null);
      }
    } else {
      result.notImplemented();
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
