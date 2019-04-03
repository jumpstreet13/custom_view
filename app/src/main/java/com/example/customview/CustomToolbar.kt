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
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlin.math.sqrt

class CustomToolbar : FrameLayout {

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pathRect = Path()
    private val pathFigur = Path()
    private val pathCircle = Path()
    private lateinit var quad: Quad
    private val listView = mutableListOf<SelectView>()

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

        initPaint()
        initCoordinate()
        initView()

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        pathRect.reset()
        pathRect.moveTo(Px0, Py0)
        pathRect.quadTo(Px1, Py1, Px2, Py2)

        canvas?.drawRect(0f, 0f, width.toFloat(), heightRect, bluePaint)
        canvas?.drawPath(pathRect, bluePaint)

        val t = (ivPost.x + ivPost.width / 2f) / width
        val r = (ivPost.x - 30f) / width
        val l = (ivPost.x + ivPost.width + 30f) / width
        drawShape(t, r, l, canvas)

    }

    fun setProgress(progress: Float) {
        Py1 = heightRect * (1 + progress)
        quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)
        invalidate()
    }

    fun select(position: Int) {
        val currentView = listView.find { it.select }
        val selectView = listView.find { it.position == position }

        if (currentView?.position == selectView?.position) return

        currentView?.select = false
        selectView?.select = true

        selectView?.let { select ->
            AdditiveAnimator.animate(select.view).apply {
                translationY(heightRect - select.view.height)
                start()
            }
        }
    }

    private fun drawShape(t: Float, r: Float, l: Float, canvas: Canvas?) {
        val k = 30f//
        val p = 30f//

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

        val ro = sqrt((xc - xd) * (xc - xd) + (yc - yd) * (yc - yd)) / 2

        val xm = (xa + xd) / 2 - k
        val ym = (ya + yd) / 2 - k

        val xn = (xb + xc) / 2 + k
        val yn = (yb + yc) / 2 - k

        pathFigur.reset()
        pathFigur.moveTo(xa, ya)
        pathFigur.quadTo(xm, ym, xd, yd)
        pathFigur.lineTo(xb, yb)
        pathFigur.quadTo(xn, yn, xc, yc)

        pathCircle.addOval(x - ro, y - ro - 10f, x + ro, y + ro - 10f, Path.Direction.CCW)

        //рисуем круг
        //canvas?.drawPath(pathCircle, whitePaint)
        //рисуем трапецию
        //canvas?.drawPath(pathFigur, whitePaint)

    }

    private fun initPaint() {

        bluePaint.apply {
            color = ContextCompat.getColor(context, R.color.blue)
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        whitePaint.apply {
            color = Color.WHITE
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
    }

    private fun initCoordinate() {

        viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Px1 = width / 2f
                Py1 = heightRect
                Px2 = width.toFloat()
                Py2 = heightRect
                Py0 = heightRect
                heightShape = ivUsers.height.toFloat() + resources.getDimension(R.dimen.margin_normal) + 20f
                quad = Quad(Px0, Py0, Px1, Py1, Px2, Py2)

                select(0)

                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

    }

    private fun initView() {
        listView.add(SelectView(0, ivPost, false))
        listView.add(SelectView(1, ivUsers, false))
        listView.add(SelectView(2, ivTags, false))
        listView.add(SelectView(3, ivPlace, false))
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

    data class SelectView(val position: Int, val view: View, var select: Boolean)

}