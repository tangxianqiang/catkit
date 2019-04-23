package com.catkit.framework.net.http.okhttp

import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import java.io.Reader

/**
 * The base info about http response after http connect success. It means that logic error is not contains.
 */
class RequestResult {
    private var mResult: Response? = null
    var exception: HttpException? = null
        private set

    constructor(e:HttpException){
        this.exception = e
    }
    constructor(result: Response){
        this.mResult = result
    }

    val isSuccessful: Boolean
        get() = mResult?.isSuccessful ?: false

    val headers: HashMap<String, String>?
        get() {
            return try {
                val headers = mResult?.headers() ?: return null
                val size = headers.size()
                val headerMap = HashMap<String, String>(size)
                for (i in 0 until size) {
                    headerMap[headers.name(i)] = headers.value(i)
                }

                headerMap
            } catch (e: Exception) {
                null
            }
        }

    val bodyLength: Long
        get() = mResult?.body()?.contentLength() ?: 0L

    val bodyString: String?
        get() = try {
            mResult?.body()?.string()
        } catch (e: Exception) {
            null
        }

    val bodyBytes: ByteArray?
        get() = try {
            mResult?.body()?.bytes()
        } catch (e: Exception) {
            null
        }

    val bodyByteStream: InputStream?
        @Throws(IOException::class) get() = mResult?.body()?.byteStream()

    val bodyCharStream: Reader?
        @Throws(IOException::class) get() = mResult?.body()?.charStream()

    val code: Int
        get() = mResult?.code() ?: -1
}