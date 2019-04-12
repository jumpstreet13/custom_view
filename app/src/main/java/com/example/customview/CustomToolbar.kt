package com.example.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import kotlin.math.*

class CustomToolbar : FrameLayout {

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pathRectBlue = Path()
    private val pathRectWhite = Path()
    private val pathTr = Path()

    private val listView = mutableListOf<SelectView>()

    private val heightRect = resources.getDimension(R.dimen.toolbar_size)
    private val delta = resources.getDimension(R.dimen.delta)

    private lateinit var quadTop: Quad
    private lateinit var quadBottom: Quad

    private var progress = 0f

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
        initPaint()
        initView()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        with(quadTop) {
            Px0 = 0f
            Py0 = heightRect
            Px1 = width / 2f
            Px2 = width * 1f
            Py2 = heightRect
        }

        with(quadBottom) {
            Px0 = 0f
            Py0 = heightRect + delta
            Px1 = width / 2f
            Px2 = width * 1f
            Py2 = heightRect + delta
        }

        listView.forEachIndexed { index, view ->
            val defaultX = width * (index + 1) / (listView.size + 1f) - view.view.width
            val defaultY = abs(heightRect - view.view.height) / 2
            view.view.x = defaultX
            view.view.y = defaultY
            view.defaultX = defaultX
            view.defaultY = defaultY
        }

        select(1)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        with(pathRectBlue) {
            reset()
            moveTo(quadTop.Px0, quadTop.Py0)
            quadTo(quadTop.Px1, quadTop.Py1, quadTop.Px2, quadTop.Py2)
        }

        with(pathRectWhite) {
            reset()
            moveTo(quadTop.Px0, quadTop.Py0)
            quadTo(quadTop.Px1, quadTop.Py1, quadTop.Px2, quadTop.Py2)
            lineTo(quadBottom.Px2, quadBottom.Py2)
            quadTo(quadBottom.Px1, quadBottom.Py1, quadBottom.Px0, quadBottom.Py0)

        }

        canvas?.drawRect(0f, 0f, width.toFloat(), heightRect, bluePaint)
        canvas?.drawPath(pathRectBlue, bluePaint)
        canvas?.drawPath(pathRectWhite, whitePaint)

        listView.find { it.select }?.let {
            val t = (it.view.x + it.view.width / 2f) / width
            val r = (it.view.x - 60f) / width
            val l = (it.view.x + it.view.width + 60f) / width
            drawShape(t, r, l, 60f, canvas)
        }

    }

    fun setProgress(progress: Float) {
        quadTop.Py1 = heightRect * (1 + progress)
        quadBottom.Py1 = (heightRect + delta) * (1 + progress)
        changeView()
        invalidate()
        this.progress = progress
    }

    fun addImage(@DrawableRes idRes: Int) {
        val iv = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundResource(idRes)

            }
        listView.add(SelectView(iv, 0f, 0f, false))
        addView(iv)
    }


    fun select(position: Int) {
        val currentView = listView.find { it.select }
        val selectView = listView[position]

        if (currentView == selectView) return

        currentView?.select = false
        selectView.select = true

        selectView.view.setOnClickListener {

            val hs = (heightRect + selectView.view.height) / 2
            val pathAnimSelect = Path().apply {
                moveTo(selectView.view.x, selectView.view.y)
                lineTo(selectView.view.x, hs)
            }
            ObjectAnimator.ofFloat(selectView.view, View.X, View.Y, pathAnimSelect)
                .apply {
                    duration = 500L
                    addUpdateListener { selectView.defaultY = hs }
                    start()
                }

            currentView?.let { v ->
                val hc = abs(heightRect - v.view.height) / 2
                val pathAnimCurrent = Path().apply {
                    moveTo(v.view.x, v.view.y)
                    lineTo(v.view.x, hc)
                }
                ObjectAnimator.ofFloat(v.view, View.X, View.Y, pathAnimCurrent)
                    .apply {
                        duration = 500L
                        addUpdateListener { v.defaultY = hc }
                        start()
                    }
            }
        }
    }

    /**
     * center - относительня величина для центра большого круга
     * left - относительня величина для низа левого маленького круга
     * right - относительня величина для низа правого маленького круга
     * canvas - на чем рисуем
     **/
    private fun drawShape(center: Float, right: Float, left: Float, R: Float, canvas: Canvas?) {
        //находим центр большого круга
        val x0 = quadTop.getX(center)
        val y0 = quadTop.getY(center)

        //находим точки пересечения  левого маленького круга и кривой безье
        val xo1 = quadTop.getX(left)
        val yo1 = quadTop.getY(left)

        //находим точки пересечения правого маленького круга и кривой безье
        val xo2 = quadTop.getX(right)
        val yo2 = quadTop.getY(right)

        //расстояние от низа левого маленького круга до центра большого
        val b = sqrt((xo1 - x0) * (xo1 - x0) + (yo1 - y0) * (yo1 - y0))
        //расстояние от края большого круга до низа маленького левого
        val e = b - R
        //радиус маленького круга левого
        val Rml = e * (1 + (e / (2 * R)))
        val cosa = (b * b + Rml * Rml - (R + Rml) * (R + Rml)) / (2 * b * Rml)
        val d = abs(y0 - yo1)
        val cosb = d / b
        val q = PI - acos(cosa) - acos(cosb)
        //координаты левого маленького круга
        val x2 = xo1 + sin(q.toFloat()) * Rml
        val y2 = yo1 - cos(q.toFloat()) * Rml

        //расстояние от низа правого маленького круга до центра большого
        val a = sqrt((xo2 - x0) * (xo2 - x0) + (yo2 - y0) * (yo2 - y0))
        val p = abs(y0 - yo2)
        //расстояние от края большого круга до низа маленького правого
        val f = a - R
        //радиус маленького круга правого
        val Rmr = f * (1 + f / (2 * R))
        val sinc = p / a
        val cosd = (a * a + (Rmr + R) * (Rmr + R) - Rmr * Rmr) / (2 * a * (Rmr + R))
        //координаты правого и центрального кругов
        val x3 = x0 - cos(asin(sinc) + acos(cosd)) * (Rmr + R)
        val y3 = y0 - sin(asin(sinc) + acos(cosd)) * (Rmr + R)

        canvas?.drawCircle(x2, y2, Rml, whitePaint)
        canvas?.drawCircle(x3, y3, Rmr, whitePaint)

        canvas?.drawCircle(x0, y0, R, whitePaint)

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

        redPaint.apply {
            color = Color.RED
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        blackPaint.apply {
            color = Color.BLACK
            strokeWidth = 3f
            style = Paint.Style.FILL
        }
    }

    private fun initView() {
        minimumHeight = resources.getDimension(R.dimen.toolbar_height).toInt()
        quadTop = Quad(0f, 0f, 0f, 0f, 0f, 0f)
        quadBottom = Quad(0f, 0f, 0f, 0f, 0f, 0f)
    }

    private fun changeView() {

        listView.forEach {
            it.view.y = it.defaultY + quadTop.getY((it.view.x + it.view.width) / width.toFloat()) - heightRect
        }

    }

    class Quad(
        var Px0: Float,
        var Py0: Float,
        var Px1: Float,
        var Py1: Float,
        var Px2: Float,
        var Py2: Float
    ) {
        fun getX(t: Float) = (1 - t) * (1 - t) * Px0 + 2 * t * (1 - t) * Px1 + t * t * Px2
        fun getY(t: Float) = (1 - t) * (1 - t) * Py0 + 2 * t * (1 - t) * Py1 + t * t * Py2
    }

    data class SelectView(
        val view: ImageView,
        var defaultX: Float,
        var defaultY: Float,
        var select: Boolean
    )

}