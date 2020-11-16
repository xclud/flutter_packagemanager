import 'dart:async';

import 'package:flutter/services.dart';

class PackageManager {
  static final PackageManager instance = PackageManager._();
  static const MethodChannel _channel = const MethodChannel('packagemanager');

  StreamController _streamController = StreamController();

  Future<dynamic> _platformCallHandler(MethodCall call) async {
    _streamController.add(call.arguments);
  }

  PackageManager._() {
    _channel.setMethodCallHandler(_platformCallHandler);
  }

  Stream<dynamic> get stream => _streamController.stream;

  Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String> getLauncherPackageName() async {
    final String packageName =
        await _channel.invokeMethod('getLauncherPackageName');
    return packageName;
  }

  Future<List<String>> getPackagesForUid(int uid) async {
    final List<String> packages =
        await _channel.invokeMethod('getPackagesForUid', uid);
    return packages;
  }

  Future<void> uninstallPackage(String packageName) async {
    await _channel.invokeMethod('uninstallPackage', packageName);
  }

  Future<void> openDefaultAppsSettings() async {
    await _channel.invokeMethod('openDefaultAppsSettings');
  }
}

class Intent {
  static const String ACTION_PACKAGE_ADDED =
      'android.intent.action.PACKAGE_ADDED';
  static const String ACTION_PACKAGE_FULLY_REMOVED =
      'android.intent.action.PACKAGE_FULLY_REMOVED';

  static const String ACTION_PACKAGE_REMOVED =
      'android.intent.action.PACKAGE_REMOVED';
}
