package com.catkit.framework.ui.activity

import android.graphics.Color
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

abstract class BaseFragmentActivity : FragmentActivity() {

    companion object {
        private val TAG_STATUS_BAR = "TAG_STATUS_BAR"
        private val TAG_OFFSET = "TAG_OFFSET"
        private val KEY_OFFSET = -123
    }

    fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun setStatusBarVisibility(visible: Boolean) {
        if (visible) {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //show the status bar by window
            window?.decorView?.findViewWithTag<View>(TAG_STATUS_BAR)?.visibility = View.VISIBLE
            // add top margin for window
            addTopMargin()
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window?.decorView?.findViewWithTag<View>(TAG_STATUS_BAR)?.visibility = View.GONE
            subTopMargin()
        }
    }

    fun addTopMargin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        val view = window.decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
        view.tag = TAG_OFFSET
        val haveSetOffset = view.getTag(KEY_OFFSET)
        if (haveSetOffset != null && haveSetOffset as Boolean) return
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin + getStatusBarHeight(),
                layoutParams.rightMargin,
                layoutParams.bottomMargin)
        view.setTag(KEY_OFFSET, true)
    }

    fun subTopMargin() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        val view = window.decorView.findViewWithTag<View>(TAG_OFFSET) ?: return
        view.tag = TAG_OFFSET
        val haveSetOffset = view.getTag(KEY_OFFSET)
        if (haveSetOffset != null && haveSetOffset as Boolean) return
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin - getStatusBarHeight(),
                layoutParams.rightMargin,
                layoutParams.bottomMargin)
        view.setTag(KEY_OFFSET, false)
    }

    fun setStatusBarLightMode(isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            if (decorView != null) {
                var vis = decorView.systemUiVisibility
                if (isLightMode) {
                    vis = vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis = vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                decorView.systemUiVisibility = vis
            }
        }
    }

    fun setStatusBarColor(isDecor: Boolean, color: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        transparentStatusBar()
        val parent: ViewGroup = if (isDecor)
            window.decorView as ViewGroup
        else
            findViewById(android.R.id.content)
        var fakeStatusBarView: View? = parent.findViewWithTag(TAG_STATUS_BAR)
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.visibility == View.GONE) {
                fakeStatusBarView.visibility = View.VISIBLE
            }
            fakeStatusBarView.setBackgroundColor(color)
        } else {
            fakeStatusBarView = createStatusBarView(color)
            parent.addView(fakeStatusBarView)
        }
    }

    fun createStatusBarView(color: Int): View {
        val statusBarView = View(this)
        statusBarView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight())
        statusBarView.setBackgroundColor(color)
        statusBarView.tag = TAG_STATUS_BAR
        return statusBarView
    }

    fun transparentStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val vis = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility = option or vis
            } else {
                window.decorView.systemUiVisibility = option
            }
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun getActionBarHeight(): Int {
        val tv = TypedValue()
        return if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(
                    tv.data, resources.displayMetrics
            )
        } else 0
    }

    fun getNavBarHeight(): Int {
        val res = resources
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    fun setNavBarVisibility(visible: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return
        val decorView = window.decorView as ViewGroup
        var i = 0
        val count = decorView.childCount
        while (i < count) {
            val child = decorView.getChildAt(i)
            val id = child.id
            if (id != View.NO_ID) {
                val resourceEntryName = resources.getResourceEntryName(id)
                if ("navigationBarBackground" == resourceEntryName) {
                    child.visibility = if (visible) View.VISIBLE else View.INVISIBLE
                }
            }
            i++
        }
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (visible) {
            decorView.systemUiVisibility = decorView.systemUiVisibility and uiOptions.inv()
        } else {
            decorView.systemUiVisibility = decorView.systemUiVisibility or uiOptions
        }
    }
}