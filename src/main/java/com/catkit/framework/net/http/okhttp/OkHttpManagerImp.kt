package com.catkit.framework.net.http.okhttp

class OkHttpManagerImp:OkHttpManager(){
    /**
     * Asynchronous Get.
     * @param url Address of http get, can not be null.
     * @param header The header of http get, default null.
     * @param callback The asynchronous get's callback. can not be null.
     */
    override fun get(url: String, header: HashMap<String, String>?, params: HashMap<String, Any>?, callback: OnHttpListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Synchronous get params like [get] but the thread has to wait.
     */
    override fun getSync(url: String, header: HashMap<String, String>?, params: HashMap<String, Any>): RequestResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Post request for http asynchronously.
     * @param url The Address of post.
     * @param headers The header of request.
     * @param stringParams The request data.
     */
    override fun postString(url: String, headers: HashMap<String, String>?, stringParams: String, callback: OnHttpListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Everything like [postString] but it is synchronous.
     */
    override fun postStringSync(url: String, headers: HashMap<String, String>?, stringParams: String): RequestResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Like other post asynchronous request, but it needs a json string for request.
     */
    override fun postJson(url: String, headers: HashMap<String, String>?, jsonParams: String, callback: OnHttpListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * @see [postJson]
     */
    override fun postJsonSync(url: String, headers: HashMap<String, String>?, jsonParams: String): RequestResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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
    override fun post(url: String, headers: HashMap<String, String>?, params: String, paramType: String, callback: OnHttpListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * @see [post]
     */
    override fun postSync(url: String, headers: HashMap<String, String>?, params: String, paramType: String): RequestResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Like other post asynchronous request, but it needs a form map for request.
     */
    override fun post(url: String, headers: HashMap<String, String>?, paramsForm: HashMap<String, String>, callback: OnHttpListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * @see [post]
     */
    override fun postSync(url: String, headers: HashMap<String, String>?, paramsForm: HashMap<String, String>): RequestResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}