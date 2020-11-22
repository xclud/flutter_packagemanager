import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:packagemanager/src/activity_info.dart';
import 'package:packagemanager/src/intent.dart';
import 'package:packagemanager/src/resolve_info.dart';

class PackageManager {
  static final PackageManager instance = PackageManager._();
  static const MethodChannel _channel = const MethodChannel('packagemanager');

  final _streamController = StreamController<Intent>();

  Future<dynamic> _platformCallHandler(MethodCall call) async {
    final Map<String, dynamic> json = Map.from(call.arguments);

    final intent = Intent.fromJson(json);
    _streamController.add(intent);
  }

  PackageManager._() {
    _channel.setMethodCallHandler(_platformCallHandler);
  }

  Stream<Intent> get stream => _streamController.stream;

  Future<String> getPlatformVersion() async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String> resolveActivity(Intent intent) async {
    final String packageName =
        await _channel.invokeMethod('resolveActivity', intent.toJson());
    return packageName;
  }

  Future<List<ResolveInfo>> queryIntentActivities(Intent intent) async {
    final result =
        await _channel.invokeMethod('queryIntentActivities', intent.toJson());

    final list = List.from(result);

    final val = list.map((e) => Map.from(e)).map(
          (e) =>  ResolveInfo.fromJson(
            Map<String, dynamic>.from(e),
          ),
        );

    return val.toList();
  }

  Future<List<String>> getPackagesForUid(int uid) async {
    final List<String> packages =
        await _channel.invokeMethod('getPackagesForUid', uid);
    return packages;
  }

  Future<void> uninstallPackage(String packageName) async {
    await _channel.invokeMethod('uninstallPackage', packageName);
  }

  Future<void> startActivity(Intent intent) async {
    await _channel.invokeMethod('startActivity', intent.toJson());
  }

  Future<String> getPackageName() async {
    return _channel.invokeMethod('getPackageName');
  }

  Future<void> openDefaultAppsSettings() async {
    await _channel.invokeMethod('openDefaultAppsSettings');
  }

  Future<String> getDefaultSmsPackage() async {
    return _channel.invokeMethod('getDefaultSmsPackage');
  }

  Future<void> setWallpaperOffsets(double xOffset, double yOffset) async {
    final Map<String, Object> map = {'xOffset': xOffset, 'yOffset': yOffset};

    await _channel.invokeMethod('setWallpaperOffsets', map);
  }

  Future<void> setWallpaperOffsetSteps(double xStep, double yStep) async {
    final Map<String, Object> map = {'xStep': xStep, 'yStep': yStep};

    await _channel.invokeMethod('setWallpaperOffsetSteps', map);
  }
}
