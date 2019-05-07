package com.catkit.framework.ui.fragment

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.catkit.framework.ui.BasePresenter
import com.catkit.framework.ui.Logical
import com.catkit.framework.ui.ViewCallback

abstract class BaseFragment<out T : BasePresenter<*>> : Fragment(),Logical,ViewCallback {

    @Suppress("LeakingThis")
    private var presenter: T? = createPresenter()

    abstract protected fun createPresenter(): T

    fun <E> callPresenter(action: T.() -> E): E? = presenter?.action()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter?.onAttach(context)
    }

    @TargetApi(24)
    override fun onAttachFragment(childFragment: Fragment?) {
        super.onAttachFragment(childFragment)
        presenter?.onAttachFragment(childFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter?.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter?.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        presenter?.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter?.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        presenter?.onDetach()
        presenter = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter?.onActivityResult(requestCode, resultCode, data)
    }
}