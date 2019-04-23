package com.catkit.framework.net.http.okhttp

interface IPost {

    /**
     * Post request for http asynchronously.
     * @param url The Address of post.
     * @param headers The header of request.
     * @param stringParams The request data.
     */
    fun postString(url: String, headers: HashMap<String, String>? = null, stringParams: String, callback: OnHttpListener)

    /**
     * Everything like [postString] but it is synchronous.
     */
    fun postStringSync(url: String, headers: HashMap<String, String>? = null, stringParams: String): RequestResult

    /**
     * Like other post asynchronous request, but it needs a json string for request.
     */
    fun postJson(url: String, headers: HashMap<String, String>? = null, jsonParams: String, callback: OnHttpListener)

    /**
     * @see [postJson]
     */
    fun postJsonSync(url: String, headers: HashMap<String, String>? = null, jsonParams: String): RequestResult

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
    fun post(url: String, headers: HashMap<String, String>? = null, params: String, paramType: String, callback: OnHttpListener)

    /**
     * @see [post]
     */
    fun postSync(url: String, headers: HashMap<String, String>? = null, params: String, paramType: String): RequestResult

    /**
     * Like other post asynchronous request, but it needs a form map for request.
     */
    fun post(url: String, headers: HashMap<String, String>? = null, paramsForm: HashMap<String, String>, callback: OnHttpListener)

    /**
     * @see [post]
     */
    fun postSync(url: String, headers: HashMap<String, String>? = null, paramsForm: HashMap<String, String>): RequestResult
}