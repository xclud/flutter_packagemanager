package ir.pwa.packagemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.ContextWrapper
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

import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware

/** PackageManagerPlugin */
class PackageManagerPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity
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
            val categories = call.argument<ArrayList<String>>("categories");
            result.success(resolveActivity(action, categories))
        } else if (call.method == "uninstallPackage") {
            val packageName = call.arguments<String>()
            uninstallPackage(packageName)
            result.success(true)
        } else if (call.method == "getPackageName") {
            result.success(getPackageName())
        } else if (call.method == "openDefaultAppsSettings") {
            openDefaultAppsSettings()
            result.success(true)
        } else if (call.method == "getPackagesForUid") {
            val packages = context.packageManager.getPackagesForUid(call.arguments<Int>())
            result.success(packages)
        } else if (call.method == "setWallpaperOffsets") {
            val xOffset = call.argument<Double>("xOffset")
            val yOffset = call.argument<Double>("yOffset")
            setWallpaperOffsets(xOffset!!.toFloat(), yOffset!!.toFloat())
            result.success(true)
        } else if (call.method == "setWallpaperOffsetSteps") {
            val xStep = call.argument<Double>("xStep")
            val yStep = call.argument<Double>("yStep")
            setWallpaperOffsetSteps(xStep!!.toFloat(), yStep!!.toFloat())
            result.success(true)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        context.unregisterReceiver(broadcastReceiver)
    }

    private fun resolveActivity(action: String?, categories: ArrayList<String>?): String? {
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

    private fun getPackageName(): String {
        return context.packageName;
    }

    @SuppressLint("InlinedApi")
    private fun openDefaultAppsSettings() {
        /* Some Huawei devices don't let us reset normally, handle it by opening preferred apps */
        val preferredApps = Intent("com.android.settings.PREFERRED_SETTINGS");
        preferredApps.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        if (context.packageManager.resolveActivity(preferredApps, 0) != null) {
            context.startActivity(preferredApps);
            return
        }

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


    @SuppressLint("NewApi")
    private fun setWallpaperOffsets(xOffset: Float, yOffset: Float) {
        val b = activity.window.decorView.rootView.windowToken;
        val w = WallpaperManager.getInstance(context)
        w.setWallpaperOffsets(b, xOffset, yOffset)
    }

    @SuppressLint("NewApi")
    private fun setWallpaperOffsetSteps(xStep: Float, yStep: Float) {
        val w = WallpaperManager.getInstance(context)
        w.setWallpaperOffsetSteps(xStep, yStep)

    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        // The Activity your plugin was associated with has been
        // destroyed due to config changes. It will be right back
        // but your plugin must clean up any references to that
        // Activity and associated resources.
    }

    override fun onReattachedToActivityForConfigChanges(
            binding: ActivityPluginBinding
    ) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        // Your plugin is no longer associated with an Activity.
        // You must clean up all resources and references. Your
        // plugin may, or may not ever be associated with an Activity
        // again.
    }
}
