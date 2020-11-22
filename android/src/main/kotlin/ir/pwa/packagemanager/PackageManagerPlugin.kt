package ir.pwa.packagemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.provider.Telephony
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/** PackageManagerPlugin */
class PackageManagerPlugin : FlutterPlugin, ActivityAware, ActivityResultListener, MethodCallHandler {
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
            val intent = getIntentFromHashMap(call.arguments as HashMap<String, Any?>)
            result.success(resolveActivity(intent))
        } else if (call.method == "queryIntentActivities") {
            val intent = getIntentFromHashMap(call.arguments as HashMap<String, Any?>)
            result.success(queryIntentActivities(intent))
        } else if (call.method == "startActivity") {
            val intent = getIntentFromHashMap(call.arguments as HashMap<String, Any?>)
            startActivity(intent)
            result.success(true)
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
        } else if (call.method == "getDefaultSmsPackage") {
            result.success(getDefaultSmsPackage())
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        context.unregisterReceiver(broadcastReceiver)
    }

    private fun resolveActivity(intent: Intent): String? {
        val packageManager = context.packageManager
        val result = packageManager.resolveActivity(intent, 0)
        return if (result?.activityInfo != null) {
            result.activityInfo.packageName
        } else null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean {
        return false;
    }

    private fun queryIntentActivities(intent: Intent): ArrayList<HashMap<String, Any>> {
        val packageManager = context.packageManager
        val appList = packageManager.queryIntentActivities(intent, 0)
        Collections.sort(appList, ResolveInfo.DisplayNameComparator(packageManager))

        val apps = ArrayList<HashMap<String, Any>>()
        for (temp in appList) {

            val icon = temp.loadIcon(packageManager)
            val encodedImage = encodeToBase64(getBitmapFromDrawable(icon), Bitmap.CompressFormat.PNG, 100)

            val resolveInfo = HashMap<String, Any>()
            val activityInfo = HashMap<String, Any>()

            activityInfo["label"] = temp.activityInfo.loadLabel(packageManager).toString()
            activityInfo["packageName"] = temp.activityInfo.packageName
            activityInfo["name"] = temp.activityInfo.name
            activityInfo["enabled"] = temp.activityInfo.enabled
            activityInfo["exported"] = temp.activityInfo.exported


            resolveInfo["icon"] = encodedImage
            resolveInfo["activityInfo"] = activityInfo

            apps.add(resolveInfo);
        }

        return apps;
    }


//    private val INSTALL_REPLACE_EXISTING = 0x00000002
//    fun installPackage(fileName: String) {
//        try {
//            val pkgManager = context.packageManager
//            val installPackage: Method = pkgManager.javaClass.getMethod("installPackage", Uri::class.java, IPackageInstallObserver::class.java, Int::class.javaPrimitiveType, String::class.java)
//            val androidAPK = Uri.fromFile(File(fileName))
//            val params = arrayOf<Any?>(androidAPK, null, INSTALL_REPLACE_EXISTING, "")
//            installPackage.invoke(pkgManager, params)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun deletePackage(packageName: String) {
//        try {
//            val pkgManager = context.packageManager
//            val deletePackage: Method = pkgManager.javaClass.getMethod("installPackage", Array<String>::class.java, IPackageDeleteObserver::class.java, Int::class.javaPrimitiveType)
//            val params = arrayOf<Any?>(packageName, null, 0)
//            deletePackage.invoke(pkgManager, params)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun uninstallPackage(packageName: String?) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.fromParts("package",
                packageName, null))
        context.startActivity(intent)
    }

    private fun startActivity(intent: Intent) {
        context.startActivity(intent)
    }

    private fun getPackageName(): String {
        return context.packageName;
    }

    @SuppressLint("NewApi")
    private fun getDefaultSmsPackage(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Telephony.Sms.getDefaultSmsPackage(context)
        } else {
            val defApp = Settings.Secure.getString(context.contentResolver, "sms_default_application")
            val pm = context.packageManager
            val iIntent = pm.getLaunchIntentForPackage(defApp)
            val mInfo = pm.resolveActivity(iIntent, 0)
            mInfo.activityInfo.packageName
        }
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
