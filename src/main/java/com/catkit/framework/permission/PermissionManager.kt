package com.catkit.framework.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import pub.devrel.easypermissions.EasyPermissions

/**
 * Use [EasyPermissions]https://github.com/googlesamples/easypermissions to request permission.
 * @param T support only[Activity],[android.support.v4.app.Fragment].
 * <in T> means that let super type in sub type. There is base [Permission] exist. so, we use it
 * rather than [PermissionManager] in code.
 * @see [com.catkit.framework.ui.activity.BaseActivity]
 */
abstract class PermissionManager<in T>(private val base: T,
                                       private val callback: PermissionCallback) :
        Permission(), EasyPermissions.PermissionCallbacks {

    /**
     * Go setting if set true.
     */
    private var requestFromSetting = true

    /**
     * Permission need not or owned.
     */
    private val notNeedRequestList = ArrayList<String>()

    /**
     * Permission to request.
     */
    private val needRequestList = ArrayList<String>()

    private var requestCode: Int = 0

    override fun hasPermissions(vararg permissions: String) = EasyPermissions.hasPermissions(getContext(base),
            *permissions)

    abstract fun getContext(base: T): Context

    override fun requestPermissions(requestCode: Int, rationale: String, requestFromSetting: Boolean,
                                    vararg permissions: String) {
        this.requestFromSetting = requestFromSetting
        this.requestCode = 0

        // split the owned permission.
        permissions.partition { hasPermissions(it) }.run {
            notNeedRequestList.apply { clear() }.addAll(first)
            needRequestList.apply { clear() }.addAll(second)
        }

        if (needRequestList.isEmpty()) {
            onPermissionsGranted(requestCode, ArrayList(notNeedRequestList))
            notNeedRequestList.clear()
        } else {
            onRequestPermissions(base, requestCode, rationale, *needRequestList.toTypedArray())
        }
    }

    abstract fun onRequestPermissions(base: T, requestCode: Int, rationale: String, vararg perms: String)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        needRequestList.partition { hasPermissions(it) }.run {
            notNeedRequestList.addAll(first)
            needRequestList.apply { clear() }.addAll(second)
        }

        if (needRequestList.isEmpty()) {
            onPermissionsGranted(requestCode, ArrayList(notNeedRequestList))
            notNeedRequestList.clear()
        } else {
            if (requestFromSetting && somePermissionPermanentlyDenied(base, needRequestList)) {
                this.requestCode = requestCode
                val build = createDialogBuilder(base)
                callback.appSettingsDialogBuild(listOf(*needRequestList.toTypedArray()), build)
                build.setRequestCode(requestCode)
                build.build().show()
            } else {
                if (notNeedRequestList.isNotEmpty()) {
                    onPermissionsGranted(requestCode, ArrayList(notNeedRequestList))
                    notNeedRequestList.clear()
                }
                onPermissionsRefused(requestCode, ArrayList(needRequestList))
                needRequestList.clear()
            }
        }
    }

    abstract fun somePermissionPermanentlyDenied(base: T, deniedList: ArrayList<String>): Boolean

    abstract fun createDialogBuilder(base: T): PermissionSettingsDialog.Builder

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        callback.onPermissionsGranted(requestCode, perms)
    }

    private fun onPermissionsRefused(requestCode: Int, perms: MutableList<String>) {
        callback.onPermissionsRefused(requestCode, perms)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (this.requestCode != requestCode) {
            return
        }

        if (notNeedRequestList.isNotEmpty()) {
            needRequestList.addAll(notNeedRequestList)
            notNeedRequestList.clear()
        }

        val result = needRequestList.partition { hasPermissions(it) }
        needRequestList.clear()

        if (result.first.isNotEmpty()) {
            onPermissionsGranted(requestCode, ArrayList(result.first))
        }

        if (result.second.isNotEmpty()) {
            onPermissionsRefused(requestCode, ArrayList(result.second))
        }
    }
}
