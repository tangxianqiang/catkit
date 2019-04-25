package com.catkit.framework.net.http.okhttp

import java.io.File

interface OnDownloadListener {

    fun onFailure(error: RequestResult)

    fun onProgress(current: Long, total: Long)

    fun onSuccess(file: File)
}