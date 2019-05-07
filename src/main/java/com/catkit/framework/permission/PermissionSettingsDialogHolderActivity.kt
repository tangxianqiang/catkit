package com.catkit.framework.permission

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

/**
 * The holder of the dialog when ask permission.
 */
class PermissionSettingsDialogHolderActivity : AppCompatActivity(), DialogInterface.OnClickListener {

    companion object {
        fun createShowDialogIntent(context: Context, dialog: PermissionSettingsDialog): Intent {
            return Intent(context, PermissionSettingsDialogHolderActivity::class.java).putExtra(
                    PermissionSettingsDialog.EXTRA_APP_SETTINGS, dialog)
        }
    }

    private var mDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = intent.getParcelableExtra<PermissionSettingsDialog>(PermissionSettingsDialog.EXTRA_APP_SETTINGS)
        dialog.setContext(this)
        dialog.setActivityOrFragment(this)
        dialog.setNegativeListener(this)
        mDialog = dialog.showDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setResult(resultCode, data)
        finish()
    }

}
