package com.simplyapp.control.arch

interface BaseMvp {
    interface View {
        fun finish()
    }

    interface Presenter {
        fun onDestroy()
    }
}
