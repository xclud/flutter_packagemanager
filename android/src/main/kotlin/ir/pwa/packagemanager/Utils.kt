package ir.pwa.packagemanager

import android.content.Intent
import android.net.Uri

fun getIntentFromHashMap(hashMap: HashMap<String, Any?>): Intent {
    val intent = Intent()

    if (hashMap.containsKey("action")) {
        intent.action = hashMap["action"] as String
    }

    if (hashMap.containsKey("data")) {
        intent.data = Uri.parse(hashMap["data"] as String)
    }

    if (hashMap.containsKey("type")) {
        intent.type = hashMap["type"] as String
    }

    if (hashMap.containsKey("flags")) {
        intent.flags = hashMap["flags"] as Int
    }

    if (hashMap.containsKey("categories")) {
        val cats = hashMap["categories"] as ArrayList<*>;
        for (cat in cats) {
            intent.addCategory(cat as String)
        }
    }

    if (hashMap.containsKey("extras")) {
        val extras = hashMap["extras"] as HashMap<*, *>;

        for (extra in extras) {
            val key = extra.key as String;
            val value = extra.value;
            if (value is Int) {
                intent.extras!!.putInt(key, value)
            } else if (value is String) {
                intent.extras!!.putString(key, value)
            }
        }
    }
    return intent;
}