/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tt.githubbrowser.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.githubbrowser.network.client.ApiResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio

object ApiUtil {
    fun <T : Any> successCall(data: T) = createCall(data)

    fun <T : Any> createCall(response: T) = MutableLiveData<ApiResponse<T>>().apply {
        value = ApiResponse(response, null)
    } as LiveData<ApiResponse<T>>


    fun MockWebServer.enqueueResponse(
        fileName: String,
        responseCode: Int = 200,
        headers: Map<String, String> = emptyMap()
    ) {
        val inputStream = javaClass.classLoader!!.getResourceAsStream("api-response/$fileName")

        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()

        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }

        this.enqueue(mockResponse.setBody(source.readString(Charsets.UTF_8)).setResponseCode(responseCode))
    }
}
