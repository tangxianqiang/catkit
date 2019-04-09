package com.catkit.framework.crash

import android.app.Application

/**
 * Thread catch exception if it has its own UncaughtExceptionHandler.
 * Or ThreadGroup will do it. Otherwise it done by default.
 * <p>This class is instanced in Application. AppCrashHandler only writes
 * errors to local file not sends to server.
 * </p>
 * @author      XianQiang Tang
 * @see         Thread.UncaughtExceptionHandler
 * @version     1.0.0
 */
internal class AppCrashHandler(private val appCtx: Application) : Thread.UncaughtExceptionHandler {
    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    companion object {
        /**
         * Init instance and to be sure init method called.
         */
        fun initCrasher(app: Application) {
            AppCrashHandler(app)
        }
    }

    override fun uncaughtException(t: Thread?, e: Throwable?) {


    }
}