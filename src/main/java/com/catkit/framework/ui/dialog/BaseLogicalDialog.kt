package com.catkit.framework.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catkit.framework.ui.Logical

/**
 * Only BaseLogicalDialog can be used to worked as "V" in MVP model. Other dialog
 * doesn't implement [Logical].
 */
abstract class BaseLogicalDialog : BaseDialog(), Logical {
    private lateinit var positiveAction: () -> Unit
    private lateinit var negativeAction: () -> Unit

    @Override
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * This method always called when fragment onDestroyView called. eg: When ViewPager + Fragments changes
     * its tab, onCreateView and onDestroyView will be called. If you do not want fragment's view recycled
     * by gc, you can save it in onCreateView and remove it in onDestroyView.
     */
    @Override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView(view)
    }

    @Override
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    @Override
    override fun onStart() {
        super.onStart()
        initOnStart()
    }

    @Override
    override fun onResume() {
        super.onResume()
    }

    @Override
    override fun onPause() {
        super.onPause()
    }

    @Override
    override fun onStop() {
        super.onStop()
    }

    @Override
    override fun onDestroyView() {
        super.onDestroyView()
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
    }

    @Override
    override fun onDetach() {
        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val dialog = Dialog(it, setStyle())
            config(dialog)
            return dialog
        }
        return super.onCreateDialog(savedInstanceState)
    }

    /**
     * Sometimes IllegalStateException of "Can not perform this action after onSaveInstanceState" will
     * happened. Be sure that the fragment's activity is under running or whether the activity is null(if
     * lifecycle is not in onAttach to onDestroy, getActivity will return null).
     * This error happens when the app is under low memory and the system will save the status of the fragment,
     * so when fragment shows again[android.support.v4.app.DialogFragment.show], system will call commit method. finally,
     * the exception will throw. It always happened in activity or fragment's status-save.
     *
     * When Activity is killed, [android.app.Activity.onSaveInstanceState] will called to save the activity's
     * data. so Fragment's method [android.support.v4.app.FragmentManagerImpl.saveAllState] will be called to save
     * data. But sometimes(eg: onBackPressed), if mStateSaved is true, exception will throw. so ,we can
     * change the mStateSaved in onSaveInstanceState method by reflection.
     *
     * [android.support.v4.app.BackStackRecord.commitAllowingStateLoss] is same as commit, but commit will call
     * [android.support.v4.app.FragmentManagerImpl.checkStateLoss] to throw exception.
     */
    override fun show(manager: FragmentManager?, tag: String?) {
        super.show(manager, tag)
        val mDismissed = DialogFragment::class.java.getDeclaredField(" mDismissed")
        val mShownByMe = DialogFragment::class.java.getDeclaredField("mShownByMe")
        mDismissed.setBoolean(this, false)
        mShownByMe.setBoolean(this, true)
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        ft?.commitAllowingStateLoss()
    }

    /**
     * Set the view of the dialog.
     * @return @LayoutRes
     */
    protected abstract fun getLayoutId(): Int

    /**
     * Set the dialog theme. It decides the dialog's look.
     * @return @LayoutRes
     */
    protected abstract fun setStyle(): Int

    /**
     * Init the data of the dialog, eg: put Int data by bundle.
     */
    protected abstract fun initData()

    /**
     * Init the view of the dialog.
     */
    protected abstract fun initView(view: View)

    /**
     * Set the alpha, animation, height, width, navigationBar and so of the dialog's window.
     */
    protected abstract fun initOnStart()

    /**
     * Get the dialog instance and make some changes. eg: set dialog cancelable.
     */
    protected abstract fun config(dialog: Dialog)

    fun setPositiveAction(action: () -> Unit) {
        this.positiveAction = action
    }

    fun setNegativeAction(action: () -> Unit){
        this.negativeAction = action
    }
}