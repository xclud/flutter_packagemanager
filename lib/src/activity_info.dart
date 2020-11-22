class ActivityInfo {
  final String label;
  final String name;
  final String packageName;
  final bool enabled;
  final bool exported;

  ActivityInfo({
    this.label,
    this.packageName,
    this.name,
    this.enabled,
    this.exported,
  });

  factory ActivityInfo.fromJson(Map<String, dynamic> json) {
    final String label = json['label'];
    final String packageName = json['packageName'];
    final String name = json['name'];
    final bool enabled = json['enabled'];
    final bool exported = json['exported'];

    final activityInfo = ActivityInfo(
      label: label,
      packageName: packageName,
      name: name,
      enabled: enabled,
      exported: exported,
    );

    return activityInfo;
  }
}
