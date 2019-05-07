package com.catkit.framework.permission

import android.app.Activity
import pub.devrel.easypermissions.EasyPermissions

/**
 * The Implement of the base permission.
 */
class ActivityImpl(base: Activity, callback: PermissionCallback) : PermissionManager<Activity>(base, callback) {

    override fun getContext(base: Activity) = base

    override fun onRequestPermissions(base: Activity, requestCode: Int, rationale: String, vararg perms: String) {
        EasyPermissions.requestPermissions(base, rationale, requestCode, *perms)
    }

    override fun somePermissionPermanentlyDenied(base: Activity,
                                                 deniedList: ArrayList<String>) = EasyPermissions.somePermissionPermanentlyDenied(
            base, deniedList)

    override fun createDialogBuilder(base: Activity) = PermissionSettingsDialog.Builder(base)
}