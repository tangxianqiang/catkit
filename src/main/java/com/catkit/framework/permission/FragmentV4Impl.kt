package com.catkit.framework.permission

import android.content.Context
import android.support.v4.app.Fragment
import pub.devrel.easypermissions.EasyPermissions

/**
 * The Implement of the base permission.
 */
class FragmentV4Impl(base: Fragment, callback: PermissionCallback) : PermissionManager<Fragment>(base, callback) {

    override fun getContext(base: Fragment): Context = base.activity!!

    override fun onRequestPermissions(base: Fragment, requestCode: Int, rationale: String, vararg perms: String) {
        EasyPermissions.requestPermissions(base, rationale, requestCode, *perms)
    }

    override fun somePermissionPermanentlyDenied(base: Fragment,
                                                 deniedList: ArrayList<String>) = EasyPermissions.somePermissionPermanentlyDenied(
            base, deniedList)

    override fun createDialogBuilder(base: Fragment) = PermissionSettingsDialog.Builder(base)
}