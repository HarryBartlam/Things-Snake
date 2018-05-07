package com.simplyapp.control.ui.control

import android.os.ParcelFileDescriptor
import com.simplyapp.control.arch.BaseMvp
import com.simplyapp.data.data.ControlMove

interface ControlMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun setStatus(status: String, showProgress: Boolean)
        fun showProgress(show: Boolean)
        fun connectionComplete(rows: Int, columns: Int)
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate(endPointId: String)
        fun onViewDestroy()
        fun transferStream(readFD: ParcelFileDescriptor?)
        fun transferMove(control: ControlMove)
    }
}