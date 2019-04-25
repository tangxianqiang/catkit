package com.catkit.framework.net.http.okhttp

import com.catkit.framework.common.closeIO
import com.catkit.framework.common.content
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object OkHttpFile {
    private val MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream")
    /**
     * Download file asynchronous, you can get the progress by OnDownloadListener.
     * @param url          The address of download url.
     * @param headers      The header of http.
     * @param downloadFile The file to download from net.
     * @param listener     The handler of download progress.
     */
    fun download(mClient: OkHttpClient, url: String, headers: HashMap<String, String>?, downloadFile: File, listener: OnDownloadListener?) {
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        try {
            mClient.newCall(builder.build()).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    listener?.onFailure(RequestResult(HttpException(e)))
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val proxy = object : OnDownloadListener {
                        override fun onFailure(error: RequestResult) {
                            listener?.onFailure(error)
                        }

                        override fun onProgress(current: Long, total: Long) {
                            listener?.onProgress(current, total)
                        }

                        override fun onSuccess(file: File) {
                            listener?.onSuccess(file)
                        }
                    }
                    readFile(response, downloadFile, proxy, false)
                }
            })
        } catch (e: Exception) {
            listener?.onFailure(RequestResult(HttpException(e)))
        }
    }

    /**
     * Download file asynchronous, anyway, everything can do after download.
     */
    fun downloadSync(mClient: OkHttpClient, url: String, headers: HashMap<String, String>?, downloadFile: File): RequestResult {
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        return try {
            readFile(mClient.newCall(builder.build()).execute(), downloadFile, null, false)
        } catch (e: Exception) {
            RequestResult(HttpException(e))
        }
    }

    /**
     * Upload the file to net asynchronously.
     * @param params     The params of http when upload.
     * @param listener   The listener of upload.
     */
    fun upload(mClient: OkHttpClient, url: String, headers: HashMap<String, String>?, params: HashMap<String, String>?, uploadFile: HashMap<String, File>, listener: OnFileUploadListener?) {
        if (uploadFile.isEmpty()) {
            listener?.onFailure(HttpException("'uploadFile' params is invalid"))
            return
        }
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        // add params
        val fileBody = MultipartBody.Builder().apply { setType(MultipartBody.FORM) }
        params?.forEach { (key, value) -> fileBody.addFormDataPart(key, value) }
        // add files
        var path = ""
        uploadFile.forEach { (key, file) ->
            path += file.absolutePath + ", "
            fileBody.addFormDataPart(key, file.name, RequestBody.create(MEDIA_TYPE_STREAM, file))
        }
        builder.post(fileBody.build())
        try {
            mClient.newCall(builder.build()).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    listener?.onFailure(HttpException(e))
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    listener?.let {
                        val result = if (!response.isSuccessful) {
                            RequestResult(HttpException(response.code()))
                        } else {
                            RequestResult(response)
                        }
                        if (result.isSuccessful) {
                            it.onSuccess(result)
                        } else {
                            it.onFailure(HttpException(response.code()))
                        }
                    }
                }
            })
        } catch (e: Exception) {
            listener?.onFailure(HttpException(e))
        }
    }

    /**
     * Upload the file to net synchronously.
     */
    fun uploadSync(mClient: OkHttpClient, url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null, uploadFile: HashMap<String, File>): RequestResult {
        if (uploadFile.isEmpty()) {
            return RequestResult(HttpException("'uploadFile' params is invalid"))
        }
        val builder = Request.Builder().url(url)
        // add header
        headers?.forEach { (key, value) -> builder.header(key, value) }
        // add params
        val fileBody = MultipartBody.Builder().apply { setType(MultipartBody.FORM) }
        params?.forEach { (key, value) -> fileBody.addFormDataPart(key, value) }
        // add files
        var path = ""
        uploadFile.forEach { (key, file) ->
            path += file.absolutePath + ", "
            fileBody.addFormDataPart(key, file.name, RequestBody.create(MEDIA_TYPE_STREAM, file))
        }
        builder.post(fileBody.build())
        val response = mClient.newCall(builder.build()).execute()
        return try {
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
     * @param downloadFile The file most be create before.
     */
    private fun readFile(response: Response, downloadFile: File, listener: OnDownloadListener?,
                         throwException: Boolean): RequestResult {
        val result = RequestResult(response)
        if (!result.isSuccessful) {
            if (listener == null) {
                if (throwException) {
                    throw IOException(result.exception)
                }
            } else {
                listener.onFailure(result)
            }
            return result
        }
        val total = response.body()!!.contentLength()
        var input: InputStream? = null
        var out: FileOutputStream? = null
        try {
            input = response.body()!!.byteStream()
            out = FileOutputStream(downloadFile, false)
            var current = 0L
            input?.content { buffer, size ->
                out.write(buffer, 0, size)
                current += size.toLong()
                listener?.onProgress(current, total)
            }
        } catch (e: Exception) {
            if (listener == null) {
                if (throwException) {
                    throw IOException(e)
                }
            } else {
                listener.onFailure(RequestResult(HttpException(e), response))
            }

            return RequestResult(HttpException(e), response)
        } finally {
            out?.let {
                try {
                    it.flush()
                    it.fd.sync()
                    it.close()
                } catch (e: Exception) {
                }
            }
            input.closeIO()
        }

        return if (downloadFile.length() == total) {
            listener?.onSuccess(downloadFile)
            result
        } else {
            if (listener == null) {
                if (throwException) {
                    throw IOException("File length error")
                }
            } else {
                listener.onFailure(RequestResult(HttpException("File length error"), response))
            }

            RequestResult(HttpException("File length error"), response)
        }
    }

}