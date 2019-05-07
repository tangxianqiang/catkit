package com.catkit.framework.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import pub.devrel.easypermissions.EasyPermissions
import android.support.v4.app.Fragment as FragmentV4

/**
 * The permission base class.
 * To get permission：[requestPermissions]. It will call back the result of the [Callback.onPermissionsGranted] method
 * after permission request if request successfully. Otherwise, [Callback.onPermissionsRefused]
 * [hasPermissions] let you check some permission.
 *
 * There are permissions which need not to request.
 * [android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS]
 * [android.Manifest.permission.ACCESS_NETWORK_STATE]
 * [android.Manifest.permission.ACCESS_NOTIFICATION_POLICY]
 * [android.Manifest.permission.ACCESS_WIFI_STATE]
 * [android.Manifest.permission.BLUETOOTH]
 * [android.Manifest.permission.BLUETOOTH_ADMIN]
 * [android.Manifest.permission.BROADCAST_STICKY]
 * [android.Manifest.permission.CHANGE_NETWORK_STATE]
 * [android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE]
 * [android.Manifest.permission.CHANGE_WIFI_STATE]
 * [android.Manifest.permission.DISABLE_KEYGUARD]
 * [android.Manifest.permission.EXPAND_STATUS_BAR]
 * [android.Manifest.permission.GET_PACKAGE_SIZE]
 * [android.Manifest.permission.INSTALL_SHORTCUT]
 * [android.Manifest.permission.INTERNET]
 * [android.Manifest.permission.KILL_BACKGROUND_PROCESSES]
 * [android.Manifest.permission.MODIFY_AUDIO_SETTINGS]
 * [android.Manifest.permission.NFC]
 * [android.Manifest.permission.READ_SYNC_SETTINGS]
 * [android.Manifest.permission.READ_SYNC_STATS]
 * [android.Manifest.permission.RECEIVE_BOOT_COMPLETED]
 * [android.Manifest.permission.REORDER_TASKS]
 * [android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS]
 * [android.Manifest.permission.REQUEST_INSTALL_PACKAGES]
 * [android.Manifest.permission.SET_ALARM]
 * [android.Manifest.permission.SET_TIME_ZONE]
 * [android.Manifest.permission.SET_WALLPAPER]
 * [android.Manifest.permission.SET_WALLPAPER_HINTS]
 * [android.Manifest.permission.TRANSMIT_IR]
 * [android.Manifest.permission.UNINSTALL_SHORTCUT]
 * [android.Manifest.permission.USE_FINGERPRINT]
 * [android.Manifest.permission.VIBRATE]
 * [android.Manifest.permission.WAKE_LOCK]
 * [android.Manifest.permission.WRITE_SYNC_SETTINGS]
 *
 * adb shell pm list permissions -d -g
 *
 */
abstract class Permission {

    companion object {
        internal fun newInstance(base: Activity, callback: PermissionCallback): Permission = ActivityImpl(base, callback)

        internal fun newInstance(base: FragmentV4, callback: PermissionCallback): Permission = FragmentV4Impl(base, callback)

        /**
         * Check permission just. so, actually, it uses the google example called EasyPermissions.
         */
        fun hasPermissions(context: Context, vararg permissions: String): Boolean = EasyPermissions.hasPermissions(
                context, *permissions)

        /**
         * Request permission.
         */
        fun requestPermissions(context: Context, requestCode: Int, rationale: String, requestFromSetting: Boolean,
                               callback: PermissionCallback, vararg permissions: String) {
            PermissionRequestActivity.start(context, requestCode, rationale, requestFromSetting, callback, *permissions)
        }
    }

    /**
     * Check permission just.
     */
    abstract fun hasPermissions(vararg permissions: String): Boolean

    /**
     * Request permission.
     */
    abstract fun requestPermissions(requestCode: Int, rationale: String, requestFromSetting: Boolean,
                                    vararg permissions: String)

    abstract fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)

    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

}