package com.example.customview

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.TouchDelegate
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
    private val pinkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val pathRectBlue = Path()
    private val pathRectWhite = Path()
    private val pathTr = Path()

    private val listView = mutableListOf<SelectView>()

    private val heightRect = resources.getDimension(R.dimen.toolbar_size)
    private val delta = resources.getDimension(R.dimen.delta)
    private val margin = resources.getDimension(R.dimen.margin_extra).toInt()

    private val length = 50f//отступы от круга
    private val radiusView = 60f//радиус большого круга
    private val radiusGradient = 300f//радиус большого круга

    private val colorBackground = ContextCompat.getColor(context, R.color.blue)//цвет фона
    private val colorSelect = ContextCompat.getColor(context, R.color.pink)//цвет выделения
    private val colorLineBottom = Color.WHITE//цвет нижней линии

    private lateinit var curveBezierTop: CurveBezier
    private lateinit var curveBezierBottom: CurveBezier

    private var clickListener: OnPositionClickListener? = null

    //анимация для переключения элементов
    private var progressAnim = 0f

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

        with(curveBezierTop) {
            Px0 = 0f
            Py0 = heightRect
            Px1 = width / 2f
            Px2 = width * 1f
            Py2 = heightRect
        }

        with(curveBezierBottom) {
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

        select(0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        with(pathRectBlue) {
            reset()
            moveTo(curveBezierTop.Px0, curveBezierTop.Py0)
            quadTo(curveBezierTop.Px1, curveBezierTop.Py1, curveBezierTop.Px2, curveBezierTop.Py2)
        }

        with(pathRectWhite) {
            reset()
            moveTo(curveBezierTop.Px0, curveBezierTop.Py0)
            quadTo(curveBezierTop.Px1, curveBezierTop.Py1, curveBezierTop.Px2, curveBezierTop.Py2)
            lineTo(curveBezierBottom.Px2, curveBezierBottom.Py2)
            quadTo(curveBezierBottom.Px1, curveBezierBottom.Py1, curveBezierBottom.Px0, curveBezierBottom.Py0)

        }

        canvas?.drawRect(0f, 0f, width.toFloat(), heightRect, bluePaint)
        canvas?.drawPath(pathRectBlue, bluePaint)

        listView.find { it.select }?.let { drawShape(it.view, radiusView, canvas) }

    }

    fun setProgress(progress: Float) {
        curveBezierTop.Py1 = heightRect * (1 + progress)
        curveBezierBottom.Py1 = heightRect * (1 + progress) + delta
        changeView()
        invalidate()
    }

    fun addImage(@DrawableRes idRes: Int) {
        val iv = ImageView(context)
            .apply {
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setImageResource(idRes)
                drawable.setTint(colorLineBottom)
                this@CustomToolbar.touchDelegate =
                    TouchDelegate(
                        Rect(
                            top - margin,
                            left - margin,
                            bottom + margin,
                            right + margin
                        ), this
                    )
            }
        listView.add(SelectView(iv, 0f, 0f, false))
        listView.forEachIndexed { i, s ->
            s.view.setOnClickListener {
                select(i)
                clickListener?.onClickPosition(i, s.view)
            }
        }
        addView(iv)
        invalidate()
    }

    fun setOnPositionClickListener(listener: OnPositionClickListener) {
        this.clickListener = listener
    }


    fun select(position: Int) {
        val currentView = listView.find { it.select }
        val selectView = listView[position]

        if (currentView == selectView) return

        currentView?.select = false
        selectView.select = true

        val hs = (heightRect + selectView.view.height) / 2
        val dhs = curveBezierTop.getY((selectView.view.x + selectView.view.width) / width) - curveBezierTop.Py0
        val pathAnimSelect = Path().apply {
            moveTo(selectView.view.x, selectView.view.y)
            lineTo(selectView.view.x, hs + dhs)
        }

        ObjectAnimator.ofFloat(selectView.view, View.X, View.Y, pathAnimSelect)
            .apply {
                duration = 400L
                addUpdateListener { selectView.defaultY = hs }
                start()
            }

        ObjectAnimator.ofFloat(0f, 1f)
            .apply {
                duration = 400L
                addUpdateListener {
                    progressAnim = it.animatedValue as Float
                    invalidate()
                }
                start()
            }

        ObjectAnimator.ofObject(ArgbEvaluator(), colorLineBottom, colorSelect)
            .apply {
                duration = 400L
                addUpdateListener {
                    val colorValue = it.animatedValue as Int
                    selectView.view.drawable.setTint(colorValue)
                }
                start()
            }

        currentView?.let { v ->
            val hc = abs(heightRect - v.view.height) / 2
            val dhc = curveBezierTop.getY((v.view.x + v.view.width) / width) - curveBezierTop.Py0
            val pathAnimCurrent = Path().apply {
                moveTo(v.view.x, v.view.y)
                lineTo(v.view.x, hc + dhc)
            }
            ObjectAnimator.ofFloat(v.view, View.X, View.Y, pathAnimCurrent)
                .apply {
                    duration = 400L
                    addUpdateListener { v.defaultY = hc }
                    start()
                }

            ObjectAnimator.ofObject(ArgbEvaluator(), colorSelect, colorLineBottom)
                .apply {
                    duration = 400L
                    addUpdateListener {
                        val colorValue = it.animatedValue as Int
                        v.view.drawable.setTint(colorValue)
                    }
                    start()
                }
        }
    }

    private fun drawShape(view: View, R: Float, canvas: Canvas?) {

        //относительные величины
        val center = (view.x + view.width / 2f) / width
        val right = (view.x - length) / width
        val left = (view.x + view.width + length) / width

        val rh = (view.x + view.width / 2 + radiusGradient) / width
        val lh = (view.x + view.width / 2 - radiusGradient) / width

        //коэфициент для разных сторон
        val k = if (center > 0.5f) -1 else 1

        //находим центр большого круга
        val x0 = curveBezierTop.getX(center)
        //расчитывае высоту в зависимости от прогреса анимации
        val y0 = curveBezierTop.getY(center) + (1 - progressAnim) * radiusView

        //находим точки пересечения  левого маленького круга и кривой безье
        val xo1 = curveBezierTop.getX(left)
        //расчитывае высоту в зависимости от прогреса анимации
        val yo1 = curveBezierTop.getY(left) + (1 - progressAnim) * radiusView

        //находим точки пересечения правого маленького круга и кривой безье
        val xo2 = curveBezierTop.getX(right)
        //расчитывае высоту в зависимости от прогреса анимации
        val yo2 = curveBezierTop.getY(right) + (1 - progressAnim) * radiusView

        //точки для пересечения градиетна и кривой безье
        val xlh = curveBezierTop.getX(lh)
        val ylh = curveBezierTop.getY(lh)
        val xrh = curveBezierTop.getX(rh)
        val yrh = curveBezierTop.getY(rh)

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
        val x2 = xo1 + k * sin(q.toFloat()) * Rml
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
        //координаты правого маленького круга
        val x3 = x0 - cos(asin(sinc) + k * acos(cosd)) * (Rmr + R)
        val y3 = y0 - k * sin(asin(sinc) + k * acos(cosd)) * (Rmr + R)

        val xdl = x0 - x2
        val ydl = y0 - y2
        val s = (Rml * Rml - R * R - xdl * xdl - ydl * ydl) / (2 * xdl)
        val z = ydl / xdl
        //точки пересечения правого и центрального кругов
        val yt = s * z / (1 + z * z)
        val xt = (Rml * Rml - R * R - xdl * xdl - 2 * ydl * yt - ydl * ydl) / (2 * xdl)

        val xdr = x0 - x3
        val ydr = y0 - y3
        val u = (Rmr * Rmr - R * R - xdr * xdr - ydr * ydr) / (2 * xdr)
        val j = ydr / xdr
        //точки пересечения левого и центрального кругов
        val yk = u * j / (1 + j * j)
        val xk = (Rmr * Rmr - R * R - xdr * xdr - 2 * ydr * yk - ydr * ydr) / (2 * xdr)

        val ac = sqrt((x0 - xlh) * (x0 - xlh) + (y0 - ylh) * (y0 - ylh))
        val bc = sqrt((x0 - xrh) * (x0 - xrh) + (y0 - yrh) * (y0 - yrh))

        val sins = (y0 - ylh) / ac
        //откуда начинает рисоваться градиент
        val aos = 180f * (1 + asin(sins) / PI.toFloat()) - 5f

        val sinb = (yrh - y0) / bc
        //на сколько градусов рисуется градиент
        val aoc = 180 - asin(sins) / PI.toFloat() * 180 + asin(sinb) / PI.toFloat() * 180 + 10f

        //строим фигуру для прикрывания цвета
        pathTr.reset()
        pathTr.moveTo(x0 + xt, y0 + yt)
        pathTr.lineTo(x0 + xk, y0 + yk)
        pathTr.lineTo(xo2, yo2)
        pathTr.lineTo(xo2, if (center > 0.5f) 2 * yo2 - y0 else 2 * y0 - yo2)
        pathTr.lineTo(xo1, if (center > 0.5f) 2 * y0 - yo1 else 2 * yo1 - y0)
        pathTr.lineTo(xo1, yo1)

        //шаблон градиента
        gradientPaint.shader = RadialGradient(
            x0,
            y0,
            radiusGradient,
            colorSelect,
            colorBackground,
            android.graphics.Shader.TileMode.CLAMP
        )

        //рисуем градиент
        canvas?.drawArc(
            x0 - radiusGradient * progressAnim,
            y0 - radiusGradient * progressAnim,
            x0 + radiusGradient * progressAnim,
            y0 + radiusGradient * progressAnim,
            aos,
            aoc,
            true,
            gradientPaint
        )


        //рисуем фигуру для цвета в зависимости от прогреса анимации
        if (progressAnim > 0.5) {
            canvas?.drawPath(pathTr, whitePaint)
        }
        //рисуем левый круг
        canvas?.drawCircle(x2, y2, Rml, gradientPaint)
        //русуем правый круг
        canvas?.drawCircle(x3, y3, Rmr, gradientPaint)
        //рисуем центральный полукруг
        canvas?.drawArc(
            x0 - R,
            y0 - R,
            x0 + R,
            y0 + R,
            aos,
            aoc,
            true,
            whitePaint
        )
        //рисуем дугу
        canvas?.drawPath(pathRectWhite, whitePaint)

    }

    private fun initPaint() {

        bluePaint.apply {
            color = ContextCompat.getColor(context, R.color.blue)
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        whitePaint.apply {
            color = colorLineBottom
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        redPaint.apply {
            color = Color.RED
            strokeWidth = 3f
            style = Paint.Style.FILL
        }

        pinkPaint.apply {
            color = colorSelect
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
        curveBezierTop = CurveBezier(0f, 0f, 0f, 0f, 0f, 0f)
        curveBezierBottom = CurveBezier(0f, 0f, 0f, 0f, 0f, 0f)
    }

    private fun changeView() {

        listView.forEach {
            it.view.y = it.defaultY + curveBezierTop.getY((it.view.x + it.view.width) / width.toFloat()) - heightRect
        }

    }

    class CurveBezier(
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