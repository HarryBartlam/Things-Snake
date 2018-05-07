package com.simplyapp.control.ui.connect

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import com.simplyapp.control.arch.AppModule
import com.simplyapp.control.arch.BasePresenter
import com.simplyapp.data.EndPoint
import timber.log.Timber

class ConnectPresenter constructor(connectView: ConnectMvp.View,
                                   val context: Context = AppModule.application) : BasePresenter<ConnectMvp.View>(connectView), ConnectMvp.Presenter {

    var endPointList = mutableListOf<EndPoint>()

    override fun onCreate() {
        view?.setStatus("Starting", true)
        Nearby.getConnectionsClient(context).startDiscovery(
                "com.simplyapp.com.slave",
                mEndpointDiscoveryCallback,
                DiscoveryOptions(Strategy.P2P_CLUSTER))
                .addOnSuccessListener {
                    view?.setStatus("Finding Devices", true)

                }
                .addOnFailureListener {
                    Timber.e(it)
                    view?.setStatus("An error has occured", false)
                }
    }

    private val mEndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, discoveredEndpointInfo: DiscoveredEndpointInfo) {
            // An endpoint was found!
            Timber.d("Work boi : %s", endpointId)
            Timber.d("Work boi stuff : %s", discoveredEndpointInfo.endpointName)

            if (endPointList.isEmpty()) {
                view?.showProgress(false)
            }
            endPointList.add(EndPoint(endpointId, discoveredEndpointInfo))
            view?.setData(endPointList)

        }

        override fun onEndpointLost(endPointId: String) {
            // A previously discovered endpoint has gone away.
            endPointList.removeAll { it.endpointId == endPointId }
            if (endPointList.isEmpty()) {
                view?.showProgress(true)
            }
            view?.setData(endPointList)
        }
    }

    override fun onEndpointClicked(endpoint: EndPoint) {
        view?.showControl(endpoint.endpointId)
    }
}
