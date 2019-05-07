package com.catkit.framework.permission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * The empty view content activity for asking permissions.
 */
class PermissionRequestActivity : AppCompatActivity(), PermissionCallback {

    companion object {
        private val KEY_REQUEST_CODE = "key_request_code"
        private val KEY_RATIONALE = "key_rationale"
        private val KEY_REQUEST_FROM_SETTING = "key_request_from_setting"
        private val KEY_PERMISSIONS = "key_permissions"
        private val KEY_CALLBACK_TOKEN = "key_callback_token"

        private val mCallback = mutableMapOf<String, PermissionCallback>()

        fun start(context: Context, requestCode: Int, rationale: String, requestFromSetting: Boolean,
                  callback: PermissionCallback, vararg permissions: String) {
            val callbackToken = Random().nextLong().toString()
            mCallback.put(callbackToken, callback)

            val intent = Intent(context, PermissionRequestActivity::class.java)
            intent.putExtra(KEY_REQUEST_CODE, requestCode)
            intent.putExtra(KEY_RATIONALE, rationale)
            intent.putExtra(KEY_REQUEST_FROM_SETTING, requestFromSetting)
            intent.putStringArrayListExtra(KEY_PERMISSIONS, ArrayList(permissions.toList()))
            intent.putExtra(KEY_CALLBACK_TOKEN, callbackToken)
            context.startActivity(intent)
        }
    }

    private var permissionImpl: Permission? = null
    private var mPermissionSize = 0
    private var completedSize = 0
    private var mRequestCode = 0
    private var callbackToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = intent.getStringArrayListExtra(KEY_PERMISSIONS)
        if (permissions == null || permissions.size == 0) {
            finish()
            return
        }

        mPermissionSize = permissions.size
        mRequestCode = Math.max(intent.getIntExtra(KEY_REQUEST_CODE, 0),
                PermissionSettingsDialog.DEFAULT_SETTINGS_REQ_CODE)
        val rationale = intent.getStringExtra(KEY_RATIONALE)
        val requestFromSetting = intent.getBooleanExtra(KEY_REQUEST_FROM_SETTING, true)
        callbackToken = intent.getStringExtra(KEY_CALLBACK_TOKEN)

        completedSize = 0
        permissionImpl = Permission.newInstance(this, this)
        permissionImpl?.requestPermissions(mRequestCode, rationale, requestFromSetting, *permissions.toTypedArray())
    }

    override fun appSettingsDialogBuild(permissions: List<String>, build: PermissionSettingsDialog.Builder) {
        mCallback[callbackToken]?.appSettingsDialogBuild(permissions, build)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionImpl?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionImpl?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        mCallback[callbackToken]?.onPermissionsGranted(requestCode, perms)
        checkCompleted(perms.size)
    }

    override fun onPermissionsRefused(requestCode: Int, perms: MutableList<String>) {
        mCallback[callbackToken]?.onPermissionsRefused(requestCode, perms)
        checkCompleted(perms.size)
    }

    @Synchronized
    private fun checkCompleted(currentSize: Int) {
        completedSize += currentSize
        if (mPermissionSize == completedSize) {
            finish()
        }
    }

    @Suppress("ConvertLambdaToReference")
    override fun onDestroy() {
        super.onDestroy()
        permissionImpl = null
        mCallback.remove(callbackToken)
    }

}
