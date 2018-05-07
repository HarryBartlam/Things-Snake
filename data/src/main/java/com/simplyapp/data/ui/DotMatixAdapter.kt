package com.simplyapp.data.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.simplyapp.control.networking.R
import kotlinx.android.synthetic.main.element_dot.view.*

class DotMatixAdapter constructor(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var booleanArray: BooleanArray

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.element_dot, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        booleanArray.get(holder.adapterPosition).let { lit ->
            holder.itemView.matix_dot.isSelected = lit
            holder.itemView.matix_dot.isPressed = lit
            holder.itemView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    booleanArray.set(holder.adapterPosition, !lit)
                    holder.itemView.matix_dot.isSelected = !lit
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun getItemCount(): Int = booleanArray.size

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    fun setRowAndColums(rows: Int, columns: Int) {
        val dotsPerRow = rows * 8
        val dotsPerColumn = columns * 8

        booleanArray = BooleanArray(dotsPerRow * dotsPerColumn)

        notifyDataSetChanged()
    }

    fun getDisplayBooleanArray(): BooleanArray {
        return booleanArray
    }

    fun setData(booleanArray: BooleanArray) {
        this.booleanArray = booleanArray
        notifyDataSetChanged()
    }

}
