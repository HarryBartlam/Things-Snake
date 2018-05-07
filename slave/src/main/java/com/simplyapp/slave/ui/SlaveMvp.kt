package com.simplyapp.slave.ui

import com.simplyapp.control.arch.BaseMvp
import com.simplyapp.data.data.ControlMove

interface SlaveMvp: BaseMvp {
    interface View : BaseMvp.View {

        fun setControl(controlMove: ControlMove)
        fun setStatus(status: String)
        fun setIP(ipAddress: String?)
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
        fun updateXY(x: Int, y: Int, lit: Boolean)
        fun setupDisplay()
        fun resetDisplay()
    }
}