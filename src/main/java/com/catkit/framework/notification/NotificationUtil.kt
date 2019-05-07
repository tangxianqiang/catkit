package com.catkit.framework.notification

import android.os.Build
import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat.getSystemService
import com.catkit.framework.CatkitApplication


object NotificationUtil {
    /**
     * <p>Must hold {@code <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" />}</p>
     */
    fun setNotificationBarVisibility(visible: Boolean) {
        val methodName: String
        if (visible) {
            methodName = if (Build.VERSION.SDK_INT <= 16) "expand" else "expandNotificationsPanel"
        } else {
            methodName = if (Build.VERSION.SDK_INT <= 16) "collapse" else "collapsePanels"
        }
        invokePanels(methodName)
    }

    fun invokePanels(methodName: String) {
        try {
            @SuppressLint("WrongConstant")
            val service = CatkitApplication.appCtx.getSystemService("statusbar")
            @SuppressLint("PrivateApi")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expand = statusBarManager.getMethod(methodName)
            expand.invoke(service)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}