package com.catkit.framework.net.http.okhttp

import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

class OkHttpManagerImp : OkHttpManager {
    private val mClient: OkHttpClient
    private var readTimeOut: Long = 10L
    private var writeTimeOut: Long = 10L

    constructor(readTimeOut: Long, writeTimeOut: Long) {
        this.readTimeOut = readTimeOut
        this.writeTimeOut = writeTimeOut
    }

    constructor()

    companion object {
        const val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
        const val MEDIA_TYPE_TEXT = "text/plain; charset=utf-8"
    }

    init {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(readTimeOut, TimeUnit.SECONDS)
        builder.writeTimeout(writeTimeOut, TimeUnit.SECONDS)
        mClient = builder.build()
    }

    /**
     * Asynchronous Get.
     * @param url Address of http get, can not be null.
     * @param header The header of http get, default null.
     * @param callback The asynchronous get's callback. can not be null.
     */
    override fun get(url: String, header: HashMap<String, String>?, params: HashMap<String, Any>?, callback: OnHttpListener) {
        OkHttpGet.get(mClient, url, header, params, callback)
    }

    /**
     * Synchronous get params like [get] but the thread has to wait.
     */
    override fun getSync(url: String, header: HashMap<String, String>?, params: HashMap<String, Any>): RequestResult =
            OkHttpGet.getSync(mClient, url, header, params)

    /**
     * Post request for http asynchronously.
     * @param url The Address of post.
     * @param headers The header of request.
     * @param stringParams The request data.
     */
    override fun postString(url: String, headers: HashMap<String, String>?, stringParams: String, callback: OnHttpListener) {
        OkHttpPost.post(mClient, url, headers, stringParams, MEDIA_TYPE_TEXT, callback)
    }

    /**
     * Everything like [postString] but it is synchronous.
     */
    override fun postStringSync(url: String, headers: HashMap<String, String>?, stringParams: String): RequestResult =
            OkHttpPost.postSync(mClient, url, headers, stringParams, MEDIA_TYPE_TEXT)

    /**
     * Like other post asynchronous request, but it needs a json string for request.
     */
    override fun postJson(url: String, headers: HashMap<String, String>?, jsonParams: String, callback: OnHttpListener) {
        OkHttpPost.post(mClient, url, headers, jsonParams, MEDIA_TYPE_JSON, callback)
    }

    /**
     * @see [postJson]
     */
    override fun postJsonSync(url: String, headers: HashMap<String, String>?, jsonParams: String): RequestResult =
            OkHttpPost.postSync(mClient, url, headers, jsonParams, MEDIA_TYPE_JSON)

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
        OkHttpPost.post(mClient, url, headers, params, paramType, callback)
    }

    /**
     * @see [post]
     */
    override fun postSync(url: String, headers: HashMap<String, String>?, params: String, paramType: String): RequestResult =
            OkHttpPost.postSync(mClient, url, headers, params, paramType)

    /**
     * Like other post asynchronous request, but it needs a form map for request.
     */
    override fun post(url: String, headers: HashMap<String, String>?, paramsForm: HashMap<String, String>, callback: OnHttpListener) {
        OkHttpPost.post(mClient, url, headers, paramsForm, callback)
    }

    /**
     * @see [post]
     */
    override fun postSync(url: String, headers: HashMap<String, String>?, paramsForm: HashMap<String, String>): RequestResult =
            OkHttpPost.postSync(mClient, url, headers, paramsForm)

    /**
     * Download file asynchronous, you can get the progress by OnDownloadListener.
     * @param url          The address of download url.
     * @param headers      The header of http.
     * @param downloadFile The file to download from net.
     * @param listener     The handler of download progress.
     */
    override fun download(url: String, headers: HashMap<String, String>?, downloadFile: File, listener: OnDownloadListener?) {
        OkHttpFile.download(mClient, url, headers, downloadFile, listener)
    }

    /**
     * Download file asynchronous, anyway, everything can do after download.
     */
    override fun downloadSync(url: String, headers: HashMap<String, String>?, downloadFile: File): RequestResult =
            OkHttpFile.downloadSync(mClient, url, headers, downloadFile)

    /**
     * Upload the file to net asynchronously.
     * @param params     The params of http when upload.
     * @param fileKey    The key of file upload.
     * @param listener   The listener of upload.
     */
    override fun upload(url: String, headers: HashMap<String, String>?, params: HashMap<String, String>?, fileKey: String,
                        uploadFile: File, listener: OnFileUploadListener?) {
        val file = HashMap<String, File>()
        file.put(fileKey, uploadFile)
        OkHttpFile.upload(mClient, url, headers, params, file, listener)
    }

    /**
     * Upload the file to net synchronously.
     */
    override fun uploadSync(url: String, headers: HashMap<String, String>?, params: HashMap<String, String>?, fileKey: String,
                            uploadFile: File): RequestResult {
        val file = HashMap<String, File>()
        file.put(fileKey, uploadFile)
        return OkHttpFile.uploadSync(mClient, url, headers, params, file)
    }

    /**
     * Upload the file to net asynchronously.
     */
    override fun upload(url: String, headers: HashMap<String, String>?, params: HashMap<String, String>?,
                        uploadFile: HashMap<String, File>, listener: OnFileUploadListener?) {
        OkHttpFile.upload(mClient, url, headers, params, uploadFile, listener)
    }

    /**
     * @see [uploadSync]
     */
    override fun uploadSync(url: String, headers: HashMap<String, String>?, params: HashMap<String, String>?,
                            uploadFile: HashMap<String, File>): RequestResult = OkHttpFile.uploadSync(mClient, url, headers,
            params, uploadFile)

}