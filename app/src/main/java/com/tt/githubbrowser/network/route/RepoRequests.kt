package com.tt.githubbrowser.network.route

import com.android.volley.Request

class Repos(prefs: UserCredentials?) : ApiRoute(prefs) {
    override val url: String
        get() = "$baseUrl/user/repos"

    override val httpMethod: Int
        get() = Request.Method.GET

    override val params: HashMap<String, Any>
        get() = HashMap()
}