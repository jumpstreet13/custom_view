package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sqrt

class CustomToolbar : FrameLayout {

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val pathC = Path()
    private lateinit var quad: Quad

    private var Px0 = 0f
    private var Py0 = 0f
    private var Px1 = 0f
    private var Py1 = 0f
    private var Px2 = 0f
    private var Py2 = 0f
    private val heightRect = resources.getDimension(R.dimen.toolbar_size)
    private var heightShape = 0f

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
        setWillNotDraw(false)
        View.inflate(context, R.layout.custom_toolbar, this)

        val colorView = ContextCompat.getColor(context, R.color.blue)

        bluePaint.apply {
            color = colorView
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        whitePaint.apply {
            color = Color.WHITE
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Px1 = width / 2f
                Py1 = heightRect
                Px2 = width.toFloat()
                Py2 = heightRect
                Py0 = heightRect
                heightShape = ivUsers.height.toFloat() + resources.getDimension(R.dimen.margin_normal) + 20f
                quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        path.reset()
        path.moveTo(Px0, Py0)
        path.quadTo(Px1, Py1, Px2, Py2)

        canvas?.drawRect(0f, 0f, width.toFloat(), heightRect, bluePaint)
        canvas?.drawPath(path, bluePaint)

        val t = (ivUsers.x + ivUsers.width / 2) / width
        val r = (ivUsers.x - 30f) / width
        val l = (ivUsers.x + ivUsers.width + 30f) / width.toFloat()
        drawShape(t, r, l, canvas)

    }

    fun setProgress(progress: Float) {
        Py1 = heightRect * (1 + progress)
        quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)
        invalidate()
    }

    private fun drawShape(t: Float, r: Float, l: Float, canvas: Canvas?) {
        val k = 50f//
        val p = 10f//

        val x = quad.getX(t)
        val y = quad.getY(t)

        //находим левые точки фигуры основания
        val xa = quad.getX(l)
        val ya = quad.getY(l)

        //находим правые точки фигуры основания
        val xb = quad.getX(r)
        val yb = quad.getY(r)

        //находим левые точки фигуры основания
        val xc = xb + k
        val yc = yb - k

        //находим правые точки фигуры основания
        val xd = xa - k
        val yd = ya - k

        val xo = (xa + xb) / 2
        val yo = (yb + ya) / 2 - k

        val ro = sqrt((xc - xd) * (xc - xd) + (yc - yd) * (yc - yd)) / 2

        val xm = (xa + xd) / 2 - p
        val ym = (ya + yd) / 2 + p

        val xn = (xb + xc) / 2 + p
        val yn = (yb + yc) / 2 + p

        pathC.reset()
        pathC.moveTo(xa, ya)
        pathC.quadTo(xm, ym, xd, yd)
        pathC.lineTo(xc, yc)
        pathC.quadTo(xn, yn, xb, yb)

        //рисуем трапецию
        canvas?.drawPath(pathC, whitePaint)
        //рисуем круг
        canvas?.drawOval(xo - ro, yo + ro, xo + ro, yo - ro, whitePaint)

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