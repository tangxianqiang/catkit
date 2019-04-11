package com.catkit.framework.utils

import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import com.catkit.framework.CatkitApplication

/**
 * Get the info from App you developing.
 * <p>
 *     Note: Be sure the api run after Application is running.
 * </p>
 * @author      XianQiang Tang
 * @see         [CatkitApplication.onCreate]
 */
object AppUtil {
    /**
     * Get the app package name
     */
    fun getPackageName(): String {
        val ctx = CatkitApplication.appCtx
        return try {
            val packageManager = ctx.packageManager
            val packageInfo = packageManager.getPackageInfo(ctx.packageName, 0)
            packageInfo?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    /**
     * Bellow Android 28, it is versionCode return.
     * @return -1: error
     */
    @Suppress("DEPRECATION")
    fun getVersionCode(): Long {
        val ctx = CatkitApplication.appCtx
        return try {
            val packageManager = ctx.packageManager
            val packageInfo = packageManager.getPackageInfo(ctx.packageName, 0)
            if (Build.VERSION.SDK_INT > 27) {
                packageInfo?.longVersionCode ?: -1
            } else {
                packageInfo?.versionCode?.toLong() ?: -1
            }
        } catch (e: PackageManager.NameNotFoundException) {
            -1
        }
    }

    /**
     * @return "": error
     */
    fun getVersionName(): String {
        val ctx = CatkitApplication.appCtx
        return try {
            val packageManager = ctx.packageManager
            val packageInfo = packageManager.getPackageInfo(ctx.packageName, 0)
            packageInfo?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    /**
     * Bellow Android 28, it is signatures return, or apkContentsSigners.
     * @return null: error
     */
    @Suppress("DEPRECATION")
    fun getSignature(): Array<Signature>? {
        val ctx = CatkitApplication.appCtx
        return try {
            val packageManager = ctx.packageManager
            if (Build.VERSION.SDK_INT > 27) {
                val packageInfo = packageManager.getPackageInfo(ctx.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                packageInfo?.signingInfo?.apkContentsSigners
            } else {
                val packageInfo = packageManager.getPackageInfo(ctx.packageName, @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES)
                packageInfo?.signatures
            }
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}
