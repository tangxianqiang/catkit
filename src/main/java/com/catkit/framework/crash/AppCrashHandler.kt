package com.catkit.framework.crash

import android.annotation.SuppressLint
import android.app.Application
import android.os.Environment
import android.support.annotation.RequiresPermission
import com.catkit.framework.DEFAULT_ERROR_LOCAL_PATH_EXTRA
import com.catkit.framework.utils.DeviceUtil
import com.catkit.framework.utils.FileUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*

/**
 * Thread catch exception if it has its own UncaughtExceptionHandler.
 * Or ThreadGroup will do it. Otherwise it done by default.
 * <p>
 *     Note: This class is instanced in Application. AppCrashHandler only writes
 *     errors to local file not sends to server.
 * </p>
 * @author      XianQiang Tang
 * @see         Thread.UncaughtExceptionHandler
 * @version     1.0.0
 */
internal class AppCrashHandler(private val appCtx: Application, private val crashLocalDir: String) : Thread.UncaughtExceptionHandler {

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    companion object {
        /**
         * Init instance and to be sure init method called.
         */
        fun initCrasher(app: Application, crashFile: String) {
            AppCrashHandler(app, crashFile)
        }
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        if (e == null) {
            killProgressByError()
            return
        }
        val time = SimpleDateFormat("MM-dd HH-mm-ss").format(Date(System.currentTimeMillis()))
        val errorInfo = StringBuilder()
        errorInfo.append("Crash Time: = ").append(time).append("\r\n")
                .append(DeviceUtil.getDeviceInfo()).append("\r\n")
                .append(ThrowableUtil.getFullStackTrace(e)).append("\r\n")
        //write error to local file
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            if (FileUtil.isPathInvalid(crashLocalDir)) {
                if (appCtx.externalCacheDir != null) {
                    //write to file to Android/data/packageName by named default
                    val filePath = "${appCtx.externalCacheDir!!.absolutePath}${File.separator}$DEFAULT_ERROR_LOCAL_PATH_EXTRA${File.separator}$time"
                    if (FileUtil.createFile(filePath, true)) {
                        writeCrashToLocal(errorInfo.toString(), filePath)
                    }
                } else {
                    //write to file only to data/data/packageName by named default
                    val filePath = "${appCtx.cacheDir.absolutePath}${File.separator}$DEFAULT_ERROR_LOCAL_PATH_EXTRA${File.separator}$time"
                    if (FileUtil.createFile(filePath, true)) {
                        writeCrashToLocal(errorInfo.toString(), filePath)
                    }
                }
            } else {
                val filePath = "$crashLocalDir${File.separator}$time"
                if (FileUtil.createFile(filePath, true)) {
                    writeCrashToLocal(errorInfo.toString(), filePath)
                }
            }
        } else {
            //write to file only to data/data/packageName by named default
            val filePath = "${appCtx.cacheDir.absolutePath}${File.separator}$DEFAULT_ERROR_LOCAL_PATH_EXTRA${File.separator}$time"
            if (FileUtil.createFile(filePath, true)) {
                writeCrashToLocal(errorInfo.toString(), filePath)
            }
        }
    }

    /**
     * Write info to local by other thread. Besides, Future's get can let you to find the callable's
     * return immediately.
     */
    private fun writeCrashToLocal(crashInfo: String, fullPath: String): Boolean {
        val submit: Future<Boolean> = Executors.newSingleThreadExecutor().submit(object : Callable<Boolean> {
            override fun call(): Boolean {
                return try {
                    FileUtil.writeFile(fullPath, false, crashInfo)
                    true
                } catch (e: Exception) {
                    false
                }
            }

        })
        return try {
            submit.get()
        } catch (e: InterruptedException) {
            false
        } catch (e: ExecutionException) {
            false
        }
    }

    /**
     * Exit progress by error
     */
    private fun killProgressByError() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    /**
     * Terminate progress, so it is not a good way.
     */
    private fun killProgressByTerminate() {
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }
}