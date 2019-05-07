package com.catkit.framework.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.view.View
import com.catkit.framework.CatkitApplication
import com.catkit.framework.permission.Permission
import com.catkit.framework.permission.PermissionCallback
import com.catkit.framework.permission.PermissionRequestActivity
import kotlinx.coroutines.*
import pub.devrel.easypermissions.EasyPermissions

/**
 * <p>
 *     <out> means that it maybe a child class copy to parent class, so it always work on interface.
 * </p>
 */
abstract class BasePresenter<out T : ViewCallback>(viewCallback: T) : PermissionCallback {
    /**
     * The luck that to make sure view callback can happened on other thread safely.
     */
    private val lock = Any()

    private var permission: Permission? = null

    private var viewCallback:ViewCallback? = viewCallback

    /**
     * The final method be called by presenter for viewer(MVP-V).
     */
    fun <R> viewCallback(action: ViewCallback.() -> R): R {
        return viewCallback.run {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this!!.action()
            } else {
                synchronized(lock) {
                    var result: R? = null
                    //to suspend the other thread.
                    runBlocking(Dispatchers.Unconfined) {
                        val jobber = CoroutineScope(Dispatchers.Main).launch {
                            //the callback called on UI-thread.
                            result = this@run!!.action()
                        }
                        //make sure the UI-coroutine completed after blocking coroutine.
                        jobber.join()
                    }
                    return result!!
                }
            }
        }
    }

    /**
     * Check the permissions.
     */
    fun hasPermissions(vararg permissions: String) = EasyPermissions.hasPermissions(CatkitApplication.appCtx,
            *permissions)

    /**
     * @see [requestPermissions]
     */
    protected open fun requestPermissions(context: Context, requestCode: Int, rationale: String,
                                          vararg permissions: String) {
        requestPermissions(context, requestCode, rationale, true, *permissions)
    }

    /**
     * If asking permissions in presenter(MVP-P), it is important that the [Context] leaking will happen.
     */
    protected open fun requestPermissions(context: Context, requestCode: Int, rationale: String,
                                          requestFromSetting: Boolean, vararg permissions: String) {
        PermissionRequestActivity.start(context, requestCode, rationale, requestFromSetting, this, *permissions)
    }

    internal fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permission?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun requestPermissions(requestCode: Int, rationale: String, requestFromSetting: Boolean,
                           vararg permissions: String) {
        if (viewCallback == null) return
        if (permission == null) {
            if (viewCallback is Activity) {
                permission = Permission.newInstance(viewCallback!! as Activity, this)
            }else if(viewCallback is Fragment){
                permission = Permission.newInstance(viewCallback!! as Fragment, this)
            }else{

            }
        }
        permission!!.requestPermissions(requestCode, rationale, requestFromSetting, *permissions)
    }

    open fun onAttach(context: Context?) {}

    open fun onAttachFragment(childFragment: Fragment?) {}

    open fun onCreate(savedInstanceState: Bundle?) {}

    open fun onViewCreated(view: View?, savedInstanceState: Bundle?) {}

    open fun onActivityCreated(savedInstanceState: Bundle?) {}

    open fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {}

    open fun onStart() {}

    open fun onNewIntent(intent: Intent?) {}

    open fun onResume() {}

    open fun onPause() {}

    open fun onStop() {}

    open fun onSaveInstanceState(outState: Bundle?) {}

    open fun onRestoreInstanceState(savedInstanceState: Bundle?) {}

    open fun onDetach() {}

    open fun onDestroyView() {}

    fun onDestroy() {
        permission = null
        viewCallback = null
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        permission?.onActivityResult(requestCode, resultCode, data)
    }
}