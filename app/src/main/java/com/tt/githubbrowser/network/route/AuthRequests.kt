package com.tt.githubbrowser.network.route

import com.android.volley.Request

class Login(credentials: UserCredentials) : ApiRoute(credentials) {

    override val url: String
        get() = "$baseUrl/user"

    override val httpMethod: Int
        get() = Request.Method.GET


    override val params: HashMap<String, Any>
        get() = hashMapOf()

}