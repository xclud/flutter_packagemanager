import 'package:flutter/material.dart';
import 'dart:async';

import 'package:packagemanager/packagemanager.dart';
import 'package:packagemanager/packagemanager.dart' as pm;

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
                  isThreeLine: true,
                  title: Text(e.activityInfo.label),
                  subtitle: Text(
                      '${e.activityInfo.name}\n${e.activityInfo.packageName}'),
                  onTap: () {
                    final intent = pm.Intent();
                    intent.component = pm.ComponentName(
                        e.activityInfo.packageName, e.activityInfo.name);

                    PackageManager.instance.startActivity(intent);
                  },
                ),
              )
              .toList(),
        ),
      ),
    );
  }
}
