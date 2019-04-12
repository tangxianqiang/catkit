package com.catkit.framework.config

abstract class BaseConfig{
    /**
     * Do something before the config info created.
     */
    abstract fun onCreate()

    /**
     * Reset config info.
     */
    abstract fun onReset()

    /**
     * clean the data you want delete.
     */
    abstract fun onDestroy()
}