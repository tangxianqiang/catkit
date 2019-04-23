package com.catkit.framework.ui.dialog

import android.support.v4.app.DialogFragment

/**
 * BaseDialog has own lifecycle, it is different from [android.app.Dialog].
 * [DialogFragment] has own lifecycle, so it synchronizes with activity. Specially,
 * it's lifecycle will run when the direction of phone has changed. And it is also
 * used to worked as "v" in MVP model.
 */
abstract class BaseDialog : DialogFragment() {

}