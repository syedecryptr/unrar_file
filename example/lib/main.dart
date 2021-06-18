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
  String rar_path = 'assets/sample.rar';
  String rar5_path = 'assets/sample5.rar';
  String rar_password_path = "assets/rar4-password-junrar.rar";
  late String file_path;
  late String destination_path ;
  late Directory tempDir;

  extraction() async{
    await extract_file(rar_path);
    await extract_file(rar5_path);
    await extract_file(rar_password_path, password:"junrar");
  }

  // prints content of the temporary directory.
  print_files() async {
    print("\n");
    print("extraction done ... input and output files : ");
    tempDir.list(recursive: true, followLinks: false)
        .listen((FileSystemEntity entity) {
      print(entity.path);
    });
  }

  delete_file() async{
    tempDir.list(recursive: true, followLinks: false)
        .listen((FileSystemEntity entity) {
      new File(entity.path).deleteSync();
    });
  }

  Future<List<String>>get_input_and_destination_path(asset_file_path) async{
    // for this example
    // inside tempDir rar files kept and later
    // extracted outputs are also stored in the same directory.
    tempDir = await getApplicationDocumentsDirectory();
    //empty the directory removing previous results.
    await delete_file();
    var input_path = join(tempDir.path, basename(asset_file_path));
    // inside the file_path first copy the file from the assets folder.

    ByteData data = await rootBundle.load(asset_file_path);
    List<int> bytes = data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
    await File(input_path).writeAsBytes(bytes);
    destination_path = tempDir.path;
    return [input_path, destination_path];
  }

  Future<void> extract_file(file_path, {password=""}) async {
    var input_output = await get_input_and_destination_path(file_path);
    var input_file_path = input_output[0];
    var destination_path = input_output[1];
    // Extraction may fail, so we use a try/catch PlatformException.
    try {
      var result = await UnrarFile.extract_rar(input_file_path,  destination_path, password: password);
      print(result);
      await print_files();
    } catch(e) {

      print("extraction failed $e");
    }
  }

  // this is where it all started.
  @override
  void initState() {
    super.initState();
    extraction();
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
