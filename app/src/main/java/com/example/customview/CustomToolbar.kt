package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import at.wirecube.additiveanimations.additive_animator.AdditiveAnimator
import kotlin.math.abs
import kotlin.math.sqrt

class CustomToolbar : FrameLayout {

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bluePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val redPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pathRect = Path()
    private val pathFigur = Path()
    private val pathCircle = Path()

    private val listView = mutableListOf<SelectView>()

    private val heightRect = resources.getDimension(R.dimen.toolbar_size)
    private var heightShape = 0f

    private lateinit var quad: Quad

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

        with(quad) {
            Px0 = 0f
            Py0 = heightRect
            Px1 = width / 2f
            Px2 = width * 1f
            Py2 = heightRect
        }

        listView.forEachIndexed { index, view ->
            val defaultX = width * (index + 1) / (listView.size + 1f) - view.view.width
            val defaultY = abs(heightRect - view.view.height) / 2
            view.view.x = defaultX
            view.view.y = defaultY
            view.defaultX = defaultX
            view.defaultY = defaultY
        }

        select(0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        pathRect.reset()
        pathRect.moveTo(quad.Px0, quad.Py0)
        pathRect.quadTo(quad.Px1, quad.Py1, quad.Px2, quad.Py2)

        canvas?.drawRect(0f, 0f, width.toFloat(), heightRect, bluePaint)
        canvas?.drawPath(pathRect, bluePaint)

        /*val t = (ivPost.x + ivPost.width / 2f) / width
        val r = (ivPost.x - 30f) / width
        val l = (ivPost.x + ivPost.width + 30f) / width
        drawShape(t, r, l, canvas)*/

    }

    fun setProgress(progress: Float) {
        quad.Py1 = heightRect * (1 + progress)
        changeView()
        invalidate()
    }

    fun select(position: Int) {
        val currentView = listView.find { it.select }
        val selectView = listView[position]

        if (currentView == selectView) return

        currentView?.select = false
        selectView.select = true

        AdditiveAnimator.animate(selectView.view).apply {
            translationY(heightRect - selectView.view.height)
            start()
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
        canvas?.drawPath(pathCircle, whitePaint)
        //рисуем трапецию
        canvas?.drawPath(pathFigur, whitePaint)

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
        val ivPost = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundResource(R.drawable.ic_post)
            }

        val ivUsers = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundResource(R.drawable.ic_users)
            }

        val ivTags = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundResource(R.drawable.ic_tags)
            }

        val ivPlace = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundResource(R.drawable.ic_place)
            }

        minimumHeight = resources.getDimension(R.dimen.toolbar_height).toInt()


        quad = Quad(0f, 0f, 0f, 0f, 0f, 0f)

        listView.add(SelectView(ivPost, 0f, 0f, false))
        listView.add(SelectView(ivUsers, 0f, 0f, false))
        listView.add(SelectView(ivTags, 0f, 0f, false))
        listView.add(SelectView(ivPlace, 0f, 0f, false))

        listView.forEach { v -> addView(v.view) }

    }

    private fun changeView() {

        listView.forEach {
            it.view.y = it.defaultY + quad.getY((it.view.x + it.view.width) / width.toFloat()) - heightRect
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