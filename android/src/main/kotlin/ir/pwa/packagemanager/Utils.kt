package ir.pwa.packagemanager

import android.content.Intent
import android.net.Uri

fun getIntentFromHashMap(hashMap: HashMap<String, Any?>): Intent {
    val intent = Intent()

    if (hashMap.containsKey("action")) {
        intent.action = hashMap["action"] as String
    }

    if (hashMap.containsKey("data")) {
        val data = hashMap["data"] as String?
        if (data != null) {
            intent.data = Uri.parse(data)
        }
    }

    if (hashMap.containsKey("type")) {
        val type = hashMap["type"] as String?
        intent.type = type
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
                intent.putExtra(key, value)
            } else if (value is String) {
                intent.putExtra(key, value)
            }
        }
    }
    return intent;
}