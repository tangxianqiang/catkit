package com.catkit.framework.net.http.okhttp

import okhttp3.*
import java.io.IOException

object OkHttpGet {
    /**
     * Asynchronous Get.
     * @param url      Address of http get, can not be null.
     * @param header   The header of http get, default null.
     * @param callback The asynchronous get's callback. can not be null.
     */
    fun get(client: OkHttpClient, url: String, header: HashMap<String, String>?, params: HashMap<String, Any>?, callback: OnHttpListener) {
        val builder = Request.Builder().url(url)
        // add header
        header?.forEach { (key, value) ->
            builder.header(key, value)
        }

        try {
            client.newCall(builder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(HttpException(e))
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        callback.onFailure(HttpException(response.code()))
                    } else {
                        callback.onSuccess(RequestResult(response))
                    }
                }
            })
        } catch (e: Exception) {
            callback.onFailure(HttpException(e))
        }
    }

    /**
     * Synchronous get params like [get] but the thread has to wait.
     */
    fun getSync(client: OkHttpClient, url: String, header: HashMap<String, String>?, params: HashMap<String, Any>): RequestResult {
        val builder = Request.Builder().url(url)
        header?.forEach { (key, value) -> builder.header(key, value) }
        return try {
            val response = client.newCall(builder.build()).execute()
            if (!response.isSuccessful) {
                RequestResult(HttpException(response.code()))
            } else {
                RequestResult(response)
            }
        } catch (e: Exception) {
            RequestResult(HttpException(e))
        }
    }
}

