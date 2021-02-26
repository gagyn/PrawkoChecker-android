package com.example.prawkochecker

import android.content.Context
import java.util.*

class ResourceHelper {

    fun getConfigValue(context: Context, name: String): String {
        val resources = context.resources
        val rawResource = resources.openRawResource(R.raw.config)
        val properties = Properties()
        properties.load(rawResource)
        return properties.getProperty(name)
    }
}