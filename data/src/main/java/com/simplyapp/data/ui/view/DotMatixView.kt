package com.simplyapp.data.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DotMatixView @TargetApi(21) @JvmOverloads constructor(
        context: Context,
        attrs:
        AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    var unSelectedDot: Paint
    var selectedDot: Paint

    var dotsPerRow: Int = 8
    var dotsPerColumn: Int = 8
    var dotSpacing: Float = 0f
    var onChange: ((BooleanArray) -> Unit)? = null

    var dotMatixArray: BooleanArray = BooleanArray(dotsPerRow * dotsPerColumn)

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

        for (x in 0 until 8) {
            for (y in 0 until 8) {
                canvas.drawCircle((x * dotSpacing) + dotRadius, (y * dotSpacing) + dotRadius, dotRadius, if (dotMatixArray[x + (y * dotsPerColumn)]) selectedDot else unSelectedDot)
            }
        }
    }

    var lastIndex = -1

    fun onTouch(touchX: Float, touchY: Float, upEvent: Boolean) {
        if (upEvent) {
            lastIndex = -1
            return
        }

        val x = (touchX / dotSpacing).toInt() // + dotSpacing * 0.5
        val y = (touchY / dotSpacing).toInt() // + dotSpacing * 0.5

        val dotIndex = ((y * dotsPerRow) + x).toInt()

        if (lastIndex == dotIndex) return

        lastIndex = dotIndex

        if (dotIndex <= dotMatixArray.size - 1) {
            dotMatixArray[dotIndex] = !dotMatixArray[dotIndex]
            onChange?.invoke(dotMatixArray)
            invalidate()
        }
    }

    fun setData(booleanArray: BooleanArray) {
        this.dotMatixArray = booleanArray
        invalidate()
    }

    fun clearData() {
        this.dotMatixArray = BooleanArray(dotMatixArray.size)
        invalidate()
    }

}

