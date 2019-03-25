package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import java.lang.Math.cos
import java.lang.Math.sin
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sqrt

class LineView : View {

    private val blue = Paint(Paint.ANTI_ALIAS_FLAG)
    private val red = Paint(Paint.ANTI_ALIAS_FLAG)
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
            color = Color.WHITE
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Px1 = width / 2f
                Py1 = height / 4f
                Px2 = width / 1f
                Py0 = height / 8f
                Py2 = height / 8f
                quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

        setBackgroundColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        path.reset()
        path.moveTo(Px0, Py0)
        path.quadTo(Px1, Py1, Px2, Py2)

        canvas?.drawRect(0f, 0f, width / 1f, height / 8f, red)
        canvas?.drawPath(path, red)

        drawShape(.8f, canvas)
    }

    fun setProgress(progress: Float) {
        Py1 = height / (4f * (1 + progress))
        quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)
        invalidate()
    }

    private fun drawShape(t: Float, canvas: Canvas?) {
        val x = quad.getX(t)
        val y = quad.getY(t)

        //находим левые точки фигуры
        val xl = quad.getX(t - 0.05f)
        val yl = quad.getY(t - 0.05f)

        //находим правые точки фигуры
        val xr = quad.getX(t + 0.05f)
        val yr = quad.getY(t + 0.05f)

        val a = sqrt((xl - x) * (xl - x) + (yl - y) * (yl - y))
        val l = yl - y
        val k = xl - x
        val b = 50f

        val cosA = if (l == 0f) 0.00001 else (a * a + l * l - k * k) / (2.0 * a * l)

        val A = acos(cosA)

        //координаты вершины рисуемой фигуры
        val xt = x - b * sin(PI / 2 - A).toFloat()
        val yt = y - b * cos(PI / 2 - A).toFloat()

        val xp = x - xt
        val yp = y - yt

        val xa = xr - xp * 3 / 4
        val ya = yr - yp * 3 / 4
        val xb = xl - xp * 3 / 4
        val yb = yl - yp * 3 / 4

        val xc = (x + xl) / 2
        val yc = (y + yl) / 2
        val xd = (x + xr) / 2
        val yd = (y + yr) / 2

        //строим купол
        pathC.reset()
        pathC.moveTo(xl, yl)
        pathC.cubicTo(xc, yc, xb, yb, xt, yt)
        pathC.cubicTo(xa, ya, xd, yd, xr, yr)
        pathC.lineTo(xl, yl)
        pathC.lineTo(xr, yr)
        pathC.lineTo(xr + 20f, yr + 20f)
        pathC.lineTo(xl - 20f, yl + 20f)

        //рисуем купол
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