package com.simplyapp.data.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CordsDotMatixDisplayView @TargetApi(21) @JvmOverloads constructor(
        context: Context,
        attrs:
        AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    var unSelectedDot: Paint
    var selectedDot: Paint

    var dotsPerRow: Int = 32
    var dotsPerColumn: Int = 32
    var dotSpacing: Float = 0f
    var onChange: ((x: Int, y: Int, lit: Boolean) -> Unit)? = null

    var dotMatixArray = Array(dotsPerRow) { BooleanArray(dotsPerColumn) }

    init {
        unSelectedDot = Paint()
        unSelectedDot.isAntiAlias = true
        unSelectedDot.style = Paint.Style.FILL
        unSelectedDot.color = Color.LTGRAY

        selectedDot = Paint()
        selectedDot.isAntiAlias = true
        selectedDot.style = Paint.Style.FILL
        selectedDot.color = Color.RED

        setWillNotDraw(false)

        setBackgroundColor(Color.BLACK)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        dotSpacing = width.toFloat() / Math.max(dotsPerColumn, dotsPerRow)
        val dotRadius = dotSpacing * 0.5f

        for (x in 0 until dotsPerRow) {
            for (y in 0 until dotsPerRow) {
                canvas.drawCircle((x * dotSpacing) + dotRadius, (y * dotSpacing) + dotRadius, dotRadius, if (dotMatixArray[x][y]) selectedDot else unSelectedDot)
            }
        }
    }

    var lastXIndex = -1
    var lastYIndex = -1

    var nextTouch = 0L
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (nextTouch > System.currentTimeMillis()) {
            return false
        }
        nextTouch = System.currentTimeMillis() + 50 // debouce
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_MOVE -> if (event.x > 0 && event.y > 0 && event.x < width && event.y < height) {

                val x = Math.floor(event.x / dotSpacing.toDouble()).toInt()
                val y = Math.floor(event.y / dotSpacing.toDouble()).toInt()

                if (x == lastXIndex && y == lastYIndex) return true

                lastXIndex = x
                lastYIndex = y

                val dotInMatix = dotMatixArray[x][y]

                if (x <= dotsPerRow && y <= dotsPerColumn && dotInMatix != !dotInMatix) {
                    dotMatixArray[x][y] = !dotInMatix
                    onChange?.invoke(x, y, !dotInMatix)
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                lastXIndex = -1
                lastYIndex = -1
                return true
            }
        }

        return false
    }

    fun setData(x: Int, y: Int, lit: Boolean) {
        dotMatixArray[x][y] = lit
        invalidate()
    }

    fun clearData() {
        for (x in 0 until dotsPerRow) {
            for (y in 0 until dotsPerRow) {
                dotMatixArray[x][y] = false
            }
        }
        invalidate()
    }

}

