import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';
import 'package:path/path.dart';

import 'package:flutter/services.dart';
import 'package:unrar_file/unrar_file.dart';
import 'package:path_provider/path_provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String file_path = 'assets/sample.rar';
  String destination_path ;
  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {


    print("inside init platform state");

    Directory tempDir = await getTemporaryDirectory();
    file_path = join(tempDir.path, "sample.rar");
    // inside the file_path first copy the file from the assets folder.
    // as there I dont know direct way to find the asset folder.

    ByteData data = await rootBundle.load("assets/sample.rar");
    List<int> bytes = data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
    await File(file_path).writeAsBytes(bytes);
    destination_path = tempDir.path;
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      await UnrarFile.extract_rar(file_path,  destination_path);
      tempDir.list(recursive: true, followLinks: false)
          .listen((FileSystemEntity entity) {
        print(entity.path);
      });
      platformVersion = "extraction_done";
      print("extraction done.");
    } catch(e) {
      print("extraction failed");
      platformVersion = e;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('status message : $_platformVersion\n'),
        ),
      ),
    );
  }
}
