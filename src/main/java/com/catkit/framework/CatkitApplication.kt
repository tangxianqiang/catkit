package com.catkit.framework

import android.app.Application
import com.catkit.framework.config.BaseGlobalConfig

/**
 * Base class of application.
 * @author      XianQiang Tang
 * @see         Application
 * @version     1.0.0
 */
abstract class CatkitApplication : Application() {
    companion object {
        lateinit var appCtx: Application
        lateinit var globalConfig: BaseGlobalConfig
    }

    override fun onCreate() {
        super.onCreate()
        appCtx = this
        initGlobalConfig()
    }

    abstract fun initGlobalConfig()
}