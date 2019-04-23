package com.catkit.framework.net.http.okhttp

interface IGet {
    /**
     * Asynchronous Get.
     * @param url Address of http get, can not be null.
     * @param header The header of http get, default null.
     * @param callback The asynchronous get's callback. can not be null.
     */
    fun get(url: String, header: HashMap<String, String>? = null, params: HashMap<String, Any>?, callback: OnHttpListener)

    /**
     * Synchronous get params like [get] but the thread has to wait.
     */
    fun getSync(url: String, header: HashMap<String, String>? = null, params: HashMap<String, Any>): RequestResult
}