package com.catkit.framework.utils

import java.io.Closeable
import java.io.File

object FileUtil {
    /**
     * Create a dir file.
     * @param fullPath the dir full string path.
     * @param isNew    delete the dir if the dir is exist when true set.
     */
    fun createFileDir(fullPath: String, isNew: Boolean): Boolean {
        if (isPathInvalid(fullPath)) {
            return false
        }
        val fileDir = File(fullPath)
        return if (fileDir.exists()) {
            if (fileDir.isDirectory) {
                if (isNew) {
                    fileDir.delete()
                    fileDir.mkdirs()
                } else {
                    true
                }
            } else {
                false
            }
        } else {
            fileDir.mkdirs()
        }
    }

    /**
     * Create a file.
     * @param fullPath the file full string path.
     * @param isNew    delete the file if the file is exist when true set.
     */
    fun createFile(fullPath: String, isNew: Boolean): Boolean {
        if (isPathInvalid(fullPath)) {
            return false
        }
        val file = File(fullPath)
        return if (file.exists()) {
            if (file.isFile) {
                if (isNew) {
                    file.delete()
                    file.createNewFile()
                } else {
                    true
                }
            } else {
                false
            }
        } else {
            file.createNewFile()
        }

    }

    /**
     * Write a string to file.
     * @param fullPath the file full string path.
     * @param isAppend append data to the end of file when set true.
     * @param data     a string data.
     */
    fun writeFile(fullPath: String, isAppend: Boolean, data: String) {
        FileUtil.writeFile(fullPath, isAppend, data.toByteArray())
    }

    /**
     * Write a string to file.[kotlin.io]
     * @param fullPath the file full string path.
     * @param isAppend append data to the end of file when set true.
     * @param data     a ByteArray data.
     */
    fun writeFile(fullPath: String, isAppend: Boolean, data: ByteArray) {
        val file = File(fullPath)
        if (isAppend) {
            file.appendBytes(data)
        } else {
            file.writeBytes(data)
        }
    }

    /**
     * Get text by path.[kotlin.io]
     */
    fun readFileString(fullPath: String): String {
        val file = File(fullPath)
        return file.readText()
    }

    /**
     * Get ByteArray by path.[kotlin.io]
     */
    fun readFileByte(fullPath: String): ByteArray {
        val file = File(fullPath)
        return file.readBytes()
    }

    /**
     * The '\r', '\n', '\t', 'tab' are invalid as file path. [File.isInvalid]
     */
    fun isPathInvalid(filePath: String): Boolean {
        filePath.forEach { c ->
            if (c.isWhitespace()) {
                return true
            }
        }
        return false
    }

    /**
     * @param io file or net io
     */
    fun close(io: Closeable?) {
        io?.let {
            try {
                it.close()
            } catch (e: Exception) {

            }
        }
    }
}