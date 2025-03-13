package com.aman.voicerecognition


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View

class AudioVisualizerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var amplitudes: ArrayList<Float>? = null
    private val linePaint = Paint()
    private var width = 0
    private var height = 0
    //    private val density = this.resources.displayMetrics.densityDpi //Get the display DPI
    private var stroke = 0f
    var density = 50f
    var gap = 4
    private val TAG = "VisualizerView"
    init {
        linePaint.color = Color.WHITE
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
    /* override fun onDraw(canvas: Canvas) {
         logDebug("in on Draw ")
         val middle = height / 2 // get the middle of the View
         var curX = 0f // start curX at zero
         if(getWidth() >0 && (amplitudes?.size?:0) >0)
         // Increase the width of the bar
         {
             val barWidth =
                 (getWidth() / amplitudes!!.size) * 2 // Increase the multiplier to make the bars wider

             // for each item in the amplitudes ArrayList
             for (power in amplitudes!!) {
                 val top: Int = (getHeight() / 2
                         + (128 - (power).toInt())
                         * (getHeight() / 2) / 128)

                 val bottom: Int = (getHeight() / 2
                         - (128 - (power).toInt())
                         * (getHeight() / 2) / 128)

                 // Adjust the bar X position for the new bar width
                 val barX = (curX * barWidth) + (barWidth / 2)

                 // Draw two bars for each amplitude value
                 canvas.drawLine(
                     barX,
                     bottom.toFloat(),
                     barX,
                     (getHeight() / 2).toFloat(),
                     linePaint
                 )
                 canvas.drawLine(barX, top.toFloat(), barX, (getHeight() / 2).toFloat(), linePaint)

                 // Move to the next bar
                 curX += barWidth // Increase X by the new bar width
             }
         }
     }*/
  /*  override fun onDraw(canvas: Canvas) {
        Log.e(TAG, "in on Draw ")
        val middle = height / 2 // get the middle of the View
        var curX = 0f // start curX at zero
        val barWidth = getWidth() / density
        Log.e(TAG, "barWidth "+barWidth)
        var gap = 20
        // for each item in the amplitudes ArrayList
        for (power in amplitudes!!) {
            // draw a line representing this item in the amplitudes ArrayList

            *//*  canvas.drawLine(
                  curX, middle + power / 2, curX, middle
                          - power / 2, linePaint
              )*//*
            //this is for the straight line
            *//*canvas.drawLine(
                0f,
                (getHeight() / 2).toFloat(),
                getWidth().toFloat(),
                (getHeight() / 2).toFloat(),
                linePaint
            )*//*
            var halfHeight = getHeight()/2
            var halfPower =  (100 - power.toInt())// .div(2)
            val division =  (halfPower.toFloat().div(halfHeight.toFloat())).toFloat()
            val top = (division.times(100).toInt()).plus(30)
            val previousTop: Int = (getHeight() / 2
                    + (128 -(50 -power).toInt())
                    * (getHeight() / 2) / 128)
            val bottom: Int = (getHeight() / 2
                    - (128 - (50 - power).toInt())
                    * (getHeight() / 2) / 128)

//            canvas.drawLine(barX, bottom.toFloat(), barX, (getHeight() / 2).toFloat(), linePaint)
//            canvas.drawLine(barX, top.toFloat(), barX, (getHeight() / 2).toFloat(), linePaint)

            // Calculate bar's X position and the rectangle width
            val barLeft = curX - barWidth / 2  // center the bar on the X position
            val barRight = curX + barWidth / 2  // same as above but to the right
            val barTop = previousTop.toFloat()
            val barBottom = bottom.toFloat()

            // Draw a rectangle to simulate a thick line
            canvas.drawRect(barLeft, barTop, barRight, barBottom, linePaint)
            curX += stroke + gap// increase X by line width
        }
    }*/
    private var maxAmplitude = 0f // Variable to store the current maximum amplitude

    override fun onDraw(canvas: Canvas) {
        val middle = height / 2 // get the middle of the View
        var curX = 0f // start curX at zero
        val barWidth = getWidth() / density
        Log.e(TAG, "barWidth " + barWidth)
        var gap = 15
        val maxHeight = getHeight() // Set the maximum height for the visualizer bars

        // Set a minimum threshold for amplitude (below this, we won't draw any bars)
        val minThreshold = 15 // Amplitude value below which bars will not be drawn (adjustable)

        // For each item in the amplitudes ArrayList
        for (power in amplitudes!!) {
            // Discard amplitudes below the threshold
            if (power < minThreshold) {
                continue // Skip drawing for low amplitudes below the threshold
            }

            // Dynamically track the maximum amplitude
            if (power > maxAmplitude) {
                maxAmplitude = power // Update max amplitude if the current value exceeds the previous max
            }

            // Calculate the scaled height based on the current maximum amplitude
            val scaledHeight = (power.toFloat() / maxAmplitude.toFloat()) * maxHeight

            // Calculate the top and bottom of the bar based on the scaled height
            val top: Int = (middle - scaledHeight / 2).toInt().coerceAtLeast(0)
            val bottom: Int = (middle + scaledHeight / 2).toInt().coerceAtMost(getHeight())

            // Create a LinearGradient for the bar colors (from red to blue in this example)
            val gradient = LinearGradient(
                0f, top.toFloat(), 0f, bottom.toFloat(),
                Color.BLUE, Color.BLUE, Shader.TileMode.CLAMP
            )

            // Set up a Paint object with the gradient shader
            val gradientPaint = Paint().apply {
                shader = gradient
                isAntiAlias = true // Smooth out the edges
            }

            // Calculate bar's X position and the rectangle width
            val barLeft = curX - barWidth / 2  // center the bar on the X position
            val barRight = curX + barWidth / 2  // same as above but to the right

            // Draw the bar for the amplitude
            canvas.drawRect(barLeft, top.toFloat(), barRight, bottom.toFloat(), gradientPaint)

            // Increment the X position by the width of the bar and the gap between bars
            curX += barWidth + gap
        }
    }

    companion object {
        private const val MAX_AMPLITUDE = 32767
    }
}