package com.simplyapp.control.ui.control

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.simplyapp.control.R
import com.simplyapp.control.arch.BaseActivity
import com.simplyapp.data.data.ControlMove
import kotlinx.android.synthetic.main.activity_control.*
import timber.log.Timber

class ControlActivity : BaseActivity<ControlMvp.Presenter>(), ControlMvp.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = ControlPresenter(this)

        setContentView(R.layout.activity_control)
//        title = getString(R.string.control_title)
//        setSupportActionBar(control_toolbar)

        val endPointId = intent.getStringExtra(ARG_ENDPOINT)
        Timber.d("Connecting to endpoint: %s", endPointId)

        presenter.onCreate(endPointId)

        up.setOnClickListener {
            presenter.transferMove(ControlMove.UP)
        }
        down.setOnClickListener {
            presenter.transferMove(ControlMove.DOWN)
        }
        left.setOnClickListener {
            presenter.transferMove(ControlMove.LEFT)
        }
        right.setOnClickListener {
            presenter.transferMove(ControlMove.RIGHT)
        }
        start.setOnClickListener {
            presenter.transferMove(ControlMove.START)
        }
    }

    override fun connectionComplete(rows: Int, columns: Int) {
        setStatus("Successful Connected", false)
    }

    override fun setStatus(status: String, showProgress: Boolean) {
        showProgress(showProgress)
        control_status_textview.text = status
    }

    override fun showProgress(show: Boolean) {
        control_progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDestroy()

    }

    companion object {
        private const val ARG_ENDPOINT = "end_point_id.string"

        fun startWithEndpoint(context: Activity, endPointId: String) {
            Intent(context, ControlActivity::class.java).apply {
                putExtra(ARG_ENDPOINT, endPointId)
                ContextCompat.startActivity(context, this, null)
            }
        }
    }
}