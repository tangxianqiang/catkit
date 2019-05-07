package com.catkit.framework.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings


/**
 * Jump to setting activity of the phone.
 * @author XianQiang Tang
 */
object SettingJumpCompat {

    private const val MANUFACTURER_HUAWEI = "huawei"
    private const val MANUFACTURER_MEIZU = "meizu"
    private const val MANUFACTURER_SONY = "sony"
    private const val MANUFACTURER_OPPO = "oppo"
    private const val MANUFACTURER_LG = "lg"
    private const val MANUFACTURER_LETV = "letv"
    private const val MANUFACTURER_360 = "360"
    private const val MANUFACTURER_XIAOMI = "xiaomi"
    private const val MANUFACTURER_VIVO = "vivo"
    private const val MANUFACTURER_SAMSUNG = "samsung"
    private const val MANUFACTURER_ZTE = "zte"
    private const val MANUFACTURER_YULONG = "yulong"
    private const val MANUFACTURER_LENOVO = "lenovo"

    /**
     * Get the Intent of the Special room.
     */
    fun getSpecialSettingJumpIntent(context: Context): Intent? {
        // http://www.jianshu.com/p/b5c494dba0bc
        val pkg = context.packageName
        val pm = context.packageManager
        val sysManufacturer = android.os.Build.MANUFACTURER.toLowerCase()
        var settingIntent: Intent? = null

        if (contains(sysManufacturer, MANUFACTURER_HUAWEI)) {
            val intent = Intent()
            intent.putExtra("packageName", pkg)
            intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_MEIZU)) {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", pkg)
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_SONY)) {
            val intent = Intent()
            intent.putExtra("packageName", pkg)
            intent.component = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_OPPO)) {
            val intent = Intent()
            intent.putExtra("packageName", pkg)
            intent.component = ComponentName("com.color.safecenter",
                    "com.color.safecenter.permission.PermissionManagerActivity")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_LG)) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.putExtra("packageName", pkg)
            intent.component = ComponentName("com.android.settings",
                    "com.android.settings.Settings\$AccessLockSummaryActivity")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_LETV)) {
            val intent = Intent()
            intent.putExtra("packageName", pkg)
            intent.component = ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_360)) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.putExtra("packageName", pkg)
            intent.component = ComponentName("com.qihoo360.mobilesafe",
                    "com.qihoo360.mobilesafe.ui.index.AppEnterActivity")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            }
        } else if (contains(sysManufacturer, MANUFACTURER_XIAOMI)) {
            // v5
            var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$pkg")
            if (checkIntent(pm, intent)) {
                settingIntent = intent
            } else {
                intent = Intent("miui.intent.action.APP_PERM_EDITOR")
                intent.putExtra("extra_pkgname", pkg)

                // v6 and v7
                intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                if (checkIntent(pm, intent)) {
                    settingIntent = intent
                } else {
                    // v8
                    intent.setClassName("com.miui.securitycenter",
                            "com.miui.permcenter.permissions.PermissionsEditorActivity")
                    if (checkIntent(pm, intent)) {
                        settingIntent = intent
                    }
                }
            }
        } else if (contains(sysManufacturer, MANUFACTURER_VIVO)) {
        } else if (contains(sysManufacturer, MANUFACTURER_SAMSUNG)) {
        } else if (contains(sysManufacturer, MANUFACTURER_ZTE)) {
        } else if (contains(sysManufacturer, MANUFACTURER_YULONG)) {
        } else if (contains(sysManufacturer, MANUFACTURER_LENOVO)) {
        }

        return settingIntent
    }

    /**
     * Get the Intent of the normal room.
     */
    fun getCommonSettingJumpIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        return if (checkIntent(context.packageManager, intent)) intent else {
            Intent(Settings.ACTION_SETTINGS)
        }
    }

    private fun contains(sysManufacturer: String, targetManufacturer: String) = sysManufacturer.contains(
            targetManufacturer)

    private fun checkIntent(pm: PackageManager, intent: Intent) = pm.queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY)?.size ?: 0 > 0

}