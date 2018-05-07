package com.simplyapp.control.arch

import com.trello.rxlifecycle2.components.support.RxFragment

abstract class BaseFragment<T : BaseMvp.Presenter> : RxFragment(), BaseMvp.View {
    protected lateinit var presenter: T

    override fun finish() {
        presenter.onDestroy()
    }

    override fun onDetach() {
        finish()
        super.onDetach()
    }
}
