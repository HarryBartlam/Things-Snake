package com.simplyapp.control.ui.connect

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simplyapp.control.R
import com.simplyapp.data.EndPoint
import kotlinx.android.synthetic.main.element_endpoint.view.*

class DeviceListAdapter constructor(val context: Context) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    private var endPoints = mutableListOf<EndPoint>()

    var onEndpointClicked: ((EndPoint) -> Unit)? = null

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.element_endpoint, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        endPoints.get(holder.adapterPosition).let { endPoint ->
            holder.itemView.endpoint_id.text = endPoint.endpointId
            holder.itemView.endpoint_name.text = endPoint.discoveredEndpointInfo.endpointName
            holder.itemView.setOnClickListener { onEndpointClicked?.invoke(endPoint) }
        }
    }

    override fun getItemCount(): Int = endPoints.size

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    fun setData(endPoints: List<EndPoint>) {
        this.endPoints = endPoints.toMutableList()
        notifyDataSetChanged()
    }
}