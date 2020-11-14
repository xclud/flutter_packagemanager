package ir.pwa.packagemanager

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONException

import org.json.JSONObject


class PackageManagerBroadcastReceiver(private val methodChannel: MethodChannel) : BroadcastReceiver() {

    @SuppressLint("NewApi")
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1 == null) {
            return;
        }


        val json = JSONObject()
        json.put("action", p1.action)

        if (p1.extras != null) {
            val keys = p1.extras!!.keySet()
            val extras = JSONObject()

            for (key in keys) {
                try {
                    extras.put(key, JSONObject.wrap(p1.extras!!.get(key)))
                } catch (e: JSONException) {
                    //Handle exception here
                }
            }
            json.put("extras", extras)
        }

        methodChannel.invokeMethod("packagemanager", json.toString())
    }
}