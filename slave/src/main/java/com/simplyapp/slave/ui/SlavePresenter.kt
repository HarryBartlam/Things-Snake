package com.simplyapp.slave.ui

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.simplyapp.control.arch.AppModule
import com.simplyapp.control.arch.BasePresenter
import com.simplyapp.control.util.androidExt.applyIoSchedulers
import com.simplyapp.data.data.ControlMove
import com.simplyapp.data.util.NetworkUtil
import com.simplyapp.max72xx.driver.MAX72xxLEDController
import io.reactivex.Flowable
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit


class SlavePresenter constructor(slaveView: SlaveMvp.View,
                                 val context: Context = AppModule.application) : BasePresenter<SlaveMvp.View>(slaveView), SlaveMvp.Presenter {
    val displayNum = 16

    val ledControl = MAX72xxLEDController("SPI0.0", displayNum)

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    view?.setStatus(String.format("Connected to %s", endpointId))
                    Timber.d("Connection OK")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    view?.setStatus(String.format("Connected REJECTED for %s", endpointId))
                    Timber.d("Connection REJECTED")

                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    view?.setStatus(String.format("Connected ERROR for %s", endpointId))
                    Timber.d("Connection ERROR")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            view?.setStatus(String.format("Disconnected from %s", endpointId))

        }
    }

    override fun onCreate() {
        setupDisplay()
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        /* endpointName= */ "Android Snek",
                        /* serviceId= */ "com.simplyapp.com.slave",
                        connectionLifecycleCallback,
                        AdvertisingOptions(Strategy.P2P_CLUSTER))
                .addOnSuccessListener {
                    view?.setStatus("Successfully Advertising")
                    Timber.d("Successfully Advertising")
                }
                .addOnFailureListener {
                    view?.setStatus("Failed to Advertised")
                    Timber.e(it, "Failed to Advertised")
                }
        Flowable.interval(5, TimeUnit.SECONDS)
                .applyIoSchedulers()
                .subscribe {
                    view?.setIP(NetworkUtil.getIPAddress(true))
                }

    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
//            Timber.d("onPayloadReceived(endpointId=%s, payload=%s)", endpointId, payload)
            onReceive(payload)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
//            Timber.d("onPayloadTransferUpdate(endpointId=%s, updateGame=%s", endpointId, updateGame)
        }
    }

    protected fun onReceive(payload: Payload) {
        if (payload.type != Payload.Type.BYTES) return

        val bytes = payload.asBytes()

        if (bytes != null) {
            val receivedControl = ControlMove.fromByte(bytes)
            if (receivedControl != null) {
                view?.setControl(receivedControl)
            }
        }
//        val receivedData = bytes?.toBooleanArray()

//        receivedData?.forEach { Timber.d(it.toString()) }

    }

    override fun resetDisplay() {
        try {
            Timber.d("Setting up displays")
            for (i in 0..ledControl.getDeviceCount() - 1) {
                ledControl.shutdown(i, true)
            }

            Timber.d("Displays setup")

        } catch (e: IOException) {
            Timber.e("Error initializing LED matrix", e)
        }
        setupDisplay()
    }

    override fun setupDisplay() {
        try {
            Timber.d("Setting up displays")
            for (i in 0..ledControl.getDeviceCount() - 1) {
                ledControl.setIntensity(i, 15)
                ledControl.shutdown(i, false)
                ledControl.clearDisplay(i)
            }

            Timber.d("Displays setup")

        } catch (e: IOException) {
            Timber.e("Error initializing LED matrix", e)
        }

    }

    override fun updateXY(x: Int, y: Int, lit: Boolean) {

        val xInvert = y
        val yInvert = x
        val displayRow = Math.floor(xInvert / 8.0).toInt()
        val displayColumn = Math.floor(yInvert / 8.0).toInt()

        ledControl.setLed(displayColumn + (displayRow * 4), 7 - (xInvert % 8), 7 - (yInvert % 8), lit)

    }

}