package com.tt.githubbrowser.util

import android.content.Context

class Credentials(context: Context) {
    private val resources = context.resources
    private val packageName = context.packageName

    private val environment: String? by lazy { getResource("environment") }

    fun getResource(key: String): String? {
        return try {
            val resId = resources.getIdentifier(key, "string", packageName)
            resources.getString(resId)
        } catch (e: Exception) {
            null
        }
    }
}