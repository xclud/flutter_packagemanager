import 'dart:convert';

class Intent {
  String action;
  String data;
  String type;
  final List<String> _category = [];
  final Map<String, dynamic> _extras = {};

  List<String> get category => _category;

  Map<String, dynamic> get extras => _extras;

  set category(List<String> categories) {
    _category.clear();
    if (category != null) {
      _category.addAll(categories);
    }
  }

  set extras(Map<String, dynamic> extras) {
    _extras.clear();
    if (extras != null) {
      _extras.addAll(extras);
    }
  }

  Intent(this.action, [this.data]);

  factory Intent.fromJson(Map<String, dynamic> json) {
    final String action = json['action'];
    final String data = json['data'];
    final String type = json['type'];
    final extras = json['extras'];

    final intent = Intent(action, data);

    intent.type = type;
    if (extras != null) {
      intent.extras = Map.from(extras);
    }

    return intent;
  }

  Map<String, dynamic> toJson() {
    final json = <String, dynamic>{};

    json['action'] = action;
    json['data'] = data;
    json['type'] = type;
    json['category'] = _category;
    json['extras'] = _extras;

    return json;
  }

  @override
  String toString() {
    return jsonEncode(toJson());
  }

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

class MediaStore {
  static const ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
  static const ACTION_IMAGE_CAPTURE_SECURE =
      "android.media.action.IMAGE_CAPTURE_SECURE";
  static const ACTION_REVIEW = "android.provider.action.REVIEW";
  static const ACTION_REVIEW_SECURE = "android.provider.action.REVIEW_SECURE";
  static const ACTION_VIDEO_CAPTURE = "android.media.action.VIDEO_CAPTURE";
}
