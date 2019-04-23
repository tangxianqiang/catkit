package com.catkit.framework.net.http.okhttp

import okhttp3.*
import java.io.IOException

object OkHttpPost {
    /**
     * Like other post asynchronous request, but it needs a MIME string type called "Multipurpose Internet Mail Extensions".
     * <p>
     *     MediaType contains three information about "type","subtype","charset" like "text/x-markdown; charset=utf-8".
     *     More about MIME see: 'http://www.w3school.com.cn/media/media_mimeref.asp'
     *     More about MediaType see: 'http://www.iana.org/assignments/media-types/media-types.xhtml'
     *     eg:
     *          json : application/json
     *          xml : application/xml
     *          png : image/png
     *          jpg : image/jpeg
     *          gif : image/gif
     *    okhttp has set MediaType by MediaType.parse()
     * </p>
     */
    fun post(client: OkHttpClient,url: String, headers: HashMap<String, String>?, params: String, paramType: String, callback: OnHttpListener) {
        if (params.isEmpty()) {
            callback.onFailure(HttpException("Empty string params!"))
            return
        }
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        //add params
        builder.post(RequestBody.create(MediaType.parse(paramType), params))
        try {
            client.newCall(builder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onFailure(HttpException(e))
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {

                }
            })
        } catch (e: Exception) {
            callback.onFailure(HttpException(e))
        }
    }

    /**
     * @see [post]
     */
    fun postSync(client: OkHttpClient,url: String, headers: HashMap<String, String>?, params: String, paramType: String): RequestResult {
        if (params.isEmpty()) {
            return RequestResult(HttpException("Empty string params!"))
        }
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        //add params
        builder.post(RequestBody.create(MediaType.parse(paramType), params))
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

    /**
     * Like other post asynchronous request, but it needs a form map for request.
     */
    fun post(client: OkHttpClient, url: String, headers: HashMap<String, String>?, paramsForm: HashMap<String, String>, callback: OnHttpListener) {
        if (paramsForm.isEmpty()) {
            callback.onFailure(HttpException("Empty form params!"))
            return
        }
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        // add params
        val formBuilder = FormBody.Builder()
        paramsForm.forEach { (key, value) -> formBuilder.add(key, value) }
        builder.post(formBuilder.build())
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
     * @see [post]
     */
    fun postSync(client: OkHttpClient,url: String, headers: HashMap<String, String>?, paramsForm: HashMap<String, String>): RequestResult {
        if (paramsForm.isEmpty()) {
            return RequestResult(HttpException("Empty form params!"))
        }
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        // add params
        val formBuilder = FormBody.Builder()
        paramsForm.forEach { (key, value) -> formBuilder.add(key, value) }
        builder.post(formBuilder.build())

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