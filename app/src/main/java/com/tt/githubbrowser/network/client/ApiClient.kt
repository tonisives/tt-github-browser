package com.tt.githubbrowser.network.client

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.tt.githubbrowser.R
import com.tt.githubbrowser.network.route.ApiRoute
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import timber.log.Timber
import timber.log.Timber.d
import java.net.URLDecoder

// Client constructs and starts the requests.

abstract class ApiClient(val ctx: Context) {
    protected fun performRequest(route: ApiRoute, completion: (apiResponse: BaseApiResponse) -> Unit) {
        val request: StringRequest =
            object : StringRequest(
                route.httpMethod,
                route.url,
                { response ->
                    printResponse(route, response)
                    completion(BaseApiResponse(200, response))
                },
                {
                    it.printStackTrace()
                    handleError(it.networkResponse, completion)
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    return route.headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    return JSONObject(route.params as Map<String, Any>).toString().toByteArray()
                }
            }

        request.retryPolicy = DefaultRetryPolicy(
            route.timeOut,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        printRequest(request)
        getRequestQueue()!!.add(request)
    }

    private fun printResponse(route: ApiRoute, response: String?) {
        val json = JSONTokener(response).nextValue()

        val message = if (json is JSONObject)
            JSONObject(response).toString(2)
        else
            JSONArray(response).toString(2)

        d("<<<\n${route.url}:\n$message")
    }

    private fun <T> printRequest(request: Request<T>) {
        if (Timber.treeCount() == 0) return
        try {
            val body: ByteArray = request.body
            val bodyString = if (body != null) "\nbody:\n" + String(request.body) else ""
            val headers = JSONObject(request.headers)
            val log = ">>> \n" + URLDecoder.decode(request.url, "ASCII") + "\nheaders:\n" + headers.toString(2) + bodyString
            d(log)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This method will make the creation of the answer as ApiResponse
     **/
    private fun handleError(
        networkResponse: NetworkResponse?,
        completion: (apiResponse: BaseApiResponse) -> Unit
    ) {
        var apiResponse: BaseApiResponse = if (networkResponse == null) {
            BaseApiResponse(500, ctx?.getString(R.string.no_internet) ?: "")
        } else {
            var errorMessage = "${networkResponse.statusCode}"
            if (networkResponse.data != null) {
                errorMessage += ":\n${String(networkResponse.data)}"
            }

            d(">>> \n${errorMessage}")
            BaseApiResponse(networkResponse.statusCode, errorMessage)
        }

        completion.invoke(apiResponse)
    }

    /**
     * This method will return the error as String
     **/
    private fun getStringError(volleyError: VolleyError): String {
        return when (volleyError) {
            is TimeoutError -> "The conection timed out."
            is NoConnectionError -> "The conection couldn´t be established."
            is AuthFailureError -> "There was an authentication failure in your request."
            is ServerError -> "Error while prosessing the server response."
            is NetworkError -> "Network error, please verify your conection."
            is ParseError -> "Error while prosessing the server response."
            else -> "Internet error"
        }
    }

    /**
     * We create and return a new instance for the queue of Volley requests.
     **/
    private fun getRequestQueue(): RequestQueue? {
        val maxCacheSize = 20 * 1024 * 1024
        val cache = DiskBasedCache(ctx?.cacheDir, maxCacheSize)
        val netWork = BasicNetwork(HurlStack())
        val mRequestQueue = RequestQueue(cache, netWork)
        mRequestQueue.start()
        System.setProperty("http.keepAlive", "false")
        return mRequestQueue
    }
}

data class BaseApiResponse(val statusCode: Int, val json: String)
data class ApiResponse<T>(val value: T?, val errorMessage: String?)