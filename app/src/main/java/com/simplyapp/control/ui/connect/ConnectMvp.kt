package com.simplyapp.control.ui.connect

import com.simplyapp.control.arch.BaseMvp
import com.simplyapp.data.EndPoint

interface ConnectMvp : BaseMvp {

    interface View : BaseMvp.View {
        fun showProgress(show: Boolean)
        fun setStatus(status: String, showProgress: Boolean)
        fun setData(principlesList: List<EndPoint>)
        fun showControl(endPointId: String)
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
        fun onEndpointClicked(endpoint: EndPoint)
    }
}
