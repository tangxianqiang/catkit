package com.catkit.framework.updater

import android.support.v4.app.FragmentManager
import com.catkit.framework.UpdateMode
import com.catkit.framework.ui.dialog.BaseLogicalDialog

class UpdaterManager {
    companion object {
        private var updateParams: Updater.UpdaterParams? = null

        fun build(): UpdaterManager {
            this.updateParams = Updater.UpdaterParams()
            return UpdaterManager()
        }
    }

    fun setMode(@UpdateMode mode: Int): UpdaterManager {
        updateParams?.setUpMode(mode)
        return this
    }

    fun setInstallAddress(address: String?): UpdaterManager {
        updateParams?.setInstallAddress(address)
        return this
    }

    fun setVersion(version: Int): UpdaterManager {
        updateParams?.setVersion(version)
        return this
    }

    fun setUpdateCallBack(updateAction: UpdateAction): UpdaterManager {
        updateParams?.setUpdateAction(updateAction)
        return this
    }

    fun setTipDialog(dialog: BaseLogicalDialog,fragmentManager: FragmentManager):UpdaterManager{
        updateParams?.setTipDialog(dialog,fragmentManager)
        return this
    }

    fun create(): Updater {
        val updater = Updater.instance
        updater.setParams(updateParams!!)
        return updater
    }

    interface UpdateAction {
        fun onTip()
        fun onStart()
        fun onProgress()
        fun onPause()
        fun onRestart()
        fun onCancel()
        fun onFinish()
    }
}