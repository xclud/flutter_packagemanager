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
    return resolveActivity(Intent.ACTION_MAIN, [Intent.CATEGORY_HOME]);
  }

  Future<String> resolveActivity(String action, List<String> categories) async {
    final Map<String, Object> map = {
      'action': action,
      'categories': categories
    };

    final String packageName =
        await _channel.invokeMethod('resolveActivity', map);
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

  Future<String> getPackageName() async {
    return _channel.invokeMethod('getPackageName');
  }

  Future<void> openDefaultAppsSettings() async {
    await _channel.invokeMethod('openDefaultAppsSettings');
  }

  Future<void> setWallpaperOffsets(double xOffset, double yOffset) async {
    final Map<String, Object> map = {
      'xOffset': xOffset,
      'yOffset': yOffset
    };

    await _channel.invokeMethod('setWallpaperOffsets', map);
  }

  Future<void> setWallpaperOffsetSteps(double xStep, double yStep) async {
    final Map<String, Object> map = {
      'xStep': xStep,
      'yStep': yStep
    };

    await _channel.invokeMethod('setWallpaperOffsetSteps', map);
  }
}

class Intent {
  static const String ACTION_PACKAGE_ADDED =
      'android.intent.action.PACKAGE_ADDED';
  static const String ACTION_PACKAGE_FULLY_REMOVED =
      'android.intent.action.PACKAGE_FULLY_REMOVED';

  static const String ACTION_PACKAGE_REMOVED =
      'android.intent.action.PACKAGE_REMOVED';

  static const String ACTION_MAIN = "android.intent.action.MAIN";
  static const String ACTION_DIAL = "android.intent.action.DIAL";
  static const String ACTION_VIEW = "android.intent.action.VIEW";

  static const String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
  static const String CATEGORY_HOME = "android.intent.category.HOME";
  static const String CATEGORY_INFO = "android.intent.category.INFO";
}
