package com.simplyapp.control.ui.control

import android.content.Context
import android.os.ParcelFileDescriptor
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.simplyapp.control.arch.AppModule
import com.simplyapp.control.arch.BasePresenter
import com.simplyapp.data.data.ControlMove
import timber.log.Timber

class ControlPresenter constructor(controlView: ControlMvp.View,
                                   val context: Context = AppModule.application) : BasePresenter<ControlMvp.View>(controlView), ControlMvp.Presenter {

    lateinit var endPointId: String

    override fun onCreate(endPointId: String) {
        view?.setStatus("Requesting Connection", true)
        this.endPointId = endPointId
        Nearby.getConnectionsClient(context).requestConnection(
                "Master",
                endPointId,
                connectionLifecycleCallback)
                .addOnSuccessListener {
                    view?.setStatus("Successful Connection request", true)
                    Timber.d("Connection request successful")
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                }
                .addOnFailureListener {
                    Timber.e("Connection request failure", it)
                    view?.setStatus("Connection request Failure", false)
                    // Nearby Connections failed to request the connection.
                }
    }

    override fun transferMove(control: ControlMove) {
        Nearby.getConnectionsClient(context).sendPayload(endPointId, Payload.fromBytes(ControlMove.toByteArray(control)))
    }

    override fun transferStream(readFD: ParcelFileDescriptor?) {
        Nearby.getConnectionsClient(context).sendPayload(endPointId, Payload.fromStream(readFD))
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    view?.connectionComplete(2, 2)//todo set the rows and columns correctly
                    Timber.d("Connection OK")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    view?.setStatus("Connect Rejected", false)
                    Timber.d("Connection REJECTED")

                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    view?.setStatus("Connect Error", false)
                    Timber.d("Connection ERROR")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            view?.setStatus("Diconnected", false)
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Timber.d("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payload)
//            onReceive(establishedConnections.get(endpointId), payload)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Timber.d("onPayloadTransferUpdate(endpointId=%s, update=%s", endpointId, update)
            when (update.status) {
                PayloadTransferUpdate.Status.IN_PROGRESS -> {
                    //Transfer is to fast in the case of this app
//                    val size = update.totalBytes
//                    if (size == null) {
//                        // This is a stream payload, so we don't need to update anything at this point.
//                        return
//                    }
//                    view?.setStatus(String.format("Transfering %s ", size / update.bytesTransferred), true)
//                    notification.setProgress(size, update.bytesTransferred, false /* indeterminate */)
                }
                PayloadTransferUpdate.Status.SUCCESS -> {
                    view?.setStatus(String.format("Transferring Success"), false)

                    // SUCCESS always means that we transferred 100%.
//                    notification
//                            .setProgress(100, 100, false /* indeterminate */)
//                            .setContentText("Transfer complete!")
                }
                PayloadTransferUpdate.Status.FAILURE -> {
                    view?.setStatus(String.format("Transferring Failed"), false)

//                        notification
//                        .setProgress(0, 0, false)
//                        .setContentText("Transfer failed")
                }

            }
        }
    }

    override fun onViewDestroy() {
        Nearby.getConnectionsClient(context)
                .disconnectFromEndpoint(endPointId)
    }

}
