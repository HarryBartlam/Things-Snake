package com.simplyapp.data.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.GridLayout
import com.simplyapp.control.networking.R

class DotMatixDisplayView @TargetApi(21) @JvmOverloads constructor(
        context: Context,
        attrs:
        AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0) : GridLayout(context, attrs, defStyleAttr, defStyleRes), OnGlobalLayoutListener {

    var onDisplayChange: ((displayArray: BooleanArray) -> Unit)? = null

    var displayArray: BooleanArray

    private val views = mutableListOf<DotMatixView>()

    private var displaySize = 0

    init {
        View.inflate(context, R.layout.element_display, this)
        viewTreeObserver.addOnGlobalLayoutListener(this)

        setWillNotDraw(false)

        displaySize = views.size * 64
        this.displayArray = BooleanArray(displaySize)

        columnCount = 4


        setOnTouchListener { v, event ->
            val x = event.x
            val y = event.y
            views.forEach {
                if (it.x < x && x < it.x + it.width && it.y < y && y < it.y + it.height) {
                    it.onTouch(x - it.x, y - it.y, event.action == MotionEvent.ACTION_UP)
                    return@forEach
                }
            }
            true
        }

        setBackgroundColor(Color.BLACK)
    }

    override fun onGlobalLayout() {
        views.clear()
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is DotMatixView) {
                view.onChange = {
                    displayUpdate()
                }
                views.add(view)
            }
        }
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private fun displayUpdate() {
        displayArray = views.map { it.dotMatixArray }.reduce<BooleanArray, BooleanArray> { acc, dotMatixArray -> acc + dotMatixArray }
        onDisplayChange?.invoke(displayArray)
    }


    fun setData(booleanArray: BooleanArray) {
        this.displayArray = booleanArray

        views.forEachIndexed { index, dotMatixView ->
            dotMatixView.setData(displayArray.sliceArray((index * 64) until ((index + 1) * 64)))
        }
        invalidate()
    }

    fun clearData() {
        this.displayArray = BooleanArray(displaySize)

        views.forEach { it.clearData() }

        invalidate()
    }

}

