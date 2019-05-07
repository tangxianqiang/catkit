package com.catkit.framework.permission

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import com.catkit.framework.utils.SettingJumpCompat
import android.support.v4.app.Fragment as FragmentV4
import pub.devrel.easypermissions.R as EasyR

/**
 * The dialog when ask permission.
 */
class PermissionSettingsDialog : Parcelable, DialogInterface.OnClickListener {

    companion object {
        val DEFAULT_SETTINGS_REQ_CODE = 16061
        internal val EXTRA_APP_SETTINGS = "extra_app_settings"

        @JvmField
        val CREATOR = object : Parcelable.Creator<PermissionSettingsDialog> {
            override fun createFromParcel(inParcel: Parcel) = PermissionSettingsDialog(inParcel)
            override fun newArray(size: Int): Array<PermissionSettingsDialog?> = arrayOfNulls(size)
        }
    }

    private var mRationale: String
    private var mTitle: String
    private var mPositiveButtonText: String
    private var mNegativeButtonText: String
    private var mRequestCode: Int
    private var mActivityOrFragment: Any? = null
    private var mNegativeListener: DialogInterface.OnClickListener? = null
    private lateinit var mContext: Context

    private constructor(inParcel: Parcel) {
        mRationale = inParcel.readString()
        mTitle = inParcel.readString()
        mPositiveButtonText = inParcel.readString()
        mNegativeButtonText = inParcel.readString()
        mRequestCode = inParcel.readInt()
    }

    private constructor(activityOrFragment: Any, context: Context, rationale: String, title: String,
                        positiveButtonText: String, negativeButtonText: String, requestCode: Int) {
        mActivityOrFragment = activityOrFragment
        mContext = context
        mRationale = rationale
        mTitle = title
        mPositiveButtonText = positiveButtonText
        mNegativeButtonText = negativeButtonText
        mRequestCode = requestCode
    }

    internal fun setActivityOrFragment(activityOrFragment: Any) {
        mActivityOrFragment = activityOrFragment
    }

    internal fun setContext(context: Context) {
        mContext = context
    }

    internal fun setNegativeListener(negativeListener: DialogInterface.OnClickListener) {
        mNegativeListener = negativeListener
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private fun startForResult(intent: Intent) {
        val obj = mActivityOrFragment
        when (obj) {
            is Activity -> obj.startActivityForResult(intent, mRequestCode)
            is Fragment -> obj.startActivityForResult(intent, mRequestCode)
            is FragmentV4 -> obj.startActivityForResult(intent, mRequestCode)
        }
    }

    /**
     * Display the built dialog.
     */
    fun show() {
        //        if (mNegativeListener == null) {
        startForResult(PermissionSettingsDialogHolderActivity.createShowDialogIntent(mContext, this))
        //        } else {
        //            // We can't pass the cancel listener to an activity so we default to old behavior it there is one.
        //            // This ensures backwards compatibility.
        //            showDialog()
        //        }
    }

    /**
     * Show the dialog. [show] is a wrapper to ensure backwards compatibility
     */
    internal fun showDialog(): AlertDialog {
        return AlertDialog.Builder(mContext).setCancelable(false).setTitle(mTitle).setMessage(
                mRationale).setPositiveButton(mPositiveButtonText, this).setNegativeButton(mNegativeButtonText,
                mNegativeListener).show()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        // Start for result
        val intent = SettingJumpCompat.getSpecialSettingJumpIntent(mContext)
        if (intent == null) {
            startForResult(SettingJumpCompat.getCommonSettingJumpIntent(mContext))
        } else {
            try {
                startForResult(intent)
            } catch (e: Exception) {
                startForResult(SettingJumpCompat.getCommonSettingJumpIntent(mContext))
            }
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(mRationale)
        dest.writeString(mTitle)
        dest.writeString(mPositiveButtonText)
        dest.writeString(mNegativeButtonText)
        dest.writeInt(mRequestCode)
    }

    /**
     * Builder for an [PermissionSettingsDialog].
     */
    class Builder : Parcelable {

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<Builder> {
                override fun createFromParcel(inParcel: Parcel) = Builder(inParcel)
                override fun newArray(size: Int): Array<Builder?> = arrayOfNulls(size)
            }
        }

        private var mActivityOrFragment: Any? = null
        private var mContext: Context? = null

        private var mRationaleId: Int = EasyR.string.rationale_ask_again
        private var mTitleId: Int = EasyR.string.title_settings_dialog
        private var mPositiveButtonId: Int = android.R.string.ok
        private var mNegativeButtonId: Int = android.R.string.cancel

        private var mRationale: String? = null
        private var mTitle: String? = null
        private var mPositiveButton: String? = null
        private var mNegativeButton: String? = null

        private var mRequestCode = -1


        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(mRationale)
            dest.writeString(mTitle)
            dest.writeString(mPositiveButton)
            dest.writeString(mNegativeButton)
            dest.writeInt(mRequestCode)
        }

        private constructor(inParcel: Parcel) {
            mRationale = inParcel.readString()
            mTitle = inParcel.readString()
            mPositiveButton = inParcel.readString()
            mNegativeButton = inParcel.readString()
            mRequestCode = inParcel.readInt()
        }

        /**
         * Create a new Builder for an [PermissionSettingsDialog].
         *
         * @param activity the [Activity] in which to display the dialog.
         */
        constructor(activity: Activity) {
            mActivityOrFragment = activity
            mContext = activity
        }

        /**
         * Create a new Builder for an [PermissionSettingsDialog].
         *
         * @param fragment the [android.support.v4.app.Fragment] in which to display the dialog.
         */
        constructor(fragment: FragmentV4) {
            mActivityOrFragment = fragment
            mContext = fragment.context
        }

        /**
         * Create a new Builder for an [PermissionSettingsDialog].
         *
         * @param fragment the [android.app.Fragment] in which to display the dialog.
         */
        constructor(fragment: Fragment) {
            mActivityOrFragment = fragment
            mContext = fragment.activity
        }

        /**
         * The [Activity] or [android.app.Fragment] or [android.support.v4.app.Fragment] in which to display the dialog
         */
        fun setActivityOrFragment(obj: Any) {
            mActivityOrFragment = obj
            mContext = when (obj) {
                is Activity -> obj
                is Fragment -> obj.activity
                is FragmentV4 -> obj.context
                else -> throw IllegalArgumentException("The obj mast is Activity or Fragment(include v4)")
            }
        }

        /**
         * Set the title dialog. Default is "Permissions Required".
         */
        fun setTitle(title: String): Builder {
            mTitle = title
            return this
        }

        /**
         * Set the title dialog. Default is "Permissions Required".
         */
        fun setTitle(@StringRes title: Int): Builder {
            mTitleId = title
            return this
        }

        /**
         * Set the rationale dialog. Default is
         * "This app may not work correctly without the requested permissions.
         * Open the app settings screen to modify app permissions."
         */
        fun setRationale(rationale: String): Builder {
            mRationale = rationale
            return this
        }

        /**
         * Set the rationale dialog. Default is
         * "This app may not work correctly without the requested permissions.
         * Open the app settings screen to modify app permissions."
         */
        fun setRationale(@StringRes rationale: Int): Builder {
            mRationaleId = rationale
            return this
        }

        /**
         * Set the positive button text, default is [android.R.string.ok].
         */
        fun setPositiveButton(positiveButton: String): Builder {
            mPositiveButton = positiveButton
            return this
        }

        /**
         * Set the positive button text, default is [android.R.string.ok].
         */
        fun setPositiveButton(@StringRes positiveButton: Int): Builder {
            mPositiveButtonId = positiveButton
            return this
        }

        /**
         * Set the negative button text, default is [android.R.string.cancel].
         */
        fun setNegativeButton(negativeButton: String): Builder {
            mNegativeButton = negativeButton
            return this
        }

        /**
         * Set the negative button text, default is [android.R.string.cancel].
         */
        fun setNegativeButton(@StringRes negativeButton: Int): Builder {
            mNegativeButtonId = negativeButton
            return this
        }

        /**
         * Set the request code use when launching the Settings screen for result, can be retrieved
         * in the calling Activity's [Activity.onActivityResult] method.
         * Default is [PermissionSettingsDialog.DEFAULT_SETTINGS_REQ_CODE].
         */
        fun setRequestCode(requestCode: Int): Builder {
            mRequestCode = requestCode
            return this
        }

        internal fun copyFrom(other: Builder): Builder {
            check()
            mRationale = other.mRationale
            mTitle = other.mTitle
            mPositiveButton = other.mPositiveButton
            mNegativeButton = other.mNegativeButton
            mRequestCode = other.mRequestCode
            return this
        }

        /**
         * Build the [PermissionSettingsDialog] from the specified options. Generally followed by a
         * call to [PermissionSettingsDialog.show].
         */
        fun build(): PermissionSettingsDialog {
            check()
            return PermissionSettingsDialog(mActivityOrFragment!!, mContext!!, mRationale!!, mTitle!!,
                    mPositiveButton!!, mNegativeButton!!, mRequestCode)
        }

        private fun check() {
            if (mRationale.isNullOrEmpty()) {
                mRationale = mContext!!.getString(mRationaleId)
            }
            if (mTitle.isNullOrEmpty()) {
                mTitle = mContext!!.getString(mTitleId)
            }
            if (mPositiveButton.isNullOrEmpty()) {
                mPositiveButton = mContext!!.getString(mPositiveButtonId)
            }
            if (mNegativeButton.isNullOrEmpty()) {
                mNegativeButton = mContext!!.getString(mNegativeButtonId)
            }
            if (mRequestCode <= 0) {
                mRequestCode = DEFAULT_SETTINGS_REQ_CODE
            }
        }

    }
}