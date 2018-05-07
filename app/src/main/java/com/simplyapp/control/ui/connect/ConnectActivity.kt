package com.simplyapp.control.ui.connect

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.simplyapp.control.R
import com.simplyapp.control.arch.BaseActivity
import com.simplyapp.control.ui.control.ControlActivity
import com.simplyapp.data.EndPoint
import kotlinx.android.synthetic.main.activity_connect.*

class ConnectActivity : BaseActivity<ConnectMvp.Presenter>(), ConnectMvp.View {

    private lateinit var devicesAdapter: DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ConnectPresenter(this)

        setContentView(R.layout.activity_connect)
        title = getString(R.string.connect_title)
        setSupportActionBar(connect_toolbar)

        val request = permissionsBuilder(Manifest.permission.ACCESS_COARSE_LOCATION).build()
        request.listeners {
            onAccepted { _ ->
                presenter.onCreate()
            }
            onDenied { _ ->
                setStatus(getString(R.string.connect_permission), false)
            }
            onPermanentlyDenied { _ ->
                setStatus(getString(R.string.connect_permission), false)
            }
            onShouldShowRationale { _, _ ->
                setStatus(getString(R.string.connect_permission), false)
            }
        }
        request.send()

        devicesAdapter = DeviceListAdapter(this)
        device_list.layoutManager = LinearLayoutManager(this)
        device_list.adapter = devicesAdapter

        devicesAdapter.onEndpointClicked = {
            presenter.onEndpointClicked(it)
        }
    }

    override fun showControl(endPointId: String) {
        ControlActivity.startWithEndpoint(this, endPointId)
    }

    override fun setData(principlesList: List<EndPoint>) {
        devicesAdapter.setData(principlesList)
    }

    override fun setStatus(status: String, showProgress: Boolean) {
        showProgress(showProgress)
        status_textview.text = status
    }

    override fun showProgress(show: Boolean) {
        connect_progress.visibility = if (show) View.VISIBLE else View.GONE
    }
}
