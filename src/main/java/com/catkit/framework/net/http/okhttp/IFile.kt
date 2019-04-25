package com.catkit.framework.net.http.okhttp

import java.io.File

interface IFile {
    /**
     * Download file asynchronous, you can get the progress by OnDownloadListener.
     * @param url          The address of download url.
     * @param headers      The header of http.
     * @param downloadFile The file to download from net. The file most be create before.
     * @param listener     The handler of download progress.
     */
    fun download(url: String, headers: HashMap<String, String>? = null, downloadFile: File,
                 listener: OnDownloadListener? = null)

    /**
     * Download file asynchronous, anyway, everything can do after download.
     */
    fun downloadSync(url: String, headers: HashMap<String, String>? = null, downloadFile: File): RequestResult

    /**
     * Upload the file to net asynchronously.
     * @param params     The params of http when upload.
     * @param fileKey    The key of file upload.
     * @param listener   The listener of upload.
     * @param uploadFile The file must be create before.
     */
    fun upload(url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null,
               fileKey: String, uploadFile: File, listener: OnFileUploadListener? = null)

    /**
     * Upload the file to net synchronously.
     */
    fun uploadSync(url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null,
                   fileKey: String, uploadFile: File): RequestResult

    /**
     * Upload the file to net asynchronously.
     */
    fun upload(url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null,
               uploadFile: HashMap<String, File>, listener: OnFileUploadListener? = null)

    /**
     * @see [uploadSync]
     */
    fun uploadSync(url: String, headers: HashMap<String, String>? = null, params: HashMap<String, String>? = null,
                   uploadFile: HashMap<String, File>): RequestResult
}