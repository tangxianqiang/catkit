package com.catkit.framework.common

import java.io.InputStream
import java.io.OutputStream
import java.io.Reader

inline fun <reified T : InputStream> T.content(contentAction: (buffer: ByteArray, size: Int) -> Unit) {
    contentBy({ buffer, size -> contentAction(buffer, size).run { true } }, 1024 * 4)
}

inline fun <reified T : InputStream> T.content(contentAction: (buffer: ByteArray, size: Int) -> Unit, bufferSize: Int) {
    contentBy({ buffer, size -> contentAction(buffer, size).run { true } }, bufferSize)
}

inline fun <reified T : InputStream> T.contentBy(contentAction: (buffer: ByteArray, size: Int) -> Boolean) = contentBy(
        { buffer, size -> contentAction(buffer, size).run { true } }, 1024 * 4)

inline fun <reified T : InputStream> T.contentBy(contentAction: (buffer: ByteArray, size: Int) -> Boolean,
                                                 bufferSize: Int) {
    val buffer = ByteArray(bufferSize)
    var numRead: Int
    while (true) {
        numRead = read(buffer)
        if (numRead == -1 || !contentAction(buffer, numRead)) {
            break
        }
    }
}

inline fun <reified T : Reader> T.content(contentAction: (buffer: CharArray, size: Int) -> Unit) {
    contentBy({ buffer, size -> contentAction(buffer, size).run { true } }, 1024 * 4)
}

inline fun <reified T : Reader> T.content(contentAction: (buffer: CharArray, size: Int) -> Unit, bufferSize: Int) {
    contentBy({ buffer, size -> contentAction(buffer, size).run { true } }, bufferSize)
}

inline fun <reified T : Reader> T.contentBy(contentAction: (buffer: CharArray, size: Int) -> Boolean) = contentBy(
        { buffer, size -> contentAction(buffer, size).run { true } }, 1024 * 4)

inline fun <reified T : Reader> T.contentBy(contentAction: (buffer: CharArray, size: Int) -> Boolean, bufferSize: Int) {
    val buffer = CharArray(bufferSize)
    var numRead: Int
    while (true) {
        numRead = read(buffer)
        if (numRead == -1 || !contentAction(buffer, numRead)) {
            break
        }
    }
}

inline fun <reified T : Reader> T.content(contentAction: (line: String) -> Boolean) {
    contentBy { line -> contentAction(line).run { true } }
}

inline fun <reified T : Reader> T.contentBy(contentAction: (line: String) -> Boolean) {
    var line: String?
    while (true) {
        line = readLine()
        if (line == null || !contentAction(line)) {
            break
        }
    }
}

inline fun <reified T : Reader> T?.closeIO() {
    this?.close()
}

inline fun <reified T : InputStream> T?.closeIO() {
    this?.close()
}

inline fun <reified T : OutputStream> T?.closeIO() {
    this?.close()
}