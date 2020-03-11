package com.tt.githubbrowser.network.route

import android.util.Base64

// This is the base class for different api routes. It creates the route urls
abstract class ApiRoute(private val credentials: UserCredentials?) {
    val timeOut = 3000

    val baseUrl = customUrl ?: "https://api.github.com"

    abstract val url: String

    abstract val httpMethod: Int

    abstract val params: HashMap<String, Any>

    val headers: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            val encodedString = Base64.encodeToString(
                String.format("%s:%s", credentials?.email, credentials?.token)
                    .toByteArray(),
                Base64.NO_WRAP
            )

            map["Authorization"] = String.format("Basic %s", encodedString)
            return map
        }

    companion object {
        var customUrl: String? = null
    }
}