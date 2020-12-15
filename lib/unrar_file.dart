
import 'dart:async';

import 'package:flutter/services.dart';

class UnrarFile {
  static const MethodChannel _channel =
      const MethodChannel('unrar_file');

  static Future<String> extract_rar(file_path, destination_path) async {
    try {
      var result = await _channel.invokeMethod('extractRAR', {"file_path": file_path, "destination_path": destination_path});
      return result;
    }
    on PlatformException catch(e){
      throw e.message;
    }
  }
}
