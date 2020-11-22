import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:packagemanager/packagemanager.dart';
import 'package:packagemanager/packagemanager.dart' as pm;
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<ResolveInfo> _apps = [];

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    final mainIntent = pm.Intent(pm.Intent.ACTION_MAIN)
      ..categories = [pm.Intent.CATEGORY_LAUNCHER];
    final all = await PackageManager.instance.queryIntentActivities(mainIntent);

    _apps.addAll(all);
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: _apps
              .map(
                (e) => ListTile(
                  leading: Image.memory(e.icon),
                  title: Text(e.activityInfo.name),
                  subtitle: Text(e.activityInfo.packageName),
                ),
              )
              .toList(),
        ),
      ),
    );
  }
}
