package com.example.takingtom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AudioWaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 2f
    }

    private val waveform = mutableListOf<Float>()

    fun updateWaveform(newData: FloatArray) {
        waveform.clear()
        waveform.addAll(newData.toList())
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.let {
            val centerY = height / 2f
            val step = width / (waveform.size.toFloat() + 1)

            for (i in waveform.indices) {
                val x = i * step
                val y = centerY - waveform[i] * centerY
                it.drawLine(x, centerY, x, y, paint)
            }
        }
    }
}
