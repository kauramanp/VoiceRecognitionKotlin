package com.aman.voicerecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.TextView


class VisualizerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var amplitudes: ArrayList<Float>? = null
    private val linePaint = Paint()
    private var width = 0
    private var height = 0
    private val density = this.resources.displayMetrics.densityDpi //Get the display DPI
    private var stroke = 0f
    init {
        linePaint.color = Color.GREEN
        linePaint.isAntiAlias = true //Add AntiAlias for displaying strokes that are less than 1
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        width = w
        height = h
        amplitudes = ArrayList(width * 2)
        stroke =
            (width * (density.toFloat() / 160)) / 1000 //Calculate actual pixel size for the view based on view width and dpi
        linePaint.strokeWidth = stroke
    }

    /**
     * Add a new value of int to the visualizer array
     * @param amplitude Int value
     */
    fun addAmplitude(amplitude: Int) {
        invalidate()
        val scaledHeight = (amplitude.toFloat() / MAX_AMPLITUDE) * (height - 1)
        amplitudes!!.add(scaledHeight)
    }

    /**
     * Clears Visualization
     */
    fun clear() {
        amplitudes!!.clear()
    }

    override fun onDraw(canvas: Canvas) {
        val middle = height / 2 // get the middle of the View
        var curX = 0f // start curX at zero

        // for each item in the amplitudes ArrayList
        for (power in amplitudes!!) {
            // draw a line representing this item in the amplitudes ArrayList

            canvas.drawLine(
                curX, middle + power / 2, curX, middle
                        - power / 2, linePaint
            )

            curX += stroke // increase X by line width
        }
    }

    companion object {
        private const val MAX_AMPLITUDE = 32767
    }
}