class Intent {
  String action;
  String data;
  final List<String> _category = [];

  List<String> get category => _category;

  set category(List<String> categories) {
    _category.clear();
    if (category != null) {
      _category.addAll(categories);
    }
  }

  Intent(this.action, [this.data]);

  factory Intent.fromJson(Map<String, dynamic> json) {
    final String action = json['action'];
    final String data = json['data'];

    return Intent(action, data);
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
