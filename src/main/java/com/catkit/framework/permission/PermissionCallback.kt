package com.catkit.framework.permission

interface PermissionCallback {
    /**
     * The builder of setting dialog(When user refused the permission, the setting dialog will be chosen.)
     */
    fun appSettingsDialogBuild(permissions: List<String>, build: PermissionSettingsDialog.Builder)

    /**
     * Success callback.
     */
    fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>)

    /**
     * Failure callback.
     */
    fun onPermissionsRefused(requestCode: Int, perms: MutableList<String>)
}