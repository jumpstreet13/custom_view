package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver

class LineView : View {

    private val blue = Paint()
    private val red = Paint()
    private val path = Path()
    private val pathC = Path()
    private lateinit var quad: Quad

    private var Px0 = 0f
    private var Py0 = 0f
    private var Px1 = 0f
    private var Py1 = 0f
    private var Px2 = 0f
    private var Py2 = 0f

    constructor(context: Context) : super(context) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initial()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initial()
    }

    private fun initial() {
        red.apply {
            color = Color.RED
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        blue.apply {
            color = Color.BLUE
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Px1 = width / 2f
                Py1 = width / 4f
                Px2 = width / 1f
                quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        path.moveTo(Px0, Py0)
        path.quadTo(Px1, Py1, Px2, Py2)

        canvas?.drawPath(path, red)

        val x = quad.getX(0.5f)
        val y = quad.getY(0.5f)

        val xl = quad.getX(0.45f)
        val yl = quad.getY(0.45f)
        val xr = quad.getX(0.55f)
        val yr = quad.getY(0.55f)

        val xlc = quad.getX(0.47f)
        val ylc = quad.getY(0.47f)
        val xrc = quad.getX(0.53f)
        val yrc = quad.getY(0.53f)

        canvas?.drawCircle(x, y, 5f, blue)
        canvas?.drawCircle(xr, yr, 5f, blue)
        canvas?.drawCircle(xl, yl, 5f, blue)

        pathC.moveTo(xl, yl)
        pathC.cubicTo(xlc, ylc, xlc, ylc - 50f, x, y - 50f)
        pathC.cubicTo(xrc, yrc- 50, x, y , xr, yr)
        canvas?.drawPath(pathC, blue)

    }

    class Quad(
        private val Px0: Float,
        private val Py0: Float,
        private val Px1: Float,
        private val Py1: Float,
        private val Px2: Float,
        private val Py2: Float
    ) {
        fun getX(t: Float) = (1 - t) * (1 - t) * Px0 + 2 * t * (1 - t) * Px1 + t * t * Px2
        fun getY(t: Float) = (1 - t) * (1 - t) * Py0 + 2 * t * (1 - t) * Py1 + t * t * Py2
    }

}