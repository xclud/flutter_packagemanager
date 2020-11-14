import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:packagemanager/packagemanager.dart';

void main() {
  const MethodChannel channel = MethodChannel('packagemanager');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await Packagemanager.platformVersion, '42');
  });
}
