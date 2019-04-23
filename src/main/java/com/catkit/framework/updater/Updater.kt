package com.catkit.framework.updater

import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import com.catkit.framework.*
import com.catkit.framework.ui.dialog.BaseLogicalDialog
import com.catkit.framework.utils.AppUtil

/**
 * Update manager can check the local app's version to update it. There are four
 * things to do bellow:
 *      1. check version to get whether the local app need update;
 *      2. start a service to download new version app or jump to app store to download;
 *      3. update automatic, forcibly or manually;
 *      4. If we cache the application, we need check the local whether to download new one,
 *         or resume downloading.
 * We hope the update is happened everywhere not a specified activity. The updater is around
 * Activity whatever the mode is.
 */
class Updater {
    private var updateParams: Updater.UpdaterParams? = null

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Updater()
        }
    }

    fun setParams(params: UpdaterParams) {
        this.updateParams = params
    }

    /**
     * Start update by params set in [UpdaterManager]
     */
    fun update() {
        if (checkVersion()) {
            if (updateParams!!.getMode() == UPDATE_MANUALLY) {
                //jump to app store by intent
                return
            }
            if (updateParams!!.getUpdateAction() != null) {
                updateParams!!.getUpdateAction()!!.onTip()
            } else {
                //default tip action
                showTipDialog()
            }
        }
    }

    private fun checkVersion(): Boolean = updateParams!!.getVersion() > AppUtil.getVersionCode() && updateParams?.getInstallAddress().isNullOrEmpty()

    /**
     * If you want to change the style of the dialog. Please [UpdaterParams.setTipDialog]
     */
    private fun showTipDialog() {
        val dialog = updateParams?.getDialog()
        val fm = updateParams?.getFragmentManager()
        if (dialog != null && fm != null) {
            dialog.show(fm, "Update_Tip")
            if (updateParams?.getMode() == UPDATE_FORCIBLY) {
                return
            }
            dialog.setPositiveAction {

            }
            dialog.setNegativeAction {
                dialog.dismiss()
                updateParams = null
            }
            return
        }
        val dialogDefault = AlertDialog.Builder(CatkitApplication.appCtx)
                .setTitle(CatkitApplication.appCtx.getString(R.string.default_update_tip_title))
                .setMessage(CatkitApplication.appCtx.getString(R.string.default_update_tip_content))
                .setCancelable(true)
        if (updateParams?.getMode() == UPDATE_AUTOMATIC) {
            dialogDefault.setPositiveButton(CatkitApplication.appCtx.getString(R.string.confirm)) { dialog, which -> }
                    .setNegativeButton(CatkitApplication.appCtx.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
        }
        dialogDefault.show()
    }

    /**
     * Use [android.app.DownloadManager] downLoad application new. You can call this by [UpdaterParams.setUpdateAction] in activity or
     * anywhere. But if you do not [UpdaterParams.setUpdateAction], it will be downloaded default in service.
     */
    fun startDownload() {

    }

    /**
     * The params of update. Builder always need many parts. Params is the part of builder.
     */
    open class UpdaterParams {
        private var version: Int = 0
        @UpdateMode
        private var upMode: Int = UPDATE_MANUALLY
        private var installAddress: String? = null
        private var updateAction: UpdaterManager.UpdateAction? = null
        private var dialog: BaseLogicalDialog? = null
        private var fm: FragmentManager? = null

        fun setVersion(version: Int) {
            this.version = version
        }

        fun setUpMode(mode: Int) {
            this.upMode = mode
        }

        fun setInstallAddress(address: String?) {
            this.installAddress = address
        }

        fun setUpdateAction(callback: UpdaterManager.UpdateAction?) {
            this.updateAction = callback
        }

        fun setTipDialog(dialog: BaseLogicalDialog, fm: FragmentManager) {
            this.dialog = dialog
            this.fm = fm
        }

        fun getVersion(): Int {
            return this.version
        }

        fun getMode(): Int {
            return this.upMode
        }

        fun getInstallAddress(): String? {
            return this.installAddress
        }

        fun getUpdateAction(): UpdaterManager.UpdateAction? {
            return this.updateAction
        }

        fun getDialog(): BaseLogicalDialog? {
            return this.dialog
        }

        fun getFragmentManager(): FragmentManager? {
            return this.fm
        }
    }
}