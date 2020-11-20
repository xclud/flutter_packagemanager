class ActivityInfo {
  final String name;
  final String packageName;

  ActivityInfo(this.packageName, this.name);

  factory ActivityInfo.fromJson(Map<String, dynamic> json) {
    final String packageName = json['packageName'];
    final String name = json['name'];

    final activityInfo = ActivityInfo(packageName, name);

    return activityInfo;
  }
}
