package ir.pwa.packagemanager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.provider.Settings
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


/** PackageManagerPlugin */
class PackageManagerPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var broadcastReceiver: PackageManagerBroadcastReceiver

    @SuppressLint("InlinedApi")
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.getApplicationContext();
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "packagemanager")
        channel.setMethodCallHandler(this)

        broadcastReceiver = PackageManagerBroadcastReceiver(channel);

        context.registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_DATA_CLEARED)
            addAction(Intent.ACTION_PACKAGE_FIRST_LAUNCH)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addAction(Intent.ACTION_PACKAGE_NEEDS_VERIFICATION)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_RESTARTED)
            addAction(Intent.ACTION_PACKAGE_VERIFIED)
            addAction(Intent.ACTION_PACKAGES_SUSPENDED)
            addAction(Intent.ACTION_PACKAGES_UNSUSPENDED)
            addDataScheme("package")
        })
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "resolveActivity") {
            val action = call.argument<String>("action")
            val categories = call.argument<Array<String>>("categories");
            result.success(resolveActivity(action, categories))
        } else if (call.method == "uninstallPackage") {
            val packageName = call.arguments<String>()
            uninstallPackage(packageName)
            result.success(true)
        } else if (call.method == "openDefaultAppsSettings") {
            openDefaultAppsSettings()
            result.success(true)
        } else if (call.method == "getPackagesForUid") {
            val packages = context.packageManager.getPackagesForUid(call.arguments<Int>())
            result.success(packages)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        context.unregisterReceiver(broadcastReceiver)
    }

    private fun resolveActivity(action: String, categories: Array<String>?): String? {
        val intent = Intent()
        intent.action = action

        if (categories != null) {
            for (category in categories) {
                intent.addCategory(category)
            }
        }

        val packageManager = context.packageManager
        val result = packageManager.resolveActivity(intent, 0)
        return if (result?.activityInfo != null) {
            result.activityInfo.packageName
        } else null
    }

    private fun uninstallPackage(packageName: String?) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.fromParts("package",
                packageName, null))
        context.startActivity(intent)
    }

    private fun openDefaultAppsSettings() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent);
        } else {
            val intent = Intent(Settings.ACTION_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent);
        }
    }
}
