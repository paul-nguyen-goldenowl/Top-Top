package com.ln.toptop.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ln.toptop.R

abstract class BaseBottomSheet(@LayoutRes private val layoutRes: Int) :
    BottomSheetDialogFragment() {
    lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(layoutRes, container, false)
        bindView(mView)
        return mView
    }

    abstract fun bindView(mView: View)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    abstract protected fun setLayout()

    fun getBottomSheetTag() = this::class.java.simpleName
}